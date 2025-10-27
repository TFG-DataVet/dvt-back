package com.datavet.datavet.shared.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Value Objects Integration Tests")
class ValueObjectsIntegrationTest {

    @Test
    @DisplayName("Address should validate and format correctly")
    void addressShouldValidateAndFormatCorrectly() {
        // Given & When
        Address address = new Address("123 Main Street", "Springfield", "12345");

        // Then
        assertEquals("123 Main Street", address.getStreet());
        assertEquals("Springfield", address.getCity());
        assertEquals("12345", address.getPostalCode());
        assertEquals("123 Main Street, Springfield 12345", address.getFullAddress());
        assertEquals("123 Main Street, Springfield 12345", address.toString());
    }

    @Test
    @DisplayName("Address should handle null postal code")
    void addressShouldHandleNullPostalCode() {
        // Given & When
        Address address = new Address("123 Main Street", "Springfield", null);

        // Then
        assertEquals("123 Main Street", address.getStreet());
        assertEquals("Springfield", address.getCity());
        assertNull(address.getPostalCode());
        assertEquals("123 Main Street, Springfield", address.getFullAddress());
    }

    @Test
    @DisplayName("Address should handle empty postal code")
    void addressShouldHandleEmptyPostalCode() {
        // Given & When
        Address address = new Address("123 Main Street", "Springfield", "");

        // Then
        assertEquals("123 Main Street", address.getStreet());
        assertEquals("Springfield", address.getCity());
        assertEquals("", address.getPostalCode());
        assertEquals("123 Main Street, Springfield", address.getFullAddress());
    }

    @Test
    @DisplayName("Address should throw exception for null street")
    void addressShouldThrowExceptionForNullStreet() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Address(null, "Springfield", "12345");
        }, "Should throw exception for null street");
    }

    @Test
    @DisplayName("Address should throw exception for empty street")
    void addressShouldThrowExceptionForEmptyStreet() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Address("", "Springfield", "12345");
        }, "Should throw exception for empty street");
    }

    @Test
    @DisplayName("Address should throw exception for null city")
    void addressShouldThrowExceptionForNullCity() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Address("123 Main Street", null, "12345");
        }, "Should throw exception for null city");
    }

    @Test
    @DisplayName("Address should throw exception for empty city")
    void addressShouldThrowExceptionForEmptyCity() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Address("123 Main Street", "", "12345");
        }, "Should throw exception for empty city");
    }

    @Test
    @DisplayName("Email should validate and format correctly")
    void emailShouldValidateAndFormatCorrectly() {
        // Given & When
        Email email = new Email("test@example.com");

        // Then
        assertEquals("test@example.com", email.getValue());
        assertEquals("test@example.com", email.toString());
    }

    @Test
    @DisplayName("Email should handle various valid formats")
    void emailShouldHandleVariousValidFormats() {
        String[] validEmails = {
                "test@example.com",
                "user.name@domain.co.uk",
                "user+tag@example.org",
                "123@numbers.com",
                "a@b.co"
        };

        for (String emailValue : validEmails) {
            assertDoesNotThrow(() -> {
                Email email = new Email(emailValue);
                assertEquals(emailValue, email.getValue());
            }, "Should accept valid email: " + emailValue);
        }
    }

    @Test
    @DisplayName("Email should throw exception for invalid formats")
    void emailShouldThrowExceptionForInvalidFormats() {
        String[] invalidEmails = {
                "invalid-email",
                "@example.com",
                "test@",
                "test.example.com",
                "test@.com",
                "test@com",
                ""
        };

        for (String emailValue : invalidEmails) {
            assertThrows(IllegalArgumentException.class, () -> {
                new Email(emailValue);
            }, "Should throw exception for invalid email: " + emailValue);
        }
    }

    @Test
    @DisplayName("Email should throw exception for null value")
    void emailShouldThrowExceptionForNullValue() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Email(null);
        }, "Should throw exception for null email");
    }

    @Test
    @DisplayName("Phone should validate and format correctly")
    void phoneShouldValidateAndFormatCorrectly() {
        // Given & When
        Phone phone = new Phone("+1234567890");

        // Then
        assertEquals("+1234567890", phone.getValue());
        assertEquals("+1234567890", phone.toString());
    }

    @Test
    @DisplayName("Phone should handle various valid formats")
    void phoneShouldHandleVariousValidFormats() {
        String[] validPhones = {
                "+1234567890",
                "123-456-7890",
                "(123) 456-7890",
                "123 456 7890",
                "1234567",
                "+44 20 7946 0958",
                "555-0123"
        };

        for (String phoneValue : validPhones) {
            assertDoesNotThrow(() -> {
                Phone phone = new Phone(phoneValue);
                assertEquals(phoneValue, phone.getValue());
            }, "Should accept valid phone: " + phoneValue);
        }
    }

    @Test
    @DisplayName("Phone should throw exception for invalid formats")
    void phoneShouldThrowExceptionForInvalidFormats() {
        String[] invalidPhones = {
                "invalid-phone",
                "abc123",
                "123",
                "12345678901234567890", // too long
                "",
                "++123456789"
        };

        for (String phoneValue : invalidPhones) {
            assertThrows(IllegalArgumentException.class, () -> {
                new Phone(phoneValue);
            }, "Should throw exception for invalid phone: " + phoneValue);
        }
    }

    @Test
    @DisplayName("Phone should throw exception for null value")
    void phoneShouldThrowExceptionForNullValue() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Phone(null);
        }, "Should throw exception for null phone");
    }

    @Test
    @DisplayName("Value objects should implement equals and hashCode correctly")
    void valueObjectsShouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        Address address1 = new Address("123 Main Street", "Springfield", "12345");
        Address address2 = new Address("123 Main Street", "Springfield", "12345");
        Address address3 = new Address("456 Oak Street", "Springfield", "12345");

        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");
        Email email3 = new Email("other@example.com");

        Phone phone1 = new Phone("+1234567890");
        Phone phone2 = new Phone("+1234567890");
        Phone phone3 = new Phone("+0987654321");

        // Then - Address
        assertEquals(address1, address2, "Equal addresses should be equal");
        assertNotEquals(address1, address3, "Different addresses should not be equal");
        assertEquals(address1.hashCode(), address2.hashCode(), "Equal addresses should have same hash code");

        // Then - Email
        assertEquals(email1, email2, "Equal emails should be equal");
        assertNotEquals(email1, email3, "Different emails should not be equal");
        assertEquals(email1.hashCode(), email2.hashCode(), "Equal emails should have same hash code");

        // Then - Phone
        assertEquals(phone1, phone2, "Equal phones should be equal");
        assertNotEquals(phone1, phone3, "Different phones should not be equal");
        assertEquals(phone1.hashCode(), phone2.hashCode(), "Equal phones should have same hash code");
    }
}