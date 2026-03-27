package com.datavet.auth.domain.model;

import com.datavet.auth.domain.event.UserCreatedEvent;
import com.datavet.auth.domain.event.UserDeactivatedEvent;
import com.datavet.auth.domain.event.UserEmailVerifiedEvent;
import com.datavet.auth.domain.exception.EmailTokenExpiredException;
import com.datavet.auth.domain.exception.InvalidCredentialsException;
import com.datavet.auth.domain.exception.UserValidationException;
import com.datavet.auth.domain.valueobject.HashedPassword;
import com.datavet.shared.domain.model.AggregateRoot;
import com.datavet.shared.domain.model.Document;
import com.datavet.shared.domain.validation.ValidationResult;
import com.datavet.shared.domain.valueobject.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends AggregateRoot<String> implements Document<String> {

    private String         id;
    private String         employeeId;
    private String         clinicId;
    private Email          email;
    private HashedPassword password;
    private UserRole       role;
    private UserStatus     status;

    /**
     * Nombre y apellido temporales del CLINIC_OWNER durante el onboarding.
     * Una vez creado el Employee en el paso 3, estos campos pierden relevancia
     * pero se conservan como referencia histórica.
     */
    private String firstName;
    private String lastName;

    private String        emailVerificationToken;
    private LocalDateTime emailVerificationExpiry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String getId() {
        return this.id;
    }

    // -------------------------------------------------------------------------
    // Validación privada
    // -------------------------------------------------------------------------

    private void validate() {
        ValidationResult result = new ValidationResult();

        if (email == null) {
            result.addError("User", "El email no puede ser nulo");
        }

        if (password == null) {
            result.addError("User", "La contraseña no puede ser nula");
        }

        if (role == null) {
            result.addError("User", "El rol no puede ser nulo");
        }

        if (status == null) {
            result.addError("User", "El estado no puede ser nulo");
        }

        if (role != null && role != UserRole.SUPER_ADMIN && clinicId == null) {
            result.addError("User", "El clinicId es obligatorio para roles de clínica");
        }

        // CLINIC_OWNER en onboarding necesita nombre y apellido
        if (role != null && role == UserRole.CLINIC_OWNER
                && status == UserStatus.PENDING_EMAIL_VERIFICATION) {
            if (firstName == null || firstName.isBlank()) {
                result.addError("User", "El nombre es obligatorio para el registro");
            }
            if (lastName == null || lastName.isBlank()) {
                result.addError("User", "El apellido es obligatorio para el registro");
            }
        }

        if (result.hasErrors()) {
            throw new UserValidationException(result);
        }
    }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    /**
     * Crea el User del CLINIC_OWNER en el paso 1 del onboarding.
     * Guarda firstName y lastName para usarlos al crear el Employee en el paso 3.
     */
    public static User createClinicOwner(String clinicId, Email email,
                                         HashedPassword password,
                                         String firstName, String lastName,
                                         String emailVerificationToken) {
        String uuid = UUID.randomUUID().toString();

        User user = new User(
                uuid,
                null,           // employeeId — se asigna en el paso 3
                clinicId,
                email,
                password,
                UserRole.CLINIC_OWNER,
                UserStatus.PENDING_EMAIL_VERIFICATION,
                firstName,
                lastName,
                emailVerificationToken,
                LocalDateTime.now().plusHours(24),
                LocalDateTime.now(),
                null
        );

        user.validate();
        user.addDomainEvent(
                UserCreatedEvent.of(uuid, email.getValue(), UserRole.CLINIC_OWNER, clinicId));
        return user;
    }

    /**
     * Crea un User para un empleado de clínica (VET, ADMIN, STAFF).
     * Nace directamente en estado ACTIVE.
     */
    public static User createEmployee(String clinicId, String employeeId,
                                      Email email, HashedPassword password,
                                      UserRole role) {
        if (role == UserRole.SUPER_ADMIN || role == UserRole.CLINIC_OWNER) {
            throw new InvalidCredentialsException(
                    "Este método solo puede crear usuarios de tipo empleado");
        }

        String uuid = UUID.randomUUID().toString();

        User user = new User(
                uuid,
                employeeId,
                clinicId,
                email,
                password,
                role,
                UserStatus.ACTIVE,
                null,   // firstName — no aplica para empleados
                null,   // lastName  — no aplica para empleados
                null,
                null,
                LocalDateTime.now(),
                null
        );

        user.validate();
        user.addDomainEvent(
                UserCreatedEvent.of(uuid, email.getValue(), role, clinicId));
        return user;
    }

    /**
     * Reconstituye un User desde persistencia.
     */
    public static User reconstitute(String id, String employeeId, String clinicId,
                                    Email email, HashedPassword password,
                                    UserRole role, UserStatus status,
                                    String firstName, String lastName,
                                    String emailVerificationToken,
                                    LocalDateTime emailVerificationExpiry,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {
        return new User(id, employeeId, clinicId, email, password, role, status,
                firstName, lastName, emailVerificationToken,
                emailVerificationExpiry, createdAt, updatedAt);
    }

    // -------------------------------------------------------------------------
    // Métodos de dominio
    // -------------------------------------------------------------------------

    public void verifyEmail(String token) {
        if (this.status != UserStatus.PENDING_EMAIL_VERIFICATION) {
            throw new InvalidCredentialsException(
                    "Este usuario no está pendiente de verificación de email");
        }

        if (this.emailVerificationToken == null
                || !this.emailVerificationToken.equals(token)) {
            throw new EmailTokenExpiredException();
        }

        if (LocalDateTime.now().isAfter(this.emailVerificationExpiry)) {
            throw new EmailTokenExpiredException();
        }

        this.status                  = UserStatus.PENDING_CLINIC_SETUP;
        this.emailVerificationToken  = null;
        this.emailVerificationExpiry = null;
        this.updatedAt               = LocalDateTime.now();

        addDomainEvent(UserEmailVerifiedEvent.of(this.id, this.email.getValue()));
    }

    public void activate(String employeeId) {
        if (this.status != UserStatus.PENDING_CLINIC_SETUP) {
            throw new InvalidCredentialsException(
                    "El usuario no está en estado PENDING_CLINIC_SETUP");
        }

        this.employeeId = employeeId;
        this.status     = UserStatus.ACTIVE;
        this.updatedAt  = LocalDateTime.now();
    }

    public void changePassword(HashedPassword newPassword) {
        if (this.status == UserStatus.INACTIVE) {
            throw new InvalidCredentialsException(
                    "No se puede cambiar la contraseña de un usuario inactivo");
        }

        this.password  = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate(String reason) {
        if (this.status == UserStatus.INACTIVE) {
            throw new InvalidCredentialsException("El usuario ya está inactivo");
        }

        this.status    = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(UserDeactivatedEvent.of(this.id, this.email.getValue(), reason));
    }

    public boolean canLogin() {
        return this.status == UserStatus.ACTIVE;
    }
}