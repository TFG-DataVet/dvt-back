package com.datavet.datavet.clinic.infrastructure.adapter.input;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ClinicController endpoints.
 * Tests all CRUD operations, validation errors, and error scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ClinicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Test data constants
    private static final String VALID_CLINIC_NAME = "Veterinaria Central";
    private static final String VALID_LEGAL_NAME = "Veterinaria Central S.A.";
    private static final String VALID_LEGAL_NUMBER = "12345678901";
    private static final String VALID_ADDRESS = "Av. Principal 123";
    private static final String VALID_CITY = "Lima";
    private static final String VALID_CODE_POSTAL = "15001";
    private static final String VALID_PHONE = "+51987654321";
    private static final String VALID_EMAIL = "info@vetcentral.com";
    private static final String VALID_LOGO_URL = "https://example.com/logo.png";

    /**
     * Test successful creation of clinic with valid data.
     * Requirements: 4.1, 4.3
     */
    @Test
    void createClinic_WithValidData_ShouldReturn201AndClinicResponse() throws Exception {
        // Create request DTO (not command) for the REST endpoint
        String requestJson = """
            {
                "clinicName": "%s",
                "legalName": "%s",
                "legalNumber": "%s",
                "address": "%s",
                "city": "%s",
                "codePostal": "%s",
                "phone": "%s",
                "email": "%s",
                "logoUrl": "%s"
            }
            """.formatted(
                VALID_CLINIC_NAME,
                VALID_LEGAL_NAME,
                VALID_LEGAL_NUMBER,
                VALID_ADDRESS,
                VALID_CITY,
                VALID_CODE_POSTAL,
                VALID_PHONE,
                VALID_EMAIL,
                VALID_LOGO_URL
            );

        mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.clinicId").exists())
                .andExpect(jsonPath("$.clinicName").value(VALID_CLINIC_NAME))
                .andExpect(jsonPath("$.legalName").value(VALID_LEGAL_NAME))
                .andExpect(jsonPath("$.legalNumber").value(VALID_LEGAL_NUMBER))
                .andExpect(jsonPath("$.address.street").value(VALID_ADDRESS))
                .andExpect(jsonPath("$.address.city").value(VALID_CITY))
                .andExpect(jsonPath("$.address.postalCode").value(VALID_CODE_POSTAL))
                .andExpect(jsonPath("$.phone").value(VALID_PHONE))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.logoUrl").value(VALID_LOGO_URL));
    }

    /**
     * Test creation with validation errors returns 400.
     * Requirements: 3.2
     */
    @Test
    void createClinic_WithInvalidData_ShouldReturn400WithValidationErrors() throws Exception {
        // Create invalid request JSON to test validation
        String invalidRequestJson = """
            {
                "clinicName": "",
                "legalName": "",
                "legalNumber": "",
                "address": "Valid Street",
                "city": "Valid City",
                "codePostal": "12345",
                "phone": "+1234567890",
                "email": "valid@example.com",
                "logoUrl": null
            }
            """;

        mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.path").value("/clinic"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test retrieval of clinic by ID with valid data.
     * Requirements: 1.1, 4.3
     */
    @Test
    void getClinicById_WithValidId_ShouldReturn200AndClinicResponse() throws Exception {
        // First create a clinic
        String request = createValidRequestJson();
        String response = mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID from the response
        Long clinicId = objectMapper.readTree(response).get("clinicId").asLong();

        // Test GET by ID
        mockMvc.perform(get("/clinic/{id}", clinicId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.clinicId").value(clinicId))
                .andExpect(jsonPath("$.clinicName").value(VALID_CLINIC_NAME))
                .andExpect(jsonPath("$.legalName").value(VALID_LEGAL_NAME))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL));
    }

    /**
     * Test retrieval of non-existent clinic returns 404.
     * Requirements: 3.1
     */
    @Test
    void getClinicById_WithNonExistentId_ShouldReturn404() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(get("/clinic/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Clinic not found with id: " + nonExistentId))
                .andExpect(jsonPath("$.path").value("/clinic/" + nonExistentId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test retrieval of all clinics.
     * Requirements: 1.2, 4.3
     */
    @Test
    void getAllClinics_ShouldReturn200AndListOfClinicResponses() throws Exception {
        // Create two clinics
        String request1 = createValidRequestJson();
        String request2 = """
            {
                "clinicName": "Veterinaria Norte",
                "legalName": "Veterinaria Norte S.A.C.",
                "legalNumber": "98765432101",
                "address": "Av. Norte 456",
                "city": "Trujillo",
                "codePostal": "13001",
                "phone": "+51912345678",
                "email": "info@vetnorte.com",
                "logoUrl": "https://example.com/logo2.png"
            }
            """;

        mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request2))
                .andExpect(status().isCreated());

        // Test GET all
        mockMvc.perform(get("/clinic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clinicId").exists())
                .andExpect(jsonPath("$[1].clinicId").exists());
    }

    /**
     * Test successful update of clinic with valid data.
     * Requirements: 1.3, 4.4
     */
    @Test
    void updateClinic_WithValidData_ShouldReturn200AndUpdatedClinicResponse() throws Exception {
        // First create a clinic
        String createRequest = createValidRequestJson();
        String createResponse = mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long clinicId = objectMapper.readTree(createResponse).get("clinicId").asLong();

        // Update the clinic
        String updateRequestJson = """
            {
                "clinicName": "Updated Clinic Name",
                "legalName": "Updated Legal Name S.A.",
                "legalNumber": "%s",
                "address": "Updated Address 789",
                "city": "Updated City",
                "codePostal": "15002",
                "phone": "+51987654322",
                "email": "updated@clinic.com",
                "logoUrl": "https://example.com/updated-logo.png",
                "suscriptionStatus": "ACTIVE"
            }
            """.formatted(VALID_LEGAL_NUMBER);

        mockMvc.perform(put("/clinic/{id}", clinicId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.clinicId").value(clinicId))
                .andExpect(jsonPath("$.clinicName").value("Updated Clinic Name"))
                .andExpect(jsonPath("$.legalName").value("Updated Legal Name S.A."))
                .andExpect(jsonPath("$.address.street").value("Updated Address 789"))
                .andExpect(jsonPath("$.address.city").value("Updated City"))
                .andExpect(jsonPath("$.email").value("updated@clinic.com"));
    }

    /**
     * Test update with non-existent ID returns 404.
     * Requirements: 3.1
     */
    @Test
    void updateClinic_WithNonExistentId_ShouldReturn404() throws Exception {
        Long nonExistentId = 99999L;

        String updateRequestJson = """
            {
                "clinicName": "Test Clinic",
                "legalName": "Test Legal Name",
                "legalNumber": "123456789",
                "address": "Test Address",
                "city": "Test City",
                "codePostal": "12345",
                "phone": "+1234567890",
                "email": "test@clinic.com"
            }
            """;

        mockMvc.perform(put("/clinic/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Clinic not found with id: " + nonExistentId));
    }

    /**
     * Test update with validation errors returns 400.
     * Requirements: 3.2
     */
    @Test
    void updateClinic_WithInvalidData_ShouldReturn400WithValidationErrors() throws Exception {
        Long clinicId = 1L;

        String invalidUpdateRequestJson = """
            {
                "clinicName": "",
                "legalName": "",
                "legalNumber": "",
                "address": "Valid Street",
                "city": "Valid City",
                "codePostal": "12345",
                "phone": "+1234567890",
                "email": "valid@example.com"
            }
            """;

        mockMvc.perform(put("/clinic/{id}", clinicId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUpdateRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.timestamp").exists());
    }



    /**
     * Test creation of duplicate clinic returns 409.
     * Requirements: 3.4
     */
    @Test
    void createClinic_WithDuplicateData_ShouldReturn409() throws Exception {
        // First create a clinic
        String request = createValidRequestJson();
        mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated());

        // Try to create the same clinic again
        mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test deletion of clinic.
     */
    @Test
    void deleteClinic_WithValidId_ShouldReturn200() throws Exception {
        // First create a clinic
        String request = createValidRequestJson();
        String response = mockMvc.perform(post("/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long clinicId = objectMapper.readTree(response).get("clinicId").asLong();

        // Delete the clinic
        mockMvc.perform(delete("/clinic/{id}", clinicId))
                .andExpect(status().isOk());

        // Verify it's deleted by trying to get it
        mockMvc.perform(get("/clinic/{id}", clinicId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test deletion of non-existent clinic returns 404.
     */
    @Test
    void deleteClinic_WithNonExistentId_ShouldReturn404() throws Exception {
        Long nonExistentId = 99999L;

        mockMvc.perform(delete("/clinic/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Clinic not found with id: " + nonExistentId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Helper method to create a valid CreateClinicCommand for testing.
     */
    private CreateClinicCommand createValidCommand() {
        return new CreateClinicCommand(
            VALID_CLINIC_NAME,
            VALID_LEGAL_NAME,
            VALID_LEGAL_NUMBER,
            new Address(VALID_ADDRESS, VALID_CITY, VALID_CODE_POSTAL),
            new Phone(VALID_PHONE),
            new Email(VALID_EMAIL),
            VALID_LOGO_URL
        );
    }

    /**
     * Helper method to create a valid request JSON for testing.
     */
    private String createValidRequestJson() {
        return """
            {
                "clinicName": "%s",
                "legalName": "%s",
                "legalNumber": "%s",
                "address": "%s",
                "city": "%s",
                "codePostal": "%s",
                "phone": "%s",
                "email": "%s",
                "logoUrl": "%s"
            }
            """.formatted(
                VALID_CLINIC_NAME,
                VALID_LEGAL_NAME,
                VALID_LEGAL_NUMBER,
                VALID_ADDRESS,
                VALID_CITY,
                VALID_CODE_POSTAL,
                VALID_PHONE,
                VALID_EMAIL,
                VALID_LOGO_URL
            );
    }
}