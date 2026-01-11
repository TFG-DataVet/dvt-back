package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordStatus;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MedicalRecord extends AggregateRoot<String> implements Document<String> {

    private String id;

    private String petId;

    private String clinicId;

    private MedicalRecordType type;

    private MedicalRecordStatus status;

    private String veterinarianId;

    private String notes;

//    private List<Attachments> attachments;

    private MedicalRecordDetails details;

    private LocalDateTime recordedAt;

    private LocalDateTime updatedAt;

    @Override
    public String getId() { return this.id; }

    public static MedicalRecord create(String id,
                                       String petId,
                                       String clinicId,
                                       MedicalRecordType type,
                                       MedicalRecordStatus status,
                                       String veterinarianId,
                                       String notes,
                                       MedicalRecordDetails details){

        if ( details == null) {
            throw new IllegalArgumentException("El tipo de registro médico no puede ser nulo.");
        }

        if ( type != details.getType()){
            throw new IllegalArgumentException("El tipo de registro seleccionado debe de ser del mismo tipo que el registrado.");
        }

        details.validate();

        MedicalRecord medicalRecord = MedicalRecord.builder()
                .id(id)
                .petId(petId)
                .clinicId(clinicId)
                .type(type)
                .status(status)
                .recordedAt(LocalDateTime.now())
                .veterinarianId(veterinarianId)
                .notes(notes)
                .details(details)
                .build();

        return medicalRecord;
    }

    public void changeStatus(MedicalRecordStatus newStatus, String notes){
        if (status == newStatus) {
            throw new IllegalArgumentException("Para modificar el estado del registro médico debe seleccionar otro estado diferente al actual.");
        }

        this.status = newStatus;
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

}
