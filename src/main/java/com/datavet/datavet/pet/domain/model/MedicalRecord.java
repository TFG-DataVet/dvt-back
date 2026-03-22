package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.pet.domain.event.medicalrecord.*;
import com.datavet.datavet.pet.domain.exception.MedicalRecordStateException;
import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Document;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MedicalRecord extends AggregateRoot<String> implements Document<String> {

    private String id;
    private String petId;
    private String clinicId;
    private String correctedRecordId;
    private MedicalRecordType type;
    private MedicalRecordLifecycleStatus status;
    private String veterinarianId;
    private String notes;
    private MedicalRecordDetails details;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String getId() { return this.id; }

    public static MedicalRecord create(String petId,
                                       String clinicId,
                                       MedicalRecordType type,
                                       String veterinarianId,
                                       String notes,
                                       MedicalRecordDetails details){
        ValidationResult result = new ValidationResult();

        if ( details == null) {
            result.addError("[details]", "El detalle del registro médico no puede ser nulo.");
        } else if ( type != details.getType()){
            result.addError("[type]", "El tipo de registro seleccionado debe de ser del mismo tipo que el registrado.");
        }

        String uuid = UUID.randomUUID().toString();

        if (!result.isValid()) {
            throw new MedicalRecordValidationException(result);
        }

        details.validate();

        MedicalRecord medicalRecord = new MedicalRecord(
                uuid,
                petId,
                clinicId,
                null,
                type,
                MedicalRecordLifecycleStatus.ACTIVE,
                veterinarianId,
                notes,
                details,
                LocalDateTime.now(),
                null);

        medicalRecord.addDomainEvent(MedicalRecordCreatedEvent.of(uuid, petId, clinicId, type));

        return medicalRecord;
    }

    public static MedicalRecord createCorrectionOf(MedicalRecord exitingRecord,
                                                   MedicalRecordDetails correctedDetails,
                                                   String veterinarianId,
                                                   String reason){
        ValidationResult result = new ValidationResult();

        String uuid = UUID.randomUUID().toString();

        if ( exitingRecord.getType() != correctedDetails.getType()){
            result.addError("[type]", "El tipo del detalle corregido debe coincidir con el tipo del registro original.");
        }

        if (exitingRecord.getStatus() != MedicalRecordLifecycleStatus.ACTIVE) {
            result.addError("[status]", "Solo se pueden crear correcciones sobre registros activos.");
        }

        if (!correctedDetails.canCorrect(exitingRecord.details))
            result.addError("[correction]", "La corrección no contiene cambios relevantes respecto al registro original.");

        if (result.hasErrors()) {
            throw new  MedicalRecordValidationException(result);
        }
        correctedDetails.validate();

        MedicalRecord corrected = new MedicalRecord(
                uuid,
                exitingRecord.petId,
                exitingRecord.clinicId,
                exitingRecord.getId(),
                exitingRecord.type,
                MedicalRecordLifecycleStatus.ACTIVE,
                veterinarianId,
                exitingRecord.notes,
                correctedDetails,
                LocalDateTime.now(),
                LocalDateTime.now());

        corrected.addDomainEvent(
                MedicalRecordCorrectionCreatedEvent.of(
                        uuid,
                        exitingRecord.id,
                        reason
                )
        );

        return corrected;
    }

    public void markAsCorrected(MedicalRecord correctedRecord, String reason){
        if (this.status != MedicalRecordLifecycleStatus.ACTIVE)
            throw new MedicalRecordStateException("Medical Record - MarkAsCorrected, no Active", "Solo un registro activo puede ser corregido.");

        if (!correctedRecord.correctedRecordId.equals(this.id))
            throw new MedicalRecordStateException("Medical Record - correctedRecord", "El registro corregido no referencia a este registro.");

        this.status = MedicalRecordLifecycleStatus.CORRECTED;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(
                MedicalRecordCorrectedEvent.of(
                        this.id,
                        correctedRecord.getId(),
                        reason)
        );
    }

    public void applyAction(RecordAction action, String veterinarianId){
        try{
            StatusChangeResult statusChange= this.details.applyAction(action);

            this.updatedAt = LocalDateTime.now();

            addDomainEvent(
                    MedicalRecordStatusChangeEvent.of(
                            this.id,
                            this.petId,
                            this.clinicId,
                            statusChange.getPreviousStatus(),
                            statusChange.getNewStatus()
                    )
            );
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public static MedicalRecord reconstitute(
            String id,
            String petId,
            String clinicId,
            String correctedRecordId,
            MedicalRecordType type,
            MedicalRecordLifecycleStatus status,
            String veterinarianId,
            String notes,
            MedicalRecordDetails details,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new MedicalRecord(
                id,
                petId,
                clinicId,
                correctedRecordId,
                type,
                status,
                veterinarianId,
                notes,
                details,
                createdAt,
                updatedAt
        );
    }
}
