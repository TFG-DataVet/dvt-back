package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.pet.domain.event.medicalrecord.*;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.details.consultation.ConsultationDetails;
import com.datavet.datavet.pet.domain.model.details.consultation.ConsultationStatus;
import com.datavet.datavet.pet.domain.model.details.hospitalization.HospitalizationStatus;
import com.datavet.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
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
    //    private List<Attachments> attachments;
    private MedicalRecordDetails details;
    private LocalDateTime recordedAt;
    private LocalDateTime updatedAt;

    @Override
    public String getId() { return this.id; }

    public static MedicalRecord create(String petId,
                                       String clinicId,
                                       MedicalRecordType type,
                                       String veterinarianId,
                                       String notes,
                                       MedicalRecordDetails details){
        if ( details == null) {
            throw new IllegalArgumentException("El detalle del registro médico no puede ser nulo.");
        }

        if ( type != details.getType()){
            throw new IllegalArgumentException("El tipo de registro seleccionado debe de ser del mismo tipo que el registrado.");
        }

        String uuid = UUID.randomUUID().toString();

        details.validate();

        MedicalRecord medicalRecord = MedicalRecord.builder()
                .id(uuid)
                .petId(petId)
                .clinicId(clinicId)
                .correctedRecordId(null)
                .type(type)
                .status(MedicalRecordLifecycleStatus.ACTIVE)
                .veterinarianId(veterinarianId)
                .notes(notes)
                .details(details)
                .recordedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        medicalRecord.addDomainEvent(MedicalRecordCreatedEvent.of(uuid, petId, clinicId, type));

        return medicalRecord;
    }

    public static MedicalRecord createCorrectionOf(MedicalRecord exitingRecord,
                                                   MedicalRecordDetails correctedDetails,
                                                   String veterinarianId,
                                                   String reason){
        String uuid = UUID.randomUUID().toString();

        if ( exitingRecord.getType() != correctedDetails.getType()){
            throw new IllegalArgumentException("El tipo de registro seleccionado debe de ser del mismo tipo que el registrado.");
        }

        if (exitingRecord.getStatus() != MedicalRecordLifecycleStatus.ACTIVE) {
            throw new IllegalArgumentException("El estado del nuevo MedicalRecord debe de ser Activo.");
        }

        if (!correctedDetails.canCorrect(exitingRecord.details))
            throw new IllegalArgumentException("Los detalles corregidos no corresponden al registro original");

        correctedDetails.validate();

        MedicalRecord corrected = MedicalRecord.builder()
                .id(uuid)
                .petId(exitingRecord.petId)
                .clinicId(exitingRecord.clinicId)
                .correctedRecordId(exitingRecord.getId())
                .type(exitingRecord.type)
                .status(MedicalRecordLifecycleStatus.ACTIVE)
                .veterinarianId(veterinarianId)
                .details(correctedDetails)
                .recordedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

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
            throw new IllegalArgumentException("Solo un registro activo puede ser corregido.");

        if (!correctedRecord.correctedRecordId.equals(this.id))
            throw new IllegalArgumentException("El registro corregido no referencia a este registro.");

        this.status = MedicalRecordLifecycleStatus.CORRECTED;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(
                MedicalRecordCorrectedEvent.of(
                        this.id,
                        correctedRecord.getId(),
                        reason)
        );
    }

    public void completeConsultation(String veterinarianId){
        if (this.type != MedicalRecordType.CONSULTATION) {
            throw new IllegalArgumentException("El registro no es una consulta médica.");
        }

        ConsultationDetails consultation = (ConsultationDetails) this.details;

        ConsultationStatus previous = consultation.getStatus();
        consultation.markAsCompleted();

        addDomainEvent(
                ConsultationStatusChangedEvent.of(
                        this.id,
                        previous,
                        consultation.getStatus(),
                        veterinarianId
                )
        );
    }

    public void noShowConsultation(String veterinarianId){
        if (this.type != MedicalRecordType.CONSULTATION)
            throw new IllegalArgumentException("El registro no es un consulta médica.");

        ConsultationDetails consultation = (ConsultationDetails) this.details;

        ConsultationStatus previuos = consultation.getStatus();
        consultation.markAsNoShow();

        addDomainEvent(
                ConsultationStatusChangedEvent.of(
                        this.id,
                        previuos,
                        consultation.getStatus(),
                        veterinarianId
                )
        );
    }

    public void dischargeHospitalization(String veterinarianId){
        if (this.type != MedicalRecordType.HOSPITALIZATION){
            throw new IllegalArgumentException("El registro no es un registro hospitalario.");
        }

        HospitalizationDetails hospitalization = (HospitalizationDetails) this.details;

        HospitalizationStatus previous = hospitalization.getStatus();
        hospitalization.markAsCompleted();
        updatedAt = LocalDateTime.now();

        addDomainEvent(HospitalizationStatusChangedEvent.of(
                this.id,
                previous,
                hospitalization.getStatus(),
                veterinarianId
        ));
    }

    public void inTreatmentHospitalization(String veterinarianId){
        if (this.type != MedicalRecordType.HOSPITALIZATION){
            throw new IllegalArgumentException("El registro no es un registro hospitalario.");
        }
        HospitalizationDetails hospitalization = (HospitalizationDetails) this.details;

        HospitalizationStatus previous = hospitalization.getStatus();
        hospitalization.markAsInTreatment();
        updatedAt = LocalDateTime.now();

        addDomainEvent(
                HospitalizationStatusChangedEvent.of(
                        this.id,
                        previous,
                        hospitalization.getStatus(),
                        veterinarianId
                )
        );
    }
}
