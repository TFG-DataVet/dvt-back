package com.datavet.owner.infrastructure.adapter.input;

import com.datavet.auth.infrastructure.util.JwtUtil;
import com.datavet.owner.infrastructure.persistence.repository.MongoOwnerRepositoryAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    private static final String TEST_CLINIC_ID = "9df7c696-a3ad-48ad-85bb-210072968645";
    private static final String FAKE_TOKEN     = "fake-test-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoOwnerRepositoryAdapter ownerRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    // Test data constants
    private static final String VALID_NAME        = "Juan";
    private static final String VALID_LAST_NAME   = "Pérez";
    private static final String VALID_DNI_TYPE    = "DNI";
    private static final String VALID_DNI         = "23402587H";
    private static final String VALID_PHONE       = "+34612345678";
    private static final String VALID_EMAIL       = "juan.perez@example.com";
    private static final String VALID_ADDRESS     = "Calle Mayor 123";
    private static final String VALID_CITY        = "Madrid";
    private static final String VALID_POSTAL_CODE = "28001";

    @BeforeEach
    void setUp() {
        ownerRepository.deleteAll();

        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("test-user-id");
        when(mockClaims.get("role",       String.class)).thenReturn("CLINIC_OWNER");
        when(mockClaims.get("clinicId",   String.class)).thenReturn(TEST_CLINIC_ID);
        when(mockClaims.get("email",      String.class)).thenReturn("owner@test.com");
        when(mockClaims.get("employeeId", String.class)).thenReturn("emp-1");
        when(mockClaims.get("scope",      String.class)).thenReturn("FULL_ACCESS");

        when(jwtUtil.isTokenValid(anyString())).thenReturn(true);
        when(jwtUtil.parseToken(anyString())).thenReturn(mockClaims);
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + FAKE_TOKEN);
    }

    @Test
    void shouldCreateOwnerWithValidData() throws Exception {
        String requestJson = """
            {
                "clinicId" : "%s",
                "name": "%s",
                "lastName": "%s",
                "documentId": "%s",
                "documentNumber": "%s",
                "phone": "%s",
                "email": "%s",
                "address": "%s",
                "city": "%s",
                "postalCode": "%s",
                "acceptTermsAndCond": true
            }
            """.formatted(
                TEST_CLINIC_ID, VALID_NAME, VALID_LAST_NAME, VALID_DNI_TYPE, VALID_DNI,
                VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_CITY, VALID_POSTAL_CODE);

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.ownerId").isString())
                .andExpect(jsonPath("$.firstName").value(VALID_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andExpect(jsonPath("$.dni.documentNumber").value(VALID_DNI))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.phone").value(VALID_PHONE))
                .andExpect(jsonPath("$.address.street").value(VALID_ADDRESS))
                .andExpect(jsonPath("$.address.city").value(VALID_CITY))
                .andExpect(jsonPath("$.address.postalCode").value(VALID_POSTAL_CODE));
    }

    @Test
    void shouldReturnCreatedOwnerWithGeneratedObjectId() throws Exception {
        String requestJson = createValidRequestJson();

        String response = mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.ownerId").isString())
                .andExpect(jsonPath("$.firstName").value(VALID_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(response).get("ownerId").asText();
        assert ownerId.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

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

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson)))
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

    @Test
    void shouldGetOwnerById() throws Exception {
        String request = createValidRequestJson();
        String response = mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(response).get("ownerId").asText();

        mockMvc.perform(withAuth(get("/owner/{id}", ownerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value(VALID_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL));
    }

    @Test
    void shouldReturn404WhenOwnerNotFound() throws Exception {
        String nonExistentId = new ObjectId().toString();

        mockMvc.perform(withAuth(get("/owner/{id}", nonExistentId)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + nonExistentId))
                .andExpect(jsonPath("$.path").value("/owner/" + nonExistentId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnAllFieldsIncludingValueObjects() throws Exception {
        String request = createValidRequestJson();
        String response = mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(response).get("ownerId").asText();

        mockMvc.perform(withAuth(get("/owner/{id}", ownerId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value(VALID_NAME))
                .andExpect(jsonPath("$.lastName").value(VALID_LAST_NAME))
                .andExpect(jsonPath("$.dni").exists())
                .andExpect(jsonPath("$.dni.documentNumber").value(VALID_DNI))
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.address.street").value(VALID_ADDRESS))
                .andExpect(jsonPath("$.address.city").value(VALID_CITY))
                .andExpect(jsonPath("$.address.postalCode").value(VALID_POSTAL_CODE))
                .andExpect(jsonPath("$.address.fullAddress").exists())
                .andExpect(jsonPath("$.phone").value(VALID_PHONE))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL));
    }

    @Test
    void shouldGetAllOwners() throws Exception {
        String request1 = createValidRequestJson();
        String request2 = """
            {
                "clinicId": "%s",
                "name": "María",
                "lastName": "García",
                "documentId" : "DNI",
                "documentNumber" : "71515267X",
                "phone": "+34698765432",
                "email": "maria.garcia@example.com",
                "address": "Calle Norte 456",
                "city": "Barcelona",
                "postalCode": "08001",
                "acceptTermsAndCond" : true
            }
            """.formatted(TEST_CLINIC_ID);

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/owner")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ownerId").exists())
                .andExpect(jsonPath("$[1].ownerId").exists());
    }

    @Test
    void shouldReturnEmptyArrayWhenNoOwners() throws Exception {
        mockMvc.perform(withAuth(get("/owner")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldUpdateOwnerWithValidData() throws Exception {
        String createRequest = createValidRequestJson();
        String createResponse = mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(createResponse).get("ownerId").asText();

        String updateRequestJson = """
            {
                "clinicId" : "%s",
                "name": "Updated Name",
                "lastName": "Updated LastName",
                "documentId": "DNI",
                "documentNumber": "%s",
                "phone": "+34612345679",
                "email": "updated@example.com",
                "address": "Updated Address 789",
                "city": "Updated City",
                "postalCode": "28002"
            }
            """.formatted(TEST_CLINIC_ID, VALID_DNI);

        mockMvc.perform(withAuth(put("/owner/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Updated Name"))
                .andExpect(jsonPath("$.lastName").value("Updated LastName"))
                .andExpect(jsonPath("$.address.street").value("Updated Address 789"))
                .andExpect(jsonPath("$.address.city").value("Updated City"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentOwner() throws Exception {
        String uuid = UUID.randomUUID().toString();

        String updateRequestJson = """
            {
                "clinicId" : "%s",
                "name": "Test Name",
                "lastName": "Test LastName",
                "documentId" : "DNI",
                "documentNumber" : "71515267X",
                "phone": "+34612345678",
                "email": "test@example.com",
                "address": "Test Address",
                "city": "Test City",
                "postalCode": "12345"
            }
            """.formatted(TEST_CLINIC_ID);

        mockMvc.perform(withAuth(put("/owner/{id}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + uuid));
    }

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

        mockMvc.perform(withAuth(put("/owner/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUpdateRequestJson)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn409WhenUpdatingWithDuplicateEmail() throws Exception {
        String request1 = createValidRequestJson();
        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request1)))
                .andExpect(status().isCreated());

        String request2 = """
            {
                "clinicId": "%s",
                "name": "María",
                "lastName": "García",
                "documentId": "DNI",
                "documentNumber": "32606784Y",
                "phone": "+34698765432",
                "email": "maria.garcia@example.com",
                "address": "Calle Norte 456",
                "city": "Barcelona",
                "postalCode": "08001",
                "acceptTermsAndCond": true
            }
            """.formatted(TEST_CLINIC_ID);

        String createResponse = mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request2)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(createResponse).get("ownerId").asText();

        String updateRequestJson = """
            {
                "clinicId": "%s",
                "name": "María",
                "lastName": "García",
                "documentId": "DNI",
                "documentNumber": "71515267X",
                "phone": "+34698765432",
                "email": "%s",
                "address": "Calle Norte 456",
                "city": "Barcelona",
                "postalCode": "08001",
                "acceptTermsAndCond": true
            }
            """.formatted(TEST_CLINIC_ID, VALID_EMAIL);

        mockMvc.perform(withAuth(put("/owner/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldSucceedWhenUpdatingWithSameEmail() throws Exception {
        String createRequest = createValidRequestJson();
        String createResponse = mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(createResponse).get("ownerId").asText();

        String updateRequestJson = """
            {
                "clinicId" : "%s",
                "name": "Updated Name",
                "lastName": "Updated LastName",
                "documentId": "DNI",
                "documentNumber": "%s",
                "phone": "+34612345679",
                "email": "%s",
                "address": "Updated Address 789",
                "city": "Updated City",
                "postalCode": "28002"
            }
            """.formatted(TEST_CLINIC_ID, VALID_DNI, VALID_EMAIL);

        mockMvc.perform(withAuth(put("/owner/{id}", ownerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Updated Name"))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL));
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        String request = createValidRequestJson();
        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)))
                .andExpect(status().isCreated());

        String duplicateEmailRequest = """
            {
                "clinicId": "%s",
                "name": "Different Name",
                "lastName": "Different LastName",
                "documentId":"DNI",
                "documentNumber": "71515267X",
                "phone": "+34699999999",
                "email": "%s",
                "address": "Different Address",
                "city": "Different City",
                "postalCode": "99999",
                "acceptTermsAndCond": true
            }
            """.formatted(TEST_CLINIC_ID, VALID_EMAIL);

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateEmailRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn409WhenDniAlreadyExists() throws Exception {
        String request = createValidRequestJson();
        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)))
                .andExpect(status().isCreated());

        String duplicateDniRequest = """
            {
                "clinicId": "%s",
                "name": "Different Name",
                "lastName": "Different LastName",
                "documentId": "DNI",
                "documentNumber": "%s",
                "phone": "+34699999999",
                "email": "different@example.com",
                "address": "Different Address",
                "city": "Different City",
                "postalCode": "99999",
                "acceptTermsAndCond": true
            }
            """.formatted(TEST_CLINIC_ID, VALID_DNI);

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateDniRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldValidateValueObjectsOnCreation() throws Exception {
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

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));

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

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPhoneRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));

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

        mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidAddressRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldDeleteOwnerSuccessfully() throws Exception {
        String request = createValidRequestJson();
        String response = mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(response).get("ownerId").asText();

        mockMvc.perform(withAuth(delete("/owner/{id}", ownerId)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentOwner() throws Exception {
        String nonExistentId = new ObjectId().toString();

        mockMvc.perform(withAuth(delete("/owner/{id}", nonExistentId)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + nonExistentId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldNotBeAbleToGetDeletedOwner() throws Exception {
        String request = createValidRequestJson();
        String response = mockMvc.perform(withAuth(post("/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerId = objectMapper.readTree(response).get("ownerId").asText();

        mockMvc.perform(withAuth(get("/owner/{id}", ownerId)))
                .andExpect(status().isOk());

        mockMvc.perform(withAuth(delete("/owner/{id}", ownerId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/owner/{id}", ownerId)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Owner not found with id: " + ownerId));
    }

    private String createValidRequestJson() {
        return """
            {
                "clinicId": "%s",
                "name": "%s",
                "lastName": "%s",
                "documentId": "%s",
                "documentNumber": "%s",
                "phone": "%s",
                "email": "%s",
                "address": "%s",
                "city": "%s",
                "postalCode": "%s",
                "acceptTermsAndCond": %b
            }
            """.formatted(
                TEST_CLINIC_ID, VALID_NAME, VALID_LAST_NAME, VALID_DNI_TYPE, VALID_DNI,
                VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_CITY, VALID_POSTAL_CODE, true);
    }
}
