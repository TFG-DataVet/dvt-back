package com.datavet.clinic.infrastructure.adapter.input;

import com.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.clinic.domain.exception.ClinicNotFoundException;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClinicController.class)
@DisplayName("ClinicController — Register, Create, CompleteSetup & Read Tests")
class ClinicControllerReadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClinicUseCase clinicUseCase;

    private ObjectMapper objectMapper;
    private Clinic activeClinic;
    private Clinic pendingClinic;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Address address  = new Address("Calle Test 1", "Madrid", "28001");
        Phone   phone    = new Phone("+34912345678");
        Email   email    = new Email("clinica@test.com");
        ClinicSchedule schedule = ClinicSchedule.of(
                "Lunes - Viernes", LocalTime.of(9, 0), LocalTime.of(18, 0), "Cierra fines de semana");

        activeClinic = Clinic.create(
                "Clínica Test", "Clínica Test S.L.", "12345678A",
                LegalType.AUTONOMO, address, phone, email,
                "https://example.com/logo.png", schedule);
        activeClinic.clearDomainEvents();

        pendingClinic = Clinic.createPending("Clínica Test", email, phone);
        pendingClinic.clearDomainEvents();
    }

    // =========================================================================
    // POST /clinic/register
    // =========================================================================

    @Test
    @DisplayName("POST /clinic/register: should return 201 with pending clinic")
    void register_WithValidData_ShouldReturn201() throws Exception {
        when(clinicUseCase.createPendingClinic(any())).thenReturn(pendingClinic);

        mockMvc.perform(post("/clinic/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clinicId").exists())
                .andExpect(jsonPath("$.clinicName").value("Clínica Test"))
                .andExpect(jsonPath("$.status").value("PENDING_SETUP"));
    }

    @Test
    @DisplayName("POST /clinic/register: should return 400 when clinicName is blank")
    void register_WhenClinicNameBlank_ShouldReturn400() throws Exception {
        String json = """
                {
                    "clinicName": "",
                    "email": "clinica@test.com",
                    "phone": "+34912345678"
                }
                """;

        mockMvc.perform(post("/clinic/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("POST /clinic/register: should return 400 when email format is invalid")
    void register_WhenEmailInvalid_ShouldReturn400() throws Exception {
        String json = """
                {
                    "clinicName": "Clínica Test",
                    "email": "no-es-un-email",
                    "phone": "+34912345678"
                }
                """;

        mockMvc.perform(post("/clinic/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /clinic/register: should return 400 when phone format is invalid")
    void register_WhenPhoneInvalid_ShouldReturn400() throws Exception {
        String json = """
                {
                    "clinicName": "Clínica Test",
                    "email": "clinica@test.com",
                    "phone": "no-es-un-telefono"
                }
                """;

        mockMvc.perform(post("/clinic/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /clinic/register: should return 409 when email already exists")
    void register_WhenEmailAlreadyExists_ShouldReturn409() throws Exception {
        when(clinicUseCase.createPendingClinic(any()))
                .thenThrow(new ClinicAlreadyExistsException("email", "clinica@test.com"));

        mockMvc.perform(post("/clinic/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("email")));
    }

    // =========================================================================
    // POST /clinic
    // =========================================================================

    @Test
    @DisplayName("POST /clinic: should return 201 with fully configured clinic")
    void create_WithValidData_ShouldReturn201() throws Exception {
        when(clinicUseCase.createClinic(any())).thenReturn(activeClinic);

        mockMvc.perform(post("/clinic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clinicId").exists())
                .andExpect(jsonPath("$.clinicName").value("Clínica Test"))
                .andExpect(jsonPath("$.legalName").value("Clínica Test S.L."))
                .andExpect(jsonPath("$.legalNumber").value("12345678A"))
                .andExpect(jsonPath("$.legalType").value("AUTONOMO"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.address.street").value("Calle Test 1"))
                .andExpect(jsonPath("$.address.city").value("Madrid"))
                .andExpect(jsonPath("$.address.postalCode").value("28001"))
                .andExpect(jsonPath("$.address.fullAddress").exists())
                .andExpect(jsonPath("$.phone").value("+34912345678"))
                .andExpect(jsonPath("$.email").value("clinica@test.com"))
                .andExpect(jsonPath("$.schedule.openDays").value("Lunes - Viernes"))
                .andExpect(jsonPath("$.schedule.openTime").value("09:00:00"))
                .andExpect(jsonPath("$.schedule.closeTime").value("18:00:00"));
    }

    @Test
    @DisplayName("POST /clinic: should return 400 when clinicName is blank")
    void create_WhenClinicNameBlank_ShouldReturn400() throws Exception {
        String json = validCreateJson().replace("\"Clínica Test\"", "\"\"");

        mockMvc.perform(post("/clinic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("POST /clinic: should return 400 when legalType is missing")
    void create_WhenLegalTypeMissing_ShouldReturn400() throws Exception {
        String json = """
                {
                    "clinicName": "Clínica Test",
                    "legalName": "Clínica Test S.L.",
                    "legalNumber": "12345678A",
                    "address": "Calle Test 1",
                    "city": "Madrid",
                    "codePostal": "28001",
                    "phone": "+34912345678",
                    "email": "clinica@test.com",
                    "scheduleOpenDays": "Lunes - Viernes",
                    "scheduleOpenTime": "09:00:00",
                    "scheduleCloseTime": "18:00:00"
                }
                """;

        mockMvc.perform(post("/clinic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /clinic: should return 400 when schedule fields are missing")
    void create_WhenScheduleMissing_ShouldReturn400() throws Exception {
        String json = """
                {
                    "clinicName": "Clínica Test",
                    "legalName": "Clínica Test S.L.",
                    "legalNumber": "12345678A",
                    "legalType": "AUTONOMO",
                    "address": "Calle Test 1",
                    "city": "Madrid",
                    "phone": "+34912345678",
                    "email": "clinica@test.com"
                }
                """;

        mockMvc.perform(post("/clinic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /clinic: should return 409 when email already exists")
    void create_WhenEmailAlreadyExists_ShouldReturn409() throws Exception {
        when(clinicUseCase.createClinic(any()))
                .thenThrow(new ClinicAlreadyExistsException("email", "clinica@test.com"));

        mockMvc.perform(post("/clinic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("email")));
    }

    @Test
    @DisplayName("POST /clinic: should return 409 when legalNumber already exists")
    void create_WhenLegalNumberAlreadyExists_ShouldReturn409() throws Exception {
        when(clinicUseCase.createClinic(any()))
                .thenThrow(new ClinicAlreadyExistsException("legalNumber", "12345678A"));

        mockMvc.perform(post("/clinic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("legalNumber")));
    }

    // =========================================================================
    // PATCH /clinic/{id}/complete-setup
    // =========================================================================

    @Test
    @DisplayName("PATCH /clinic/{id}/complete-setup: should return 200 with active clinic")
    void completeSetup_WithValidData_ShouldReturn200() throws Exception {
        when(clinicUseCase.completeClinicSetup(any())).thenReturn(activeClinic);

        mockMvc.perform(patch("/clinic/clinic-1/complete-setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCompleteSetupJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clinicId").exists())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.legalName").value("Clínica Test S.L."))
                .andExpect(jsonPath("$.legalNumber").value("12345678A"));
    }

    @Test
    @DisplayName("PATCH /clinic/{id}/complete-setup: should return 400 when legalName is blank")
    void completeSetup_WhenLegalNameBlank_ShouldReturn400() throws Exception {
        String json = validCompleteSetupJson().replace("\"Clínica Test S.L.\"", "\"\"");

        mockMvc.perform(patch("/clinic/clinic-1/complete-setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PATCH /clinic/{id}/complete-setup: should return 404 when clinic not found")
    void completeSetup_WhenClinicNotFound_ShouldReturn404() throws Exception {
        when(clinicUseCase.completeClinicSetup(any()))
                .thenThrow(new ClinicNotFoundException("Clinic", "no-existe"));

        mockMvc.perform(patch("/clinic/no-existe/complete-setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCompleteSetupJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("no-existe")));
    }

    @Test
    @DisplayName("PATCH /clinic/{id}/complete-setup: should return 409 when legalNumber already exists")
    void completeSetup_WhenLegalNumberAlreadyExists_ShouldReturn409() throws Exception {
        when(clinicUseCase.completeClinicSetup(any()))
                .thenThrow(new ClinicAlreadyExistsException("legalNumber", "12345678A"));

        mockMvc.perform(patch("/clinic/clinic-1/complete-setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCompleteSetupJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("legalNumber")));
    }

    // =========================================================================
    // GET /clinic/{id}
    // =========================================================================

    @Test
    @DisplayName("GET /clinic/{id}: should return 200 with clinic and all fields")
    void getById_WhenClinicExists_ShouldReturn200() throws Exception {
        when(clinicUseCase.getClinicById("clinic-1")).thenReturn(activeClinic);

        mockMvc.perform(get("/clinic/clinic-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clinicId").exists())
                .andExpect(jsonPath("$.clinicName").value("Clínica Test"))
                .andExpect(jsonPath("$.legalName").value("Clínica Test S.L."))
                .andExpect(jsonPath("$.legalNumber").value("12345678A"))
                .andExpect(jsonPath("$.legalType").value("AUTONOMO"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.address.street").value("Calle Test 1"))
                .andExpect(jsonPath("$.address.city").value("Madrid"))
                .andExpect(jsonPath("$.address.postalCode").value("28001"))
                .andExpect(jsonPath("$.address.fullAddress").value("Calle Test 1, Madrid 28001"))
                .andExpect(jsonPath("$.phone").value("+34912345678"))
                .andExpect(jsonPath("$.email").value("clinica@test.com"))
                .andExpect(jsonPath("$.schedule.openDays").value("Lunes - Viernes"));
    }

    @Test
    @DisplayName("GET /clinic/{id}: should return 404 when clinic not found")
    void getById_WhenClinicNotFound_ShouldReturn404() throws Exception {
        when(clinicUseCase.getClinicById("no-existe"))
                .thenThrow(new ClinicNotFoundException("Clinic", "no-existe"));

        mockMvc.perform(get("/clinic/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Clinic not found with id: no-existe"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // =========================================================================
    // GET /clinic
    // =========================================================================

    @Test
    @DisplayName("GET /clinic: should return 200 with list of clinics")
    void getAll_ShouldReturn200WithList() throws Exception {
        when(clinicUseCase.getAllClinics()).thenReturn(List.of(activeClinic, activeClinic));

        mockMvc.perform(get("/clinic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clinicId").exists())
                .andExpect(jsonPath("$[0].clinicName").value("Clínica Test"));
    }

    @Test
    @DisplayName("GET /clinic: should return 200 with empty list when no clinics exist")
    void getAll_WhenNoClinics_ShouldReturn200WithEmptyList() throws Exception {
        when(clinicUseCase.getAllClinics()).thenReturn(List.of());

        mockMvc.perform(get("/clinic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // =========================================================================
    // Pending clinic — response fields
    // =========================================================================

    @Test
    @DisplayName("POST /clinic/register: pending clinic response should have null legal fields")
    void register_PendingClinicResponse_ShouldHaveNullLegalFields() throws Exception {
        when(clinicUseCase.createPendingClinic(any())).thenReturn(pendingClinic);

        mockMvc.perform(post("/clinic/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.legalName").doesNotExist())
                .andExpect(jsonPath("$.legalNumber").doesNotExist())
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.schedule").doesNotExist());
    }

    // =========================================================================
    // Helpers — JSON válidos
    // =========================================================================

    private String validRegisterJson() {
        return """
                {
                    "clinicName": "Clínica Test",
                    "email": "clinica@test.com",
                    "phone": "+34912345678"
                }
                """;
    }

    private String validCreateJson() {
        return """
                {
                    "clinicName": "Clínica Test",
                    "legalName": "Clínica Test S.L.",
                    "legalNumber": "12345678A",
                    "legalType": "AUTONOMO",
                    "address": "Calle Test 1",
                    "city": "Madrid",
                    "codePostal": "28001",
                    "phone": "+34912345678",
                    "email": "clinica@test.com",
                    "logoUrl": "https://example.com/logo.png",
                    "scheduleOpenDays": "Lunes - Viernes",
                    "scheduleOpenTime": "09:00:00",
                    "scheduleCloseTime": "18:00:00",
                    "scheduleNotes": "Cierra fines de semana"
                }
                """;
    }

    private String validCompleteSetupJson() {
        return """
                {
                    "legalName": "Clínica Test S.L.",
                    "legalNumber": "12345678A",
                    "legalType": "AUTONOMO",
                    "address": "Calle Test 1",
                    "city": "Madrid",
                    "codePostal": "28001",
                    "phone": "+34912345678",
                    "email": "clinica@test.com",
                    "scheduleOpenDays": "Lunes - Viernes",
                    "scheduleOpenTime": "09:00:00",
                    "scheduleCloseTime": "18:00:00"
                }
                """;
    }
}