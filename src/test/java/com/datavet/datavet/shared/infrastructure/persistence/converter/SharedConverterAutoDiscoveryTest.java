package com.datavet.datavet.shared.infrastructure.persistence.converter;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that shared converters are properly configured for JPA auto-discovery.
 * This test validates that converters work correctly from their new shared location.
 * Requirements: 4.5, 2.1, 2.2, 2.3
 */
class SharedConverterAutoDiscoveryTest {

    private AddressConverter addressConverter;
    private EmailConverter emailConverter;
    private PhoneConverter phoneConverter;

    @BeforeEach
    void setUp() {
        // Instantiate converters to verify they can be created from shared location
        addressConverter = new AddressConverter();
        emailConverter = new EmailConverter();
        phoneConverter = new PhoneConverter();
    }

    @Test
    @DisplayName("AddressConverter should be instantiable from shared location")
    void addressConverter_ShouldBeInstantiableFromSharedLocation() {
        assertNotNull(addressConverter, "AddressConverter should be instantiable");
        
        // Test basic functionality
        Address testAddress = new Address("123 Shared St", "Shared City", "12345");
        String dbValue = addressConverter.convertToDatabaseColumn(testAddress);
        Address convertedBack = addressConverter.convertToEntityAttribute(dbValue);
        
        assertNotNull(dbValue, "Database value should not be null");
        assertNotNull(convertedBack, "Converted back address should not be null");
        assertEquals("123 Shared St", convertedBack.getStreet());
        assertEquals("Shared City", convertedBack.getCity());
        assertEquals("12345", convertedBack.getPostalCode());
    }

    @Test
    @DisplayName("EmailConverter should be instantiable from shared location")
    void emailConverter_ShouldBeInstantiableFromSharedLocation() {
        assertNotNull(emailConverter, "EmailConverter should be instantiable");
        
        // Test basic functionality
        Email testEmail = new Email("shared@test.com");
        String dbValue = emailConverter.convertToDatabaseColumn(testEmail);
        Email convertedBack = emailConverter.convertToEntityAttribute(dbValue);
        
        assertNotNull(dbValue, "Database value should not be null");
        assertNotNull(convertedBack, "Converted back email should not be null");
        assertEquals("shared@test.com", convertedBack.getValue());
    }

    @Test
    @DisplayName("PhoneConverter should be instantiable from shared location")
    void phoneConverter_ShouldBeInstantiableFromSharedLocation() {
        assertNotNull(phoneConverter, "PhoneConverter should be instantiable");
        
        // Test basic functionality
        Phone testPhone = new Phone("+1234567890");
        String dbValue = phoneConverter.convertToDatabaseColumn(testPhone);
        Phone convertedBack = phoneConverter.convertToEntityAttribute(dbValue);
        
        assertNotNull(dbValue, "Database value should not be null");
        assertNotNull(convertedBack, "Converted back phone should not be null");
        assertEquals("+1234567890", convertedBack.getValue());
    }

    @Test
    @DisplayName("Converters should have autoApply annotation for JPA auto-discovery")
    void converters_ShouldHaveAutoApplyAnnotation() {
        // Verify that converters have the @Converter(autoApply = true) annotation
        // This is checked by examining the class annotations
        
        Class<AddressConverter> addressConverterClass = AddressConverter.class;
        Class<EmailConverter> emailConverterClass = EmailConverter.class;
        Class<PhoneConverter> phoneConverterClass = PhoneConverter.class;
        
        // Check that all converter classes have the @Converter annotation
        assertTrue(addressConverterClass.isAnnotationPresent(jakarta.persistence.Converter.class),
                "AddressConverter should have @Converter annotation");
        assertTrue(emailConverterClass.isAnnotationPresent(jakarta.persistence.Converter.class),
                "EmailConverter should have @Converter annotation");
        assertTrue(phoneConverterClass.isAnnotationPresent(jakarta.persistence.Converter.class),
                "PhoneConverter should have @Converter annotation");
        
        // Verify autoApply is set to true
        jakarta.persistence.Converter addressAnnotation = addressConverterClass.getAnnotation(jakarta.persistence.Converter.class);
        jakarta.persistence.Converter emailAnnotation = emailConverterClass.getAnnotation(jakarta.persistence.Converter.class);
        jakarta.persistence.Converter phoneAnnotation = phoneConverterClass.getAnnotation(jakarta.persistence.Converter.class);
        
        assertTrue(addressAnnotation.autoApply(), "AddressConverter should have autoApply = true");
        assertTrue(emailAnnotation.autoApply(), "EmailConverter should have autoApply = true");
        assertTrue(phoneAnnotation.autoApply(), "PhoneConverter should have autoApply = true");
    }

    @Test
    @DisplayName("Converters should handle null values correctly")
    void converters_ShouldHandleNullValuesCorrectly() {
        // Test null handling for all converters
        assertNull(addressConverter.convertToDatabaseColumn(null), 
                "AddressConverter should return null for null input");
        assertNull(addressConverter.convertToEntityAttribute(null), 
                "AddressConverter should return null for null database value");
        
        assertNull(emailConverter.convertToDatabaseColumn(null), 
                "EmailConverter should return null for null input");
        assertNull(emailConverter.convertToEntityAttribute(null), 
                "EmailConverter should return null for null database value");
        
        assertNull(phoneConverter.convertToDatabaseColumn(null), 
                "PhoneConverter should return null for null input");
        assertNull(phoneConverter.convertToEntityAttribute(null), 
                "PhoneConverter should return null for null database value");
    }

    @Test
    @DisplayName("Converters should be in correct shared package")
    void converters_ShouldBeInCorrectSharedPackage() {
        // Verify that converters are in the shared package
        String expectedPackage = "com.datavet.datavet.shared.infrastructure.persistence.converter";
        
        assertEquals(expectedPackage, AddressConverter.class.getPackage().getName(),
                "AddressConverter should be in shared converter package");
        assertEquals(expectedPackage, EmailConverter.class.getPackage().getName(),
                "EmailConverter should be in shared converter package");
        assertEquals(expectedPackage, PhoneConverter.class.getPackage().getName(),
                "PhoneConverter should be in shared converter package");
    }
}