package com.datavet.clinic.infrastructure.adapter.input;

import com.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.clinic.domain.exception.ClinicAlreadyExistsException;
import com.datavet.clinic.domain.exception.ClinicNotFoundException;
import com.datavet.clinic.domain.exception.ClinicValidationException;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.validation.ValidationResult;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClinicController.class)
@DisplayName("ClinicController — Update & Deactivate Tests")
class ClinicControllerWriteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClinicUseCase clinicUseCase;

    private ObjectMapper objectMapper;
    private Clinic activeClinic;

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
    }

    // =========================================================================
    // PUT /clinic/{id}
    // =========================================================================

    @Test
    @DisplayName("PUT /clinic/{id}: should return 200 with updated clinic")
    void update_WithValidData_ShouldReturn200() throws Exception {
        when(clinicUseCase.updateClinic(any())).thenReturn(activeClinic);

        mockMvc.perform(put("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clinicId").exists())
                .andExpect(jsonPath("$.clinicName").value("Clínica Test"))
                .andExpect(jsonPath("$.legalName").value("Clínica Test S.L."))
                .andExpect(jsonPath("$.legalNumber").value("12345678A"))
                .andExpect(jsonPath("$.legalType").value("AUTONOMO"))
                .andExpect(jsonPath("$.address.street").value("Calle Test 1"))
                .andExpect(jsonPath("$.phone").value("+34912345678"))
                .andExpect(jsonPath("$.email").value("clinica@test.com"));
    }

    @Test
    @DisplayName("PUT /clinic/{id}: should return 400 when clinicName is blank")
    void update_WhenClinicNameBlank_ShouldReturn400() throws Exception {
        String json = """
                {
                    "clinicName": "",
                    "legalName": "Clínica Test S.L.",
                    "legalNumber": "12345678A",
                    "legalType": "AUTONOMO",
                    "address": "Calle Test 1",
                    "city": "Madrid",
                    "phone": "+34912345678",
                    "email": "clinica@test.com",
                    "scheduleOpenDays": "Lunes - Viernes",
                    "scheduleOpenTime": "09:00:00",
                    "scheduleCloseTime": "18:00:00"
                }
                """;

        mockMvc.perform(put("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("PUT /clinic/{id}: should return 400 when legalType is missing")
    void update_WhenLegalTypeMissing_ShouldReturn400() throws Exception {
        String json = """
                {
                    "clinicName": "Clínica Test",
                    "legalName": "Clínica Test S.L.",
                    "legalNumber": "12345678A",
                    "address": "Calle Test 1",
                    "city": "Madrid",
                    "phone": "+34912345678",
                    "email": "clinica@test.com",
                    "scheduleOpenDays": "Lunes - Viernes",
                    "scheduleOpenTime": "09:00:00",
                    "scheduleCloseTime": "18:00:00"
                }
                """;

        mockMvc.perform(put("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PUT /clinic/{id}: should return 400 when email format is invalid")
    void update_WhenEmailInvalid_ShouldReturn400() throws Exception {
        String json = validUpdateJson().replace("clinica@test.com", "no-es-un-email");

        mockMvc.perform(put("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PUT /clinic/{id}: should return 400 when phone format is invalid")
    void update_WhenPhoneInvalid_ShouldReturn400() throws Exception {
        String json = validUpdateJson().replace("+34912345678", "no-es-un-telefono");

        mockMvc.perform(put("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PUT /clinic/{id}: should return 404 when clinic not found")
    void update_WhenClinicNotFound_ShouldReturn404() throws Exception {
        when(clinicUseCase.updateClinic(any()))
                .thenThrow(new ClinicNotFoundException("Clinic", "no-existe"));

        mockMvc.perform(put("/clinic/no-existe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Clinic not found with id: no-existe"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("PUT /clinic/{id}: should return 409 when email belongs to another clinic")
    void update_WhenEmailBelongsToAnotherClinic_ShouldReturn409() throws Exception {
        when(clinicUseCase.updateClinic(any()))
                .thenThrow(new ClinicAlreadyExistsException("email", "clinica@test.com"));

        mockMvc.perform(put("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("email")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("PUT /clinic/{id}: should return 409 when legalNumber belongs to another clinic")
    void update_WhenLegalNumberBelongsToAnotherClinic_ShouldReturn409() throws Exception {
        when(clinicUseCase.updateClinic(any()))
                .thenThrow(new ClinicAlreadyExistsException("legalNumber", "12345678A"));

        mockMvc.perform(put("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("legalNumber")));
    }

    @Test
    @DisplayName("PUT /clinic/{id}: should return 400 when trying to update a deactivated clinic")
    void update_WhenClinicIsDeactivated_ShouldReturn400() throws Exception {
        ValidationResult result = new ValidationResult();
        result.addError("ClinicStatus", "No se puede actualizar una clínica desactivada");
        when(clinicUseCase.updateClinic(any()))
                .thenThrow(new ClinicValidationException(result));

        mockMvc.perform(put("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUpdateJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("desactivada")));
    }

    // =========================================================================
    // DELETE /clinic/{id}
    // =========================================================================

    @Test
    @DisplayName("DELETE /clinic/{id}: should return 204 when clinic is deactivated successfully")
    void deactivate_WhenClinicExists_ShouldReturn204() throws Exception {
        doNothing().when(clinicUseCase).deactivateClinic(eq("clinic-1"), any());

        mockMvc.perform(delete("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validDeactivateJson()))
                .andExpect(status().isNoContent());

        verify(clinicUseCase).deactivateClinic(eq("clinic-1"), eq("Cierre temporal"));
    }

    @Test
    @DisplayName("DELETE /clinic/{id}: should return 400 when reason is blank")
    void deactivate_WhenReasonIsBlank_ShouldReturn400() throws Exception {
        String json = """
                {
                    "reason": ""
                }
                """;

        mockMvc.perform(delete("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("DELETE /clinic/{id}: should return 400 when reason is missing")
    void deactivate_WhenReasonMissing_ShouldReturn400() throws Exception {
        mockMvc.perform(delete("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("DELETE /clinic/{id}: should return 404 when clinic not found")
    void deactivate_WhenClinicNotFound_ShouldReturn404() throws Exception {
        doThrow(new ClinicNotFoundException("Clinic", "no-existe"))
                .when(clinicUseCase).deactivateClinic(eq("no-existe"), any());

        mockMvc.perform(delete("/clinic/no-existe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validDeactivateJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Clinic not found with id: no-existe"));
    }

    @Test
    @DisplayName("DELETE /clinic/{id}: should return 400 when clinic is already deactivated")
    void deactivate_WhenAlreadyDeactivated_ShouldReturn400() throws Exception {
        ValidationResult result = new ValidationResult();
        result.addError("ClinicStatus", "La clínica ya está desactivada");
        doThrow(new ClinicValidationException(result))
                .when(clinicUseCase).deactivateClinic(eq("clinic-1"), any());

        mockMvc.perform(delete("/clinic/clinic-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validDeactivateJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("desactivada")));
    }

    // =========================================================================
    // Helpers — JSON válidos
    // =========================================================================

    private String validUpdateJson() {
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

    private String validDeactivateJson() {
        return """
                {
                    "reason": "Cierre temporal"
                }
                """;
    }
}