package com.datavet.datavet.clinic.infrastructure.persistence.converter;

import com.datavet.datavet.clinic.infrastructure.persistence.converter.AddressConverter;
import com.datavet.datavet.clinic.infrastructure.persistence.converter.EmailConverter;
import com.datavet.datavet.clinic.infrastructure.persistence.converter.PhoneConverter;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JPA converters for value objects.
 * Verifies proper conversion between value objects and database columns.
 * Requirements: 5.3
 */
class JpaConvertersTest {

    private AddressConverter addressConverter;
    private EmailConverter emailConverter;
    private PhoneConverter phoneConverter;

    @BeforeEach
    void setUp() {
        addressConverter = new AddressConverter();
        emailConverter = new EmailConverter();
        phoneConverter = new PhoneConverter();
    }

    @Test
    @DisplayName("AddressConverter should convert Address to JSON string")
    void addressConverter_ShouldConvertAddressToJsonString() {
        Address address = new Address("123 Main St", "Springfield", "12345");
        
        String dbColumn = addressConverter.convertToDatabaseColumn(address);
        
        assertNotNull(dbColumn, "Database column should not be null");
        assertTrue(dbColumn.contains("123 Main St"), "Should contain street");
        assertTrue(dbColumn.contains("Springfield"), "Should contain city");
        assertTrue(dbColumn.contains("12345"), "Should contain postal code");
        assertTrue(dbColumn.startsWith("{") && dbColumn.endsWith("}"), "Should be JSON format");
    }

    @Test
    @DisplayName("AddressConverter should convert JSON string back to Address")
    void addressConverter_ShouldConvertJsonStringToAddress() {
        String jsonString = "{\"street\":\"456 Oak Ave\",\"city\":\"Riverside\",\"postalCode\":\"67890\"}";
        
        Address address = addressConverter.convertToEntityAttribute(jsonString);
        
        assertNotNull(address, "Address should not be null");
        assertEquals("456 Oak Ave", address.getStreet());
        assertEquals("Riverside", address.getCity());
        assertEquals("67890", address.getPostalCode());
    }

    @Test
    @DisplayName("AddressConverter should handle null values")
    void addressConverter_ShouldHandleNullValues() {
        // Test null Address to database
        String dbColumn = addressConverter.convertToDatabaseColumn(null);
        assertNull(dbColumn, "Database column should be null for null Address");
        
        // Test null database value to Address
        Address address = addressConverter.convertToEntityAttribute(null);
        assertNull(address, "Address should be null for null database value");
        
        // Test empty string
        Address addressFromEmpty = addressConverter.convertToEntityAttribute("");
        assertNull(addressFromEmpty, "Address should be null for empty string");
    }

    @Test
    @DisplayName("AddressConverter should handle legacy format")
    void addressConverter_ShouldHandleLegacyFormat() {
        String legacyString = "123 Legacy Street";
        
        Address address = addressConverter.convertToEntityAttribute(legacyString);
        
        assertNotNull(address, "Address should not be null");
        assertEquals("123 Legacy Street", address.getStreet());
        assertEquals("Unknown", address.getCity());
        assertNull(address.getPostalCode());
    }

    @Test
    @DisplayName("AddressConverter should handle Address without postal code")
    void addressConverter_ShouldHandleAddressWithoutPostalCode() {
        Address address = new Address("789 No Postal St", "NoPostal City", null);
        
        String dbColumn = addressConverter.convertToDatabaseColumn(address);
        Address convertedBack = addressConverter.convertToEntityAttribute(dbColumn);
        
        assertNotNull(convertedBack, "Converted address should not be null");
        assertEquals("789 No Postal St", convertedBack.getStreet());
        assertEquals("NoPostal City", convertedBack.getCity());
        assertNull(convertedBack.getPostalCode());
    }

    @Test
    @DisplayName("EmailConverter should convert Email to string")
    void emailConverter_ShouldConvertEmailToString() {
        Email email = new Email("test@example.com");
        
        String dbColumn = emailConverter.convertToDatabaseColumn(email);
        
        assertNotNull(dbColumn, "Database column should not be null");
        assertEquals("test@example.com", dbColumn);
    }

    @Test
    @DisplayName("EmailConverter should convert string back to Email")
    void emailConverter_ShouldConvertStringToEmail() {
        String emailString = "converted@example.com";
        
        Email email = emailConverter.convertToEntityAttribute(emailString);
        
        assertNotNull(email, "Email should not be null");
        assertEquals("converted@example.com", email.getValue());
    }

    @Test
    @DisplayName("EmailConverter should handle null values")
    void emailConverter_ShouldHandleNullValues() {
        // Test null Email to database
        String dbColumn = emailConverter.convertToDatabaseColumn(null);
        assertNull(dbColumn, "Database column should be null for null Email");
        
        // Test null database value to Email
        Email email = emailConverter.convertToEntityAttribute(null);
        assertNull(email, "Email should be null for null database value");
        
        // Test empty string
        Email emailFromEmpty = emailConverter.convertToEntityAttribute("");
        assertNull(emailFromEmpty, "Email should be null for empty string");
        
        // Test whitespace string
        Email emailFromWhitespace = emailConverter.convertToEntityAttribute("   ");
        assertNull(emailFromWhitespace, "Email should be null for whitespace string");
    }

    @Test
    @DisplayName("PhoneConverter should convert Phone to string")
    void phoneConverter_ShouldConvertPhoneToString() {
        Phone phone = new Phone("+1234567890");
        
        String dbColumn = phoneConverter.convertToDatabaseColumn(phone);
        
        assertNotNull(dbColumn, "Database column should not be null");
        assertEquals("+1234567890", dbColumn);
    }

    @Test
    @DisplayName("PhoneConverter should convert string back to Phone")
    void phoneConverter_ShouldConvertStringToPhone() {
        String phoneString = "+0987654321";
        
        Phone phone = phoneConverter.convertToEntityAttribute(phoneString);
        
        assertNotNull(phone, "Phone should not be null");
        assertEquals("+0987654321", phone.getValue());
    }

    @Test
    @DisplayName("PhoneConverter should handle null values")
    void phoneConverter_ShouldHandleNullValues() {
        // Test null Phone to database
        String dbColumn = phoneConverter.convertToDatabaseColumn(null);
        assertNull(dbColumn, "Database column should be null for null Phone");
        
        // Test null database value to Phone
        Phone phone = phoneConverter.convertToEntityAttribute(null);
        assertNull(phone, "Phone should be null for null database value");
        
        // Test empty string
        Phone phoneFromEmpty = phoneConverter.convertToEntityAttribute("");
        assertNull(phoneFromEmpty, "Phone should be null for empty string");
        
        // Test whitespace string
        Phone phoneFromWhitespace = phoneConverter.convertToEntityAttribute("   ");
        assertNull(phoneFromWhitespace, "Phone should be null for whitespace string");
    }

    @Test
    @DisplayName("Converters should handle round-trip conversion correctly")
    void converters_ShouldHandleRoundTripConversion() {
        // Test Address round-trip
        Address originalAddress = new Address("Round Trip St", "Round City", "54321");
        String addressDb = addressConverter.convertToDatabaseColumn(originalAddress);
        Address convertedAddress = addressConverter.convertToEntityAttribute(addressDb);
        
        assertEquals(originalAddress.getStreet(), convertedAddress.getStreet());
        assertEquals(originalAddress.getCity(), convertedAddress.getCity());
        assertEquals(originalAddress.getPostalCode(), convertedAddress.getPostalCode());
        
        // Test Email round-trip
        Email originalEmail = new Email("roundtrip@test.com");
        String emailDb = emailConverter.convertToDatabaseColumn(originalEmail);
        Email convertedEmail = emailConverter.convertToEntityAttribute(emailDb);
        
        assertEquals(originalEmail.getValue(), convertedEmail.getValue());
        
        // Test Phone round-trip
        Phone originalPhone = new Phone("+1122334455");
        String phoneDb = phoneConverter.convertToDatabaseColumn(originalPhone);
        Phone convertedPhone = phoneConverter.convertToEntityAttribute(phoneDb);
        
        assertEquals(originalPhone.getValue(), convertedPhone.getValue());
    }
}