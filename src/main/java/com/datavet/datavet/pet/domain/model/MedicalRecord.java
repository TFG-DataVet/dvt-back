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
@NoArgsConstructor
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
            throw new IllegalArgumentException("MedicalRecord details cannot be null");
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

    public void update(MedicalRecordType type, MedicalRecordStatus status, MedicalRecordDetails details){

        if (details == null){
            throw new IllegalArgumentException("MedicalRecord details cannot be null");
        }

        this.type = type;
        this.status = status;
        this.details = details;
        this.updatedAt = LocalDateTime.now();
    }

}
