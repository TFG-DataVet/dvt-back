package com.datavet.auth.domain.model;

import com.datavet.auth.domain.event.UserCreatedEvent;
import com.datavet.auth.domain.event.UserDeactivatedEvent;
import com.datavet.auth.domain.event.UserEmailVerifiedEvent;
import com.datavet.auth.domain.exception.EmailTokenExpiredException;
import com.datavet.auth.domain.exception.InvalidCredentialsException;
import com.datavet.auth.domain.exception.UserValidationException;
import com.datavet.auth.domain.valueobject.HashedPassword;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User Domain Model Tests")
class UserTest {

    private static final String VALID_BCRYPT = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private Email         email;
    private HashedPassword password;

    @BeforeEach
    void setUp() {
        email    = new Email("owner@clinic.com");
        password = HashedPassword.ofHash(VALID_BCRYPT);
    }

    // =========================================================================
    // createClinicOwner
    // =========================================================================

    @Test
    @DisplayName("createClinicOwner: should set all fields correctly")
    void createClinicOwner_ShouldSetAllFields() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "token-abc");

        assertThat(user.getClinicId()).isEqualTo("clinic-1");
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(UserRole.CLINIC_OWNER);
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING_EMAIL_VERIFICATION);
        assertThat(user.getFirstName()).isEqualTo("Juan");
        assertThat(user.getLastName()).isEqualTo("García");
        assertThat(user.getEmailVerificationToken()).isEqualTo("token-abc");
    }

    @Test
    @DisplayName("createClinicOwner: should generate a non-null UUID")
    void createClinicOwner_ShouldGenerateUUID() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "token-abc");

        assertThat(user.getId()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("createClinicOwner: should set emailVerificationExpiry 24h in the future")
    void createClinicOwner_ShouldSetExpiry24HoursAhead() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "token-abc");

        assertThat(user.getEmailVerificationExpiry()).isNotNull();
        assertThat(user.getEmailVerificationExpiry()).isAfter(java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("createClinicOwner: should raise UserCreatedEvent")
    void createClinicOwner_ShouldRaiseUserCreatedEvent() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "token-abc");

        List<DomainEvent> events = user.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(UserCreatedEvent.class);
    }

    @Test
    @DisplayName("createClinicOwner: should throw when firstName is blank")
    void createClinicOwner_WhenFirstNameBlank_ShouldThrow() {
        assertThatThrownBy(() -> User.createClinicOwner("clinic-1", email, password,
                "", "García", "token-abc"))
                .isInstanceOf(UserValidationException.class);
    }

    @Test
    @DisplayName("createClinicOwner: should throw when lastName is blank")
    void createClinicOwner_WhenLastNameBlank_ShouldThrow() {
        assertThatThrownBy(() -> User.createClinicOwner("clinic-1", email, password,
                "Juan", "", "token-abc"))
                .isInstanceOf(UserValidationException.class);
    }

    @Test
    @DisplayName("createClinicOwner: should throw when email is null")
    void createClinicOwner_WhenEmailNull_ShouldThrow() {
        assertThatThrownBy(() -> User.createClinicOwner("clinic-1", null, password,
                "Juan", "García", "token-abc"))
                .isInstanceOf(UserValidationException.class);
    }

    @Test
    @DisplayName("createClinicOwner: should throw when clinicId is null")
    void createClinicOwner_WhenClinicIdNull_ShouldThrow() {
        assertThatThrownBy(() -> User.createClinicOwner(null, email, password,
                "Juan", "García", "token-abc"))
                .isInstanceOf(UserValidationException.class);
    }

    // =========================================================================
    // createEmployee
    // =========================================================================

    @Test
    @DisplayName("createEmployee: should create with ACTIVE status")
    void createEmployee_ShouldHaveActiveStatus() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getRole()).isEqualTo(UserRole.CLINIC_VETERINARIAN);
        assertThat(user.getEmployeeId()).isEqualTo("emp-1");
    }

    @Test
    @DisplayName("createEmployee: should raise UserCreatedEvent")
    void createEmployee_ShouldRaiseUserCreatedEvent() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);

        assertThat(user.getDomainEvents()).hasSize(1);
        assertThat(user.getDomainEvents().get(0)).isInstanceOf(UserCreatedEvent.class);
    }

    @Test
    @DisplayName("createEmployee: should throw when role is SUPER_ADMIN")
    void createEmployee_WhenSuperAdmin_ShouldThrow() {
        assertThatThrownBy(() -> User.createEmployee("clinic-1", "emp-1", email, password, UserRole.SUPER_ADMIN))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("createEmployee: should throw when role is CLINIC_OWNER")
    void createEmployee_WhenClinicOwner_ShouldThrow() {
        assertThatThrownBy(() -> User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_OWNER))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // createPendingEmployee
    // =========================================================================

    @Test
    @DisplayName("createPendingEmployee: should have PENDING_EMAIL_VERIFICATION status and null password")
    void createPendingEmployee_ShouldHavePendingStatus() {
        User user = User.createPendingEmployee("clinic-1", "emp-1", email,
                UserRole.CLINIC_VETERINARIAN, "token-xyz");

        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING_EMAIL_VERIFICATION);
        assertThat(user.getPassword()).isNull();
        assertThat(user.getEmailVerificationToken()).isEqualTo("token-xyz");
    }

    @Test
    @DisplayName("createPendingEmployee: should throw when role is SUPER_ADMIN")
    void createPendingEmployee_WhenSuperAdmin_ShouldThrow() {
        assertThatThrownBy(() -> User.createPendingEmployee("clinic-1", "emp-1", email,
                UserRole.SUPER_ADMIN, "token-xyz"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // verifyEmail
    // =========================================================================

    @Test
    @DisplayName("verifyEmail: should transition to PENDING_CLINIC_SETUP")
    void verifyEmail_ShouldTransitionStatus() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "token-abc");
        user.clearDomainEvents();

        user.verifyEmail("token-abc");

        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING_CLINIC_SETUP);
        assertThat(user.getEmailVerificationToken()).isNull();
        assertThat(user.getEmailVerificationExpiry()).isNull();
    }

    @Test
    @DisplayName("verifyEmail: should raise UserEmailVerifiedEvent")
    void verifyEmail_ShouldRaiseEvent() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "token-abc");
        user.clearDomainEvents();

        user.verifyEmail("token-abc");

        assertThat(user.getDomainEvents()).hasSize(1);
        assertThat(user.getDomainEvents().get(0)).isInstanceOf(UserEmailVerifiedEvent.class);
    }

    @Test
    @DisplayName("verifyEmail: should throw when token does not match")
    void verifyEmail_WhenTokenWrong_ShouldThrow() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "token-abc");

        assertThatThrownBy(() -> user.verifyEmail("wrong-token"))
                .isInstanceOf(EmailTokenExpiredException.class);
    }

    @Test
    @DisplayName("verifyEmail: should throw when user is not in PENDING_EMAIL_VERIFICATION")
    void verifyEmail_WhenWrongStatus_ShouldThrow() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);

        assertThatThrownBy(() -> user.verifyEmail("any-token"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // renewVerificationToken
    // =========================================================================

    @Test
    @DisplayName("renewVerificationToken: should update token and expiry")
    void renewVerificationToken_ShouldUpdateToken() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "old-token");

        user.renewVerificationToken("new-token");

        assertThat(user.getEmailVerificationToken()).isEqualTo("new-token");
        assertThat(user.getEmailVerificationExpiry()).isAfter(java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("renewVerificationToken: should throw when user is not pending verification")
    void renewVerificationToken_WhenWrongStatus_ShouldThrow() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);

        assertThatThrownBy(() -> user.renewVerificationToken("new-token"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // activate
    // =========================================================================

    @Test
    @DisplayName("activate: should set status to ACTIVE and assign employeeId")
    void activate_ShouldActivateUser() {
        User user = User.createClinicOwner("clinic-1", email, password,
                "Juan", "García", "token-abc");
        user.verifyEmail("token-abc");

        user.activate("emp-99");

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getEmployeeId()).isEqualTo("emp-99");
    }

    @Test
    @DisplayName("activate: should throw when user is not in PENDING_CLINIC_SETUP")
    void activate_WhenWrongStatus_ShouldThrow() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);

        assertThatThrownBy(() -> user.activate("emp-99"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // changePassword
    // =========================================================================

    @Test
    @DisplayName("changePassword: should update password for an active user")
    void changePassword_ShouldUpdatePassword() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        HashedPassword newPw = HashedPassword.ofHash("$2a$10$DIFFERENT_HASH_VALUE_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        user.changePassword(newPw);

        assertThat(user.getPassword()).isEqualTo(newPw);
    }

    @Test
    @DisplayName("changePassword: should throw when user is INACTIVE")
    void changePassword_WhenInactive_ShouldThrow() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        user.deactivate("baja voluntaria");

        HashedPassword newPw = HashedPassword.ofHash("$2a$10$DIFFERENT_HASH_VALUE_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        assertThatThrownBy(() -> user.changePassword(newPw))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // deactivate
    // =========================================================================

    @Test
    @DisplayName("deactivate: should set status to INACTIVE")
    void deactivate_ShouldSetInactiveStatus() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        user.clearDomainEvents();

        user.deactivate("baja voluntaria");

        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("deactivate: should raise UserDeactivatedEvent")
    void deactivate_ShouldRaiseEvent() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        user.clearDomainEvents();

        user.deactivate("baja voluntaria");

        assertThat(user.getDomainEvents()).hasSize(1);
        assertThat(user.getDomainEvents().get(0)).isInstanceOf(UserDeactivatedEvent.class);
    }

    @Test
    @DisplayName("deactivate: should throw when already inactive")
    void deactivate_WhenAlreadyInactive_ShouldThrow() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        user.deactivate("baja voluntaria");

        assertThatThrownBy(() -> user.deactivate("again"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // canLogin
    // =========================================================================

    @Test
    @DisplayName("canLogin: should return true only for ACTIVE users")
    void canLogin_OnlyActiveReturnsTrue() {
        User active   = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        User pending  = User.createClinicOwner("clinic-1", email, password, "J", "G", "tok");

        assertThat(active.canLogin()).isTrue();
        assertThat(pending.canLogin()).isFalse();
    }

    // =========================================================================
    // requestPasswordReset
    // =========================================================================

    @Test
    @DisplayName("requestPasswordReset: should store token and expiry 30 min ahead")
    void requestPasswordReset_ShouldStoreToken() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);

        user.requestPasswordReset("reset-token");

        assertThat(user.getPasswordResetToken()).isEqualTo("reset-token");
        assertThat(user.getPasswordResetTokenExpiry()).isAfter(java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("requestPasswordReset: should throw when user is INACTIVE")
    void requestPasswordReset_WhenInactive_ShouldThrow() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        user.deactivate("baja");

        assertThatThrownBy(() -> user.requestPasswordReset("reset-token"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // =========================================================================
    // resetPassword
    // =========================================================================

    @Test
    @DisplayName("resetPassword: should update password and clear token")
    void resetPassword_ShouldUpdatePasswordAndClearToken() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        user.requestPasswordReset("reset-token");
        HashedPassword newPw = HashedPassword.ofHash("$2a$10$DIFFERENT_HASH_VALUE_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        user.resetPassword("reset-token", newPw);

        assertThat(user.getPassword()).isEqualTo(newPw);
        assertThat(user.getPasswordResetToken()).isNull();
        assertThat(user.getPasswordResetTokenExpiry()).isNull();
    }

    @Test
    @DisplayName("resetPassword: should throw when token is wrong")
    void resetPassword_WhenTokenWrong_ShouldThrow() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        user.requestPasswordReset("reset-token");
        HashedPassword newPw = HashedPassword.ofHash("$2a$10$DIFFERENT_HASH_VALUE_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        assertThatThrownBy(() -> user.resetPassword("wrong-token", newPw))
                .isInstanceOf(EmailTokenExpiredException.class);
    }

    // =========================================================================
    // Domain events lifecycle
    // =========================================================================

    @Test
    @DisplayName("getDomainEvents: should return immutable list")
    void getDomainEvents_ShouldReturnImmutableList() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);

        assertThatThrownBy(() -> user.getDomainEvents().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("clearDomainEvents: should empty the events list")
    void clearDomainEvents_ShouldEmptyEvents() {
        User user = User.createEmployee("clinic-1", "emp-1", email, password, UserRole.CLINIC_VETERINARIAN);
        assertThat(user.getDomainEvents()).isNotEmpty();

        user.clearDomainEvents();

        assertThat(user.getDomainEvents()).isEmpty();
    }
}
