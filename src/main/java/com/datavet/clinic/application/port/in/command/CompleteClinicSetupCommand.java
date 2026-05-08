package com.datavet.clinic.application.port.in.command;

import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/**
 * Command para completar el setup de la clínica (paso 3 del onboarding).
 * Incluye datos de la clínica y datos del Employee del CLINIC_OWNER.
 */
@Value
@Builder
public class CompleteClinicSetupCommand {

    // Identificación
    @NotBlank(message = "El identicador único de la clinica es requerido.")
    String clinicId;
    String userId;

    // Datos de la clínica
    @NotBlank(message = "El nombre fiscal de la clinica es requerido.")
    @Size(max = 150, message = "El nombre fiscal de la clinia no debe de tener mas de 150 caracteres.")
    String legalName;

    @NotBlank(message = "El numero fiscal es requerido")
    @Size(max = 50, message = "El numero fiscal no debe de tener mas de 50 caracteres")
    String legalNumber;

    @NotNull(message = "El tipo de persona legal es requerido")
    LegalType legalType;

    @NotNull(message = "La direccion es requerida")
    Address address;

    @NotNull(message = "El telefono es requerido")
    Phone phone;

    @NotNull(message = "El email es requerido")
    Email email;

    @Size(max = 255)
    String logoUrl;

    @NotNull(message = "El horario de la clinica es requerido")
    ClinicSchedule schedule;

    // Datos del Employee del CLINIC_OWNER
    DocumentId ownerDocumentNumber;
    Address ownerAddress;
    String ownerAvatarUrl;
}
