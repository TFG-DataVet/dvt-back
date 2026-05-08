package com.datavet.auth.application.service;

import com.datavet.auth.application.port.in.command.RegisterClinicOwnerCommand;
import com.datavet.auth.application.port.out.EmailPort;
import com.datavet.auth.application.port.out.RefreshTokenRepositoryPort;
import com.datavet.auth.application.port.out.UserRepositoryPort;
import com.datavet.auth.domain.exception.UserAlreadyExistsException;
import com.datavet.auth.domain.model.User;
import com.datavet.auth.domain.model.UserRole;
import com.datavet.auth.domain.model.UserStatus;
import com.datavet.auth.domain.valueobject.HashedPassword;
import com.datavet.auth.infrastructure.util.JwtUtil;
import com.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.clinic.application.port.in.command.CreatePendingClinicCommand;
import com.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.clinic.domain.model.ClinicStatus;
import com.datavet.employee.application.port.out.EmployeeRepositoryPort;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - registerClinicOwner Tests")
class AuthServiceRegisterTest {

    private AuthService authService;

    @Mock private UserRepositoryPort         userRepositoryPort;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    @Mock private ClinicUseCase              clinicUseCase;
    @Mock private EmailPort                  emailPort;
    @Mock private PasswordEncoder            passwordEncoder;
    @Mock private JwtUtil                    jwtUtil;
    @Mock private DomainEventPublisher       domainEventPublisher;
    @Mock private EmployeeRepositoryPort     employeeRepositoryPort;
    @Mock private ClinicRepositoryPort       clinicRepositoryPort;

    private static final String VALID_BCRYPT = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepositoryPort, refreshTokenRepositoryPort,
                clinicUseCase, emailPort, passwordEncoder,
                jwtUtil, domainEventPublisher,
                employeeRepositoryPort, clinicRepositoryPort
        );
    }

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("Should register clinic owner and save user")
    void register_WhenNoConflicts_ShouldSaveUser() {
        Clinic pendingClinic = buildPendingClinic();

        when(userRepositoryPort.existsByEmail("owner@clinic.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_BCRYPT);
        when(clinicUseCase.createPendingClinic(any(CreatePendingClinicCommand.class))).thenReturn(pendingClinic);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = authService.registerClinicOwner(buildCommand());

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING_EMAIL_VERIFICATION);
        assertThat(result.getRole()).isEqualTo(UserRole.CLINIC_OWNER);
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    @DisplayName("Should send verification email after registration")
    void register_ShouldSendVerificationEmail() {
        Clinic pendingClinic = buildPendingClinic();

        when(userRepositoryPort.existsByEmail("owner@clinic.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_BCRYPT);
        when(clinicUseCase.createPendingClinic(any())).thenReturn(pendingClinic);
        when(userRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        authService.registerClinicOwner(buildCommand());

        verify(emailPort).sendVerificationEmail(anyString(), anyString(), eq("owner@clinic.com"), anyString());
    }

    @Test
    @DisplayName("Should publish domain events after registration")
    void register_ShouldPublishDomainEvents() {
        Clinic pendingClinic = buildPendingClinic();

        when(userRepositoryPort.existsByEmail("owner@clinic.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_BCRYPT);
        when(clinicUseCase.createPendingClinic(any())).thenReturn(pendingClinic);
        when(userRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        authService.registerClinicOwner(buildCommand());

        verify(domainEventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    @DisplayName("Should set correct firstName and lastName on the user")
    void register_ShouldSetFirstNameAndLastName() {
        Clinic pendingClinic = buildPendingClinic();

        when(userRepositoryPort.existsByEmail("owner@clinic.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(VALID_BCRYPT);
        when(clinicUseCase.createPendingClinic(any())).thenReturn(pendingClinic);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        authService.registerClinicOwner(buildCommand());

        assertThat(captor.getValue().getFirstName()).isEqualTo("Juan");
        assertThat(captor.getValue().getLastName()).isEqualTo("García");
    }

    // =========================================================================
    // Excepciones
    // =========================================================================

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void register_WhenEmailExists_ShouldThrow() {
        when(userRepositoryPort.existsByEmail("owner@clinic.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerClinicOwner(buildCommand()))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepositoryPort, never()).save(any());
        verify(emailPort, never()).sendVerificationEmail(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should not create pending clinic if email check fails")
    void register_WhenEmailExists_ShouldNotCreateClinic() {
        when(userRepositoryPort.existsByEmail("owner@clinic.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerClinicOwner(buildCommand()))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(clinicUseCase, never()).createPendingClinic(any());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private RegisterClinicOwnerCommand buildCommand() {
        return RegisterClinicOwnerCommand.builder()
                .clinicName("Clínica Test")
                .firstName("Juan")
                .lastName("García")
                .email("owner@clinic.com")
                .rawPassword("Password1")
                .phone("+34912345678")
                .build();
    }

    private Clinic buildPendingClinic() {
        return Clinic.reconstitute(
                "clinic-uuid", "Clínica Test",
                null, null, null, null,
                new Phone("+34912345678"),
                new Email("owner@clinic.com"),
                null, null,
                ClinicStatus.PENDING_SETUP,
                LocalDateTime.now(), null
        );
    }
}
