package com.datavet.auth.application.service;

import com.datavet.auth.application.dto.TokenResponse;
import com.datavet.auth.application.port.in.command.LoginCommand;
import com.datavet.auth.application.port.out.EmailPort;
import com.datavet.auth.application.port.out.RefreshTokenRepositoryPort;
import com.datavet.auth.application.port.out.UserRepositoryPort;
import com.datavet.auth.domain.exception.InvalidCredentialsException;
import com.datavet.auth.domain.model.User;
import com.datavet.auth.domain.model.UserRole;
import com.datavet.auth.domain.model.UserStatus;
import com.datavet.auth.domain.valueobject.HashedPassword;
import com.datavet.auth.infrastructure.persistence.document.RefreshTokenDocument;
import com.datavet.auth.infrastructure.util.JwtUtil;
import com.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.employee.application.port.out.EmployeeRepositoryPort;
import com.datavet.shared.domain.event.DomainEventPublisher;
import com.datavet.shared.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - login Tests")
class AuthServiceLoginTest {

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
    @DisplayName("login: should return TokenResponse for active user with correct password")
    void login_WithValidCredentials_ShouldReturnTokenResponse() {
        User activeUser = buildActiveUser();

        when(userRepositoryPort.findByEmail("user@clinic.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("Password1", VALID_BCRYPT)).thenReturn(true);
        when(jwtUtil.generateAccessToken(any(), any(), any(), any(), any())).thenReturn("jwt-token");
        when(jwtUtil.getAccessTokenExpirationSeconds()).thenReturn(3600L);
        when(refreshTokenRepositoryPort.save(any(RefreshTokenDocument.class))).thenAnswer(i -> i.getArgument(0));

        TokenResponse result = authService.login(LoginCommand.builder()
                .email("user@clinic.com")
                .rawPassword("Password1")
                .build());

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("jwt-token");
        verify(refreshTokenRepositoryPort).save(any(RefreshTokenDocument.class));
    }

    @Test
    @DisplayName("login: should return onboarding token for PENDING_CLINIC_SETUP user")
    void login_WhenPendingClinicSetup_ShouldReturnOnboardingToken() {
        User pendingUser = buildPendingClinicSetupUser();

        when(userRepositoryPort.findByEmail("owner@clinic.com")).thenReturn(Optional.of(pendingUser));
        when(passwordEncoder.matches("Password1", VALID_BCRYPT)).thenReturn(true);
        when(jwtUtil.generateOnboardingToken(any(), any(), any(), any())).thenReturn("onboarding-jwt");

        TokenResponse result = authService.login(LoginCommand.builder()
                .email("owner@clinic.com")
                .rawPassword("Password1")
                .build());

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("onboarding-jwt");
        assertThat(result.getNextStep()).isEqualTo("COMPLETE_SETUP");
        verify(refreshTokenRepositoryPort, never()).save(any());
    }

    // =========================================================================
    // Excepciones
    // =========================================================================

    @Test
    @DisplayName("login: should throw when email not found")
    void login_WhenEmailNotFound_ShouldThrow() {
        when(userRepositoryPort.findByEmail("notfound@clinic.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(LoginCommand.builder()
                .email("notfound@clinic.com")
                .rawPassword("Password1")
                .build()))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("login: should throw when password does not match")
    void login_WhenWrongPassword_ShouldThrow() {
        User activeUser = buildActiveUser();

        when(userRepositoryPort.findByEmail("user@clinic.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("WrongPass1", VALID_BCRYPT)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(LoginCommand.builder()
                .email("user@clinic.com")
                .rawPassword("WrongPass1")
                .build()))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("login: should throw when user is inactive")
    void login_WhenUserInactive_ShouldThrow() {
        User inactiveUser = buildInactiveUser();

        when(userRepositoryPort.findByEmail("user@clinic.com")).thenReturn(Optional.of(inactiveUser));
        when(passwordEncoder.matches("Password1", VALID_BCRYPT)).thenReturn(true);

        assertThatThrownBy(() -> authService.login(LoginCommand.builder()
                .email("user@clinic.com")
                .rawPassword("Password1")
                .build()))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private User buildActiveUser() {
        return User.reconstitute(
                "user-id", "emp-id", "clinic-id",
                new Email("user@clinic.com"),
                HashedPassword.ofHash(VALID_BCRYPT),
                UserRole.CLINIC_VETERINARIAN, UserStatus.ACTIVE,
                null, null, null, null, null, null,
                LocalDateTime.now(), null
        );
    }

    private User buildPendingClinicSetupUser() {
        return User.reconstitute(
                "owner-id", null, "clinic-id",
                new Email("owner@clinic.com"),
                HashedPassword.ofHash(VALID_BCRYPT),
                UserRole.CLINIC_OWNER, UserStatus.PENDING_CLINIC_SETUP,
                "Juan", "García", null, null, null, null,
                LocalDateTime.now(), null
        );
    }

    private User buildInactiveUser() {
        return User.reconstitute(
                "user-id", "emp-id", "clinic-id",
                new Email("user@clinic.com"),
                HashedPassword.ofHash(VALID_BCRYPT),
                UserRole.CLINIC_VETERINARIAN, UserStatus.INACTIVE,
                null, null, null, null, null, null,
                LocalDateTime.now(), null
        );
    }
}
