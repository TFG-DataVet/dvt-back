package com.datavet.clinic.application.port.in.command;

import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Command para completar el setup de la clínica (paso 3 del onboarding).
 * Incluye datos de la clínica y datos del Employee del CLINIC_OWNER.
 */
@Value
@Builder
public class CompleteClinicSetupCommand {

    // Identificación
    String         clinicId;
    String         userId;          // para activar el User al final

    // Datos de la clínica
    String         legalName;
    String         legalNumber;
    LegalType      legalType;
    Address        address;
    Phone          phone;
    Email          email;
    String         logoUrl;
    ClinicSchedule schedule;

    // Datos del Employee del CLINIC_OWNER

    DocumentId ownerDocumentNumber;
    Address        ownerAddress;
    LocalDate      ownerHireDate;
    String         ownerAvatarUrl;
    String         ownerSpeciality;
}