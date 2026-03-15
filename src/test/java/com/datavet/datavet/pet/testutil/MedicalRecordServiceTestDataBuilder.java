package com.datavet.datavet.pet.testutil;

import com.datavet.datavet.pet.application.port.in.command.medicalrecord.*;
import com.datavet.datavet.pet.application.port.in.command.medicalrecord.details.*;
import com.datavet.datavet.pet.domain.model.MedicalRecord;
import com.datavet.datavet.pet.domain.model.details.vaccine.VaccineDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.pet.domain.model.action.RecordAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicalRecordServiceTestDataBuilder {

    // ── Defaults ─────────────────────────────────────────────────────────────
    public static final String DEFAULT_RECORD_ID   = "record-001";
    public static final String DEFAULT_PET_ID      = "pet-001";
    public static final String DEFAULT_CLINIC_ID   = "clinic-001";
    public static final String DEFAULT_VET_ID      = "vet-001";

    // ── Commands ─────────────────────────────────────────────────────────────

    public static CreateMedicalRecordCommand aValidCreateVaccineCommand() {
        return CreateMedicalRecordCommand.builder()
                .petId(DEFAULT_PET_ID)
                .clinicId(DEFAULT_CLINIC_ID)
                .type(MedicalRecordType.VACCINE)
                .veterinarianId(DEFAULT_VET_ID)
                .notes("Vacuna anual aplicada")
                .detailsRequest(aValidVaccineDetailsRequest())
                .build();
    }

    public static CreateMedicalRecordCommand aValidCreateConsultationCommand() {
        return CreateMedicalRecordCommand.builder()
                .petId(DEFAULT_PET_ID)
                .clinicId(DEFAULT_CLINIC_ID)
                .type(MedicalRecordType.CONSULTATION)
                .veterinarianId(DEFAULT_VET_ID)
                .notes("Consulta de rutina")
                .detailsRequest(aValidConsultationDetailsRequest())
                .build();
    }

    public static CreateMedicalRecordCommand aCreateCommandWithMismatchedType() {
        // type=CONSULTATION pero detailsRequest=VACCINE — el dominio debe rechazarlo
        return CreateMedicalRecordCommand.builder()
                .petId(DEFAULT_PET_ID)
                .clinicId(DEFAULT_CLINIC_ID)
                .type(MedicalRecordType.CONSULTATION)
                .veterinarianId(DEFAULT_VET_ID)
                .notes("Consulta")
                .detailsRequest(aValidVaccineDetailsRequest()) // ← tipo incorrecto
                .build();
    }

    public static CorrectMedicalRecordCommand aValidCorrectMedicalRecordCommand(String originalId) {
        return CorrectMedicalRecordCommand.builder()
                .originalRecordId(originalId)
                .veterinarianId(DEFAULT_VET_ID)
                .reason("Error en el número de lote")
                .detailsRequest(aValidVaccineDetailsRequest())
                .build();
    }

    public static ApplyMedicalRecordActionCommand aValidApplyActionCommand(String recordId) {
        return ApplyMedicalRecordActionCommand.builder()
                .medicalRecordId(recordId)
                .action(RecordAction.ACTIVATE)
                .veterinarianId(DEFAULT_VET_ID)
                .build();
    }

    // ── DetailsRequests ──────────────────────────────────────────────────────

    public static VaccineDetailsRequest aValidVaccineDetailsRequest() {
        return new VaccineDetailsRequest(
                "Rabia",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusYears(1),
                "BATCH-2025-001",
                "VetPharma"
        );
    }

    public static ConsultationDetailsRequest aValidConsultationDetailsRequest() {
        return new ConsultationDetailsRequest(
                "Revisión anual",
                java.util.List.of("letargia", "pérdida de apetito"),
                "Animal en buen estado general",
                "Sano",
                "Ninguno",
                false,
                null
        );
    }

    // ── Agregados ────────────────────────────────────────────────────────────

    public static MedicalRecord aValidVaccineMedicalRecord() {
        VaccineDetails details = VaccineDetails.create(
                "Rabia",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusYears(1),
                "BATCH-2025-001",
                "VetPharma"
        );
        return MedicalRecord.create(
                DEFAULT_PET_ID, DEFAULT_CLINIC_ID,
                MedicalRecordType.VACCINE, DEFAULT_VET_ID,
                "Vacuna anual", details
        );
    }

    public static MedicalRecord aValidVaccineMedicalRecordWithId(String id) {
        MedicalRecord record = aValidVaccineMedicalRecord();
        return MedicalRecord.builder()
                .id(id)
                .petId(record.getPetId())
                .clinicId(record.getClinicId())
                .type(record.getType())
                .status(MedicalRecordLifecycleStatus.ACTIVE)
                .veterinarianId(record.getVeterinarianId())
                .notes(record.getNotes())
                .details(record.getDetails())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}