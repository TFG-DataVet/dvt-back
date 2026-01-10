package com.datavet.datavet.owner.infrastructure.adapter.input;

import com.datavet.datavet.owner.infrastructure.persistence.repository.MongoOwnerRepositoryAdapter;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OwnerController endpoints.
 * Tests all CRUD operations, validation errors, and error scenarios with MongoDB.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoOwnerRepositoryAdapter ownerRepository;

    // Test data constants
    private static final String VALID_NAME = "Juan";
    private static final String VALID_LAST_NAME = "Pérez";
    private static final String VALID_DNI = "12345678A";
    private static final String VALID_PHONE = "+34612345678";
    private static final String VALID_EMAIL = "juan.perez@example.com";
    private static final String VALID_ADDRESS = "Calle Mayor 123";
    private static final String VALID_CITY = "Madrid";
    private static final String VALID_POSTAL_CODE = "28001";

    @BeforeEach
    void setUp() {
        // Clean MongoDB collections before each test
        ownerRepository.deleteAll();
    }

    /**
     * Test successful creation of owner with valid data.
     * Requirements: 8.1, 8.2, 8.3, 8.4
     */
    @Test
    void shouldCreateOwnerWithValidData() throws Exception {
        String requestJson = """
            {
                "name": "%s",
                "lastName": "%s",
                "dni": "%s",
                "phone": "%s",
                "email": "%s",
                "address": "%s",
                "city": "%s",
                "postalCode": "%s"
            }
            """.formatted(
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_DNI,
                VALID_PHONE,
                VALID_EMAIL,
                VALID_ADDRESS,
                VALID_CITY,
                VALID_POSTAL_CODE
            );

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.ownerId").isString())
                .andExpect(jsonPath("$.firstName").value(VALID_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andExpect(jsonPath("$.dni").value(VALID_DNI))
                .andExpect(jsonPath("$.phone").value(VALID_PHONE))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.address.street").value(VALID_ADDRESS))
                .andExpect(jsonPath("$.address.city").value(VALID_CITY))
                .andExpect(jsonPath("$.address.postalCode").value(VALID_POSTAL_CODE));
    }

    /**
     * Test that created owner returns with generated MongoDB ObjectId.
     * Requirements: 8.1, 8.2, 8.4
     */
    @Test
    void shouldReturnCreatedOwnerWithGeneratedObjectId() throws Exception {
        String requestJson = createValidRequestJson();

        String response = mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.ownerId").isString())
                .andExpect(jsonPath("$.firstName").value(VALID_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verify the ID is a valid MongoDB ObjectId format (24 hex characters)
        String ownerId = objectMapper.readTree(response).get("ownerId").asText();
        assert ownerId.matches("[0-9a-f]{24}");
    }

    /**
     * Test creation with validation errors returns 400.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldReturn400WhenCreatingWithInvalidData() throws Exception {
        String invalidRequestJson = """
            {
                "name": "",
                "lastName": "",
                "dni": "",
                "phone": "invalid-phone",
                "email": "invalid-email",
                "address": "",
                "city": "",
                "postalCode": ""
            }
            """;

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.path").value("/owner"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test retrieval of owner by ID with valid data.
     * Requirements: 8.1, 8.2, 8.4
     */
    @Test
    void shouldGetOwnerById() throws Exception {
        // First create an owner
        String request = createValidRequestJson();
        String response = mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the String ID from the response
        String ownerId = objectMapper.readTree(response).get("ownerId").asText();

        // Test GET by ID
        mockMvc.perform(get("/owner/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value(VALID_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL));
    }

    /**
     * Test retrieval of non-existent owner returns 404.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldReturn404WhenOwnerNotFound() throws Exception {
        String nonExistentId = new ObjectId().toString();

        mockMvc.perform(get("/owner/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + nonExistentId))
                .andExpect(jsonPath("$.path").value("/owner/" + nonExistentId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test that GET returns all fields including Value Objects.
     * Requirements: 8.2, 8.4
     */
    @Test
    void shouldReturnAllFieldsIncludingValueObjects() throws Exception {
        // Create an owner
        String request = createValidRequestJson();
        String response = mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(response).get("ownerId").asText();

        // Test GET by ID returns all fields including Value Objects
        mockMvc.perform(get("/owner/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value(VALID_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andExpect(jsonPath("$.dni").value(VALID_DNI))
                // Verify Address Value Object
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.address.street").value(VALID_ADDRESS))
                .andExpect(jsonPath("$.address.city").value(VALID_CITY))
                .andExpect(jsonPath("$.address.postalCode").value(VALID_POSTAL_CODE))
                .andExpect(jsonPath("$.address.fullAddress").exists())
                // Verify Phone Value Object
                .andExpect(jsonPath("$.phone").value(VALID_PHONE))
                // Verify Email Value Object
                .andExpect(jsonPath("$.email").value(VALID_EMAIL));
    }

    /**
     * Test retrieval of all owners.
     * Requirements: 8.1, 8.2, 8.4
     */
    @Test
    void shouldGetAllOwners() throws Exception {
        // Create two owners
        String request1 = createValidRequestJson();
        String request2 = """
            {
                "name": "María",
                "lastName": "García",
                "dni": "87654321B",
                "phone": "+34698765432",
                "email": "maria.garcia@example.com",
                "address": "Calle Norte 456",
                "city": "Barcelona",
                "postalCode": "08001"
            }
            """;

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request2))
                .andExpect(status().isCreated());

        // Test GET all
        mockMvc.perform(get("/owner"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ownerId").exists())
                .andExpect(jsonPath("$[1].ownerId").exists());
    }

    /**
     * Test that GET all returns empty array when no owners exist.
     * Requirements: 8.1, 8.5
     */
    @Test
    void shouldReturnEmptyArrayWhenNoOwners() throws Exception {
        // Test GET all without creating any owners
        mockMvc.perform(get("/owner"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Test successful update of owner with valid data.
     * Requirements: 8.1, 8.2, 8.4
     */
    @Test
    void shouldUpdateOwnerWithValidData() throws Exception {
        // First create an owner
        String createRequest = createValidRequestJson();
        String createResponse = mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(createResponse).get("ownerId").asText();

        // Update the owner
        String updateRequestJson = """
            {
                "name": "Updated Name",
                "lastName": "Updated LastName",
                "dni": "%s",
                "phone": "+34612345679",
                "email": "updated@example.com",
                "address": "Updated Address 789",
                "city": "Updated City",
                "postalCode": "28002"
            }
            """.formatted(VALID_DNI);

        mockMvc.perform(put("/owner/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Updated Name"))
                .andExpect(jsonPath("$.lastName").value("Updated LastName"))
                .andExpect(jsonPath("$.address.street").value("Updated Address 789"))
                .andExpect(jsonPath("$.address.city").value("Updated City"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    /**
     * Test update with non-existent ID returns 404.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldReturn404WhenUpdatingNonExistentOwner() throws Exception {
        String nonExistentId = new ObjectId().toString();

        String updateRequestJson = """
            {
                "name": "Test Name",
                "lastName": "Test LastName",
                "dni": "12345678A",
                "phone": "+34612345678",
                "email": "test@example.com",
                "address": "Test Address",
                "city": "Test City",
                "postalCode": "12345"
            }
            """;

        mockMvc.perform(put("/owner/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + nonExistentId));
    }

    /**
     * Test update with validation errors returns 400.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldReturn400WhenUpdatingWithInvalidData() throws Exception {
        String ownerId = new ObjectId().toString();

        String invalidUpdateRequestJson = """
            {
                "name": "",
                "lastName": "",
                "dni": "",
                "phone": "invalid-phone",
                "email": "invalid-email",
                "address": "",
                "city": "",
                "postalCode": ""
            }
            """;

        mockMvc.perform(put("/owner/{id}", ownerId)
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
     * Test update with duplicate email returns 409.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldReturn409WhenUpdatingWithDuplicateEmail() throws Exception {
        // Create first owner
        String request1 = createValidRequestJson();
        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request1))
                .andExpect(status().isCreated());

        // Create second owner with different email
        String request2 = """
            {
                "name": "María",
                "lastName": "García",
                "dni": "87654321B",
                "phone": "+34698765432",
                "email": "maria.garcia@example.com",
                "address": "Calle Norte 456",
                "city": "Barcelona",
                "postalCode": "08001"
            }
            """;
        
        String createResponse = mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request2))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(createResponse).get("ownerId").asText();

        // Try to update second owner with email from first owner
        String updateRequestJson = """
            {
                "name": "María",
                "lastName": "García",
                "dni": "87654321B",
                "phone": "+34698765432",
                "email": "%s",
                "address": "Calle Norte 456",
                "city": "Barcelona",
                "postalCode": "08001"
            }
            """.formatted(VALID_EMAIL);

        mockMvc.perform(put("/owner/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test update with same email (own email) should succeed.
     * Requirements: 8.2, 8.4
     */
    @Test
    void shouldSucceedWhenUpdatingWithSameEmail() throws Exception {
        // Create an owner
        String createRequest = createValidRequestJson();
        String createResponse = mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(createResponse).get("ownerId").asText();

        // Update the owner keeping the same email
        String updateRequestJson = """
            {
                "name": "Updated Name",
                "lastName": "Updated LastName",
                "dni": "%s",
                "phone": "+34612345679",
                "email": "%s",
                "address": "Updated Address 789",
                "city": "Updated City",
                "postalCode": "28002"
            }
            """.formatted(VALID_DNI, VALID_EMAIL);

        mockMvc.perform(put("/owner/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL));
    }

    /**
     * Test creation with duplicate email returns 409.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        // First create an owner
        String request = createValidRequestJson();
        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated());

        // Try to create another owner with the same email but different DNI
        String duplicateEmailRequest = """
            {
                "name": "Different Name",
                "lastName": "Different LastName",
                "dni": "99999999Z",
                "phone": "+34699999999",
                "email": "%s",
                "address": "Different Address",
                "city": "Different City",
                "postalCode": "99999"
            }
            """.formatted(VALID_EMAIL);

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateEmailRequest))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test creation with duplicate DNI returns 409.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldReturn409WhenDniAlreadyExists() throws Exception {
        // First create an owner
        String request = createValidRequestJson();
        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated());

        // Try to create another owner with the same DNI but different email
        String duplicateDniRequest = """
            {
                "name": "Different Name",
                "lastName": "Different LastName",
                "dni": "%s",
                "phone": "+34699999999",
                "email": "different@example.com",
                "address": "Different Address",
                "city": "Different City",
                "postalCode": "99999"
            }
            """.formatted(VALID_DNI);

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateDniRequest))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test that Value Objects are validated on creation.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldValidateValueObjectsOnCreation() throws Exception {
        // Test with invalid email format
        String invalidEmailRequest = """
            {
                "name": "Test Name",
                "lastName": "Test LastName",
                "dni": "12345678A",
                "phone": "+34612345678",
                "email": "invalid-email-format",
                "address": "Valid Address",
                "city": "Valid City",
                "postalCode": "12345"
            }
            """;

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidEmailRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));

        // Test with invalid phone format
        String invalidPhoneRequest = """
            {
                "name": "Test Name",
                "lastName": "Test LastName",
                "dni": "12345678A",
                "phone": "invalid-phone",
                "email": "valid@example.com",
                "address": "Valid Address",
                "city": "Valid City",
                "postalCode": "12345"
            }
            """;

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPhoneRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));

        // Test with empty address fields
        String invalidAddressRequest = """
            {
                "name": "Test Name",
                "lastName": "Test LastName",
                "dni": "12345678A",
                "phone": "+34612345678",
                "email": "valid@example.com",
                "address": "",
                "city": "",
                "postalCode": ""
            }
            """;

        mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidAddressRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    /**
     * Test deletion of owner.
     * Requirements: 8.1, 8.5
     */
    @Test
    void shouldDeleteOwnerSuccessfully() throws Exception {
        // First create an owner
        String request = createValidRequestJson();
        String response = mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(response).get("ownerId").asText();

        // Delete the owner - should return 204 No Content per REST standards
        mockMvc.perform(delete("/owner/{id}", ownerId))
                .andExpect(status().isNoContent());
    }

    /**
     * Test deletion of non-existent owner returns 404.
     * Requirements: 8.3, 8.5
     */
    @Test
    void shouldReturn404WhenDeletingNonExistentOwner() throws Exception {
        String nonExistentId = new ObjectId().toString();

        mockMvc.perform(delete("/owner/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + nonExistentId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test that deleted owner cannot be retrieved.
     * Requirements: 8.1, 8.5
     */
    @Test
    void shouldNotBeAbleToGetDeletedOwner() throws Exception {
        // First create an owner
        String request = createValidRequestJson();
        String response = mockMvc.perform(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(response).get("ownerId").asText();

        // Verify owner exists
        mockMvc.perform(get("/owner/{id}", ownerId))
                .andExpect(status().isOk());

        // Delete the owner
        mockMvc.perform(delete("/owner/{id}", ownerId))
                .andExpect(status().isNoContent());

        // Verify it's deleted by trying to get it - should return 404
        mockMvc.perform(get("/owner/{id}", ownerId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + ownerId));
    }

    /**
     * Helper method to create a valid request JSON for testing.
     */
    private String createValidRequestJson() {
        return """
            {
                "name": "%s",
                "lastName": "%s",
                "dni": "%s",
                "phone": "%s",
                "email": "%s",
                "address": "%s",
                "city": "%s",
                "postalCode": "%s"
            }
            """.formatted(
                VALID_NAME,
                VALID_LAST_NAME,
                VALID_DNI,
                VALID_PHONE,
                VALID_EMAIL,
                VALID_ADDRESS,
                VALID_CITY,
                VALID_POSTAL_CODE
            );
    }
}
