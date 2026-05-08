package com.datavet.auth.domain.valueobject;

import com.datavet.auth.domain.exception.InvalidCredentialsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("HashedPassword Value Object Tests")
class HashedPasswordTest {

    private static final String VALID_BCRYPT = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    // =========================================================================
    // ofHash — happy path
    // =========================================================================

    @Test
    @DisplayName("ofHash: should create with valid bcrypt hash")
    void ofHash_WithValidBcrypt_ShouldCreate() {
        HashedPassword hp = HashedPassword.ofHash(VALID_BCRYPT);

        assertThat(hp).isNotNull();
        assertThat(hp.getValue()).isEqualTo(VALID_BCRYPT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"$2a$10$abc", "$2b$12$xyz", "$2y$08$test"})
    @DisplayName("ofHash: should accept any $2 prefix variant")
    void ofHash_WithAny2xPrefix_ShouldCreate(String hash) {
        HashedPassword hp = HashedPassword.ofHash(hash);
        assertThat(hp.getValue()).isEqualTo(hash);
    }

    @Test
    @DisplayName("ofHash: two instances with same hash should be equal")
    void ofHash_SameHash_ShouldBeEqual() {
        HashedPassword a = HashedPassword.ofHash(VALID_BCRYPT);
        HashedPassword b = HashedPassword.ofHash(VALID_BCRYPT);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("ofHash: toString should not expose the hash value")
    void ofHash_ToString_ShouldNotExposeHash() {
        HashedPassword hp = HashedPassword.ofHash(VALID_BCRYPT);
        assertThat(hp.toString()).doesNotContain(VALID_BCRYPT);
        assertThat(hp.toString()).contains("PROTECTED");
    }

    // =========================================================================
    // ofHash — validaciones
    // =========================================================================

    @Test
    @DisplayName("ofHash: should throw when hash is null")
    void ofHash_WhenNull_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.ofHash(null))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("ofHash: should throw when hash is blank")
    void ofHash_WhenBlank_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.ofHash("   "))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("ofHash: should throw when hash does not start with $2")
    void ofHash_WhenNotBcrypt_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.ofHash("sha256:abc123"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("BCrypt");
    }

    // =========================================================================
    // validateRawPassword — happy path
    // =========================================================================

    @Test
    @DisplayName("validateRawPassword: should not throw for a valid password")
    void validateRaw_ValidPassword_ShouldNotThrow() {
        HashedPassword.validateRawPassword("Password1");
    }

    @Test
    @DisplayName("validateRawPassword: should accept password at exactly 8 chars")
    void validateRaw_ExactlyEightChars_ShouldNotThrow() {
        HashedPassword.validateRawPassword("Passw0rd");
    }

    @Test
    @DisplayName("validateRawPassword: should accept password at exactly 72 chars")
    void validateRaw_ExactlySeventyTwoChars_ShouldNotThrow() {
        String pw = "Aa1" + "a".repeat(69);
        HashedPassword.validateRawPassword(pw);
    }

    // =========================================================================
    // validateRawPassword — validaciones
    // =========================================================================

    @Test
    @DisplayName("validateRawPassword: should throw when password is null")
    void validateRaw_WhenNull_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.validateRawPassword(null))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("validateRawPassword: should throw when password is blank")
    void validateRaw_WhenBlank_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.validateRawPassword(""))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("validateRawPassword: should throw when password is shorter than 8 chars")
    void validateRaw_WhenTooShort_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.validateRawPassword("Pass1"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("8 caracteres");
    }

    @Test
    @DisplayName("validateRawPassword: should throw when password exceeds 72 chars")
    void validateRaw_WhenTooLong_ShouldThrow() {
        String pw = "Aa1" + "a".repeat(70);
        assertThatThrownBy(() -> HashedPassword.validateRawPassword(pw))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("72 caracteres");
    }

    @Test
    @DisplayName("validateRawPassword: should throw when password has no uppercase")
    void validateRaw_WhenNoUppercase_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.validateRawPassword("password1"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("mayúscula");
    }

    @Test
    @DisplayName("validateRawPassword: should throw when password has no lowercase")
    void validateRaw_WhenNoLowercase_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.validateRawPassword("PASSWORD1"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("minúscula");
    }

    @Test
    @DisplayName("validateRawPassword: should throw when password has no digit")
    void validateRaw_WhenNoDigit_ShouldThrow() {
        assertThatThrownBy(() -> HashedPassword.validateRawPassword("PasswordABC"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("número");
    }
}
