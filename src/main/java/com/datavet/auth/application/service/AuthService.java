package com.datavet.auth.application.service;

import com.datavet.auth.application.port.in.command.*;
import com.datavet.auth.application.dto.TokenResponse;
import com.datavet.auth.application.port.in.AuthUseCase;
import com.datavet.auth.application.port.in.command.LoginCommand;
import com.datavet.auth.application.port.in.command.RegisterClinicOwnerCommand;
import com.datavet.auth.application.port.in.command.VerifyEmailCommand;
import com.datavet.auth.application.port.out.EmailPort;
import com.datavet.auth.application.port.out.RefreshTokenRepositoryPort;
import com.datavet.auth.application.port.out.UserRepositoryPort;
import com.datavet.auth.domain.exception.EmailTokenExpiredException;
import com.datavet.auth.domain.exception.InvalidCredentialsException;
import com.datavet.auth.domain.exception.UserAlreadyExistsException;
import com.datavet.auth.domain.exception.UserNotFoundException;
import com.datavet.auth.domain.model.User;
import com.datavet.auth.domain.model.UserRole;
import com.datavet.auth.domain.model.UserStatus;
import com.datavet.auth.domain.valueobject.HashedPassword;
import com.datavet.auth.infrastructure.persistence.document.RefreshTokenDocument;
import com.datavet.auth.infrastructure.util.JwtUtil;
import com.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.clinic.application.port.in.command.CompleteClinicSetupCommand;
import com.datavet.clinic.application.port.in.command.CreatePendingClinicCommand;
import com.datavet.employee.application.port.in.EmployeeUseCase;
import com.datavet.employee.application.port.in.command.CreateEmployeeCommand;
import com.datavet.employee.domain.model.Employee;
import com.datavet.shared.application.service.ApplicationService;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements AuthUseCase, ApplicationService {

    private final UserRepositoryPort         userRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final ClinicUseCase              clinicUseCase;
    private final EmailPort                  emailPort;
    private final PasswordEncoder            passwordEncoder;
    private final JwtUtil                    jwtUtil;
    private final DomainEventPublisher       domainEventPublisher;
    // Añadir al constructor de AuthService
    private final EmployeeUseCase employeeUseCase;

    // -------------------------------------------------------------------------
    // Onboarding — Paso 1
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public User registerClinicOwner(RegisterClinicOwnerCommand command) {
        Email email = new Email(command.getEmail());
        Phone phone = new Phone(command.getPhone());

        if (userRepositoryPort.existsByEmail(email.getValue())) {
            throw new UserAlreadyExistsException("email", command.getEmail());
        }

        HashedPassword.validateRawPassword(command.getRawPassword());
        HashedPassword password = HashedPassword.ofHash(
                passwordEncoder.encode(command.getRawPassword()));

        var clinic = clinicUseCase.createPendingClinic(
                CreatePendingClinicCommand.builder()
                        .clinicName(command.getClinicName())
                        .email(email)
                        .phone(phone)
                        .build()
        );

        String verificationToken = UUID.randomUUID().toString();

        // ✅ Ahora pasamos firstName y lastName
        User user = User.createClinicOwner(
                clinic.getClinicID(),
                email,
                password,
                command.getFirstName(),
                command.getLastName(),
                verificationToken
        );

        publishDomainEvents(user);
        User savedUser = userRepositoryPort.save(user);

        emailPort.sendVerificationEmail(command.getEmail(), verificationToken);

        return savedUser;
    }

    // -------------------------------------------------------------------------
    // Onboarding — Paso 2: verificación de email
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public User verifyEmail(VerifyEmailCommand command) {
        User user = userRepositoryPort.findByEmailVerificationToken(command.getToken())
                .orElseThrow(EmailTokenExpiredException::new);

        // El dominio valida que el token no haya expirado y transiciona el estado
        user.verifyEmail(command.getToken());

        publishDomainEvents(user);
        return userRepositoryPort.save(user);
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public TokenResponse login(LoginCommand command) {
        Email email = new Email(command.getEmail());

        User user = userRepositoryPort.findByEmail(email.getValue())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Email o contraseña incorrectos"));

        // Verificamos que el usuario pueda autenticarse
        if (!user.canLogin()) {
            throw new InvalidCredentialsException(
                    "La cuenta no está activa. Estado actual: " + user.getStatus());
        }

        // Verificamos la contraseña
        if (!passwordEncoder.matches(command.getRawPassword(),
                user.getPassword().getValue())) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }

        return generateTokenResponse(user);
    }

    // -------------------------------------------------------------------------
    // Refresh token
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        String tokenHash = hashToken(refreshToken);

        RefreshTokenDocument storedToken = refreshTokenRepositoryPort
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Refresh token inválido o expirado"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepositoryPort.deleteByTokenHash(tokenHash);
            throw new InvalidCredentialsException("Refresh token expirado");
        }

        User user = userRepositoryPort.findById(storedToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException(storedToken.getUserId()));

        if (!user.canLogin()) {
            throw new InvalidCredentialsException("La cuenta no está activa");
        }

        // Rotación del refresh token — invalidamos el anterior y generamos uno nuevo
        refreshTokenRepositoryPort.deleteByTokenHash(tokenHash);

        return generateTokenResponse(user);
    }

    // -------------------------------------------------------------------------
    // Logout
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void logout(String refreshToken) {
        String tokenHash = hashToken(refreshToken);
        refreshTokenRepositoryPort.deleteByTokenHash(tokenHash);
    }

    // -------------------------------------------------------------------------
    // Cambio de contraseña
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        User user = userRepositoryPort.findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException(command.getUserId()));

        // Verificamos la contraseña actual
        if (!passwordEncoder.matches(command.getCurrentRawPassword(),
                user.getPassword().getValue())) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta");
        }

        // Validamos y hasheamos la nueva contraseña
        HashedPassword.validateRawPassword(command.getNewRawPassword());
        HashedPassword newPassword = HashedPassword.ofHash(
                passwordEncoder.encode(command.getNewRawPassword()));

        // El dominio valida que el usuario esté activo
        user.changePassword(newPassword);

        // Invalidamos todos los refresh tokens del usuario
        // para forzar re-login en todos los dispositivos
        refreshTokenRepositoryPort.deleteByUserId(user.getId());

        userRepositoryPort.save(user);
    }

    // -------------------------------------------------------------------------
    // Creación de usuario empleado
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public User createEmployeeUser(CreateEmployeeUserCommand command) {
        Email email = new Email(command.getEmail());

        if (userRepositoryPort.existsByEmail(email.getValue())) {
            throw new UserAlreadyExistsException("email", command.getEmail());
        }

        HashedPassword.validateRawPassword(command.getRawPassword());
        HashedPassword password = HashedPassword.ofHash(
                passwordEncoder.encode(command.getRawPassword()));

        User user = User.createEmployee(
                command.getClinicId(),
                command.getEmployeeId(),
                email,
                password,
                command.getRole()
        );

        publishDomainEvents(user);
        return userRepositoryPort.save(user);
    }

    // -------------------------------------------------------------------------
    // Desactivación
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void deactivateUser(String userId, String reason) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.deactivate(reason);

        // Invalidamos todos los refresh tokens activos
        refreshTokenRepositoryPort.deleteByUserId(userId);

        publishDomainEvents(user);
        userRepositoryPort.save(user);
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    /**
     * Genera el access token y el refresh token para un usuario autenticado.
     * Persiste el refresh token hasheado en base de datos.
     */
    private TokenResponse generateTokenResponse(User user) {
        // Generamos el access token JWT
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmployeeId(),
                user.getClinicId(),
                user.getEmail().getValue(),
                user.getRole()
        );

        // Generamos el refresh token — UUID aleatorio
        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash       = hashToken(rawRefreshToken);

        // Persistimos el refresh token hasheado
        RefreshTokenDocument refreshTokenDoc = RefreshTokenDocument.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepositoryPort.save(refreshTokenDoc);

        TokenResponse.UserInfo userInfo = new TokenResponse.UserInfo(
                user.getId(),
                user.getEmployeeId(),
                user.getClinicId(),
                user.getEmail().getValue(),
                user.getRole()
        );

        return TokenResponse.of(
                accessToken,
                rawRefreshToken,
                jwtUtil.getAccessTokenExpirationSeconds(),
                userInfo
        );
    }

    /**
     * Hashea el refresh token con SHA-256 antes de persistirlo.
     * Nunca almacenamos el token en texto plano.
     */
    private String hashToken(String token) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }

    private void publishDomainEvents(User user) {
        List<DomainEvent> events = user.getDomainEvents();
        events.forEach(domainEventPublisher::publish);
        user.clearDomainEvents();
    }

    /**
     * Paso 3 del onboarding — completa los datos de la clínica,
     * crea el Employee del CLINIC_OWNER y activa el User.
     * Requiere JWT temporal con scope ONBOARDING_ONLY.
     * Devuelve JWT definitivo.
     */
    @Transactional
    public TokenResponse completeOnboarding(CompleteClinicSetupCommand command) {
        // 1. Recuperamos el User — debe estar en PENDING_CLINIC_SETUP
        User user = userRepositoryPort.findById(command.getUserId())
                .orElseThrow(() -> new UserNotFoundException(command.getUserId()));

        if (user.getStatus() != UserStatus.PENDING_CLINIC_SETUP) {
            throw new InvalidCredentialsException(
                    "El usuario no está en estado PENDING_CLINIC_SETUP");
        }

        // 2. Completamos los datos de la clínica
        clinicUseCase.completeClinicSetup(command);

        // 3. Creamos el Employee del CLINIC_OWNER con los datos disponibles
        Employee employee = employeeUseCase.createEmployee(
                CreateEmployeeCommand.builder()
                        .userId(user.getId())
                        .clinicId(command.getClinicId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .documentNumber(command.getOwnerDocumentNumber())
                        .phone(command.getPhone())
                        .address(command.getOwnerAddress())
                        .avatarUrl(command.getOwnerAvatarUrl())
                        .speciality(command.getOwnerSpeciality())
                        .licenseNumber(null)
                        .hireDate(command.getOwnerHireDate())
                        .role(UserRole.CLINIC_OWNER.name())
                        .build()
        );

        // 4. Activamos el User con el employeeId recién creado
        user.activate(employee.getId());
        publishDomainEvents(user);
        userRepositoryPort.save(user);

        // 5. Enviamos email de bienvenida
        emailPort.sendWelcomeEmail(
                user.getEmail().getValue(),
                command.getLegalName()
        );

        // 6. Generamos JWT definitivo
        return generateTokenResponse(user);
    }
}
