package com.datavet.pet.infrastructure.persistence.document;

import com.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "medical_records")
@CompoundIndexes({
        @CompoundIndex(name = "pet_idx",              def = "{'pet_id': 1}"),
        @CompoundIndex(name = "clinic_idx",           def = "{'clinic_id': 1}"),
        @CompoundIndex(name = "pet_type_idx",         def = "{'pet_id': 1, 'type': 1}"),
        @CompoundIndex(name = "pet_status_idx",       def = "{'pet_id': 1, 'status': 1}"),
        @CompoundIndex(name = "clinic_type_idx",      def = "{'clinic_id': 1, 'type': 1}"),
        @CompoundIndex(name = "corrected_record_idx", def = "{'corrected_record_id': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordDocument {

    @Id
    private String id;

    @Field("pet_id")
    private String petId;

    @Field("clinic_id")
    private String clinicId;

    @Field("corrected_record_id")
    private String correctedRecordId;

    @Field("type")
    private MedicalRecordType type;

    @Field("status")
    private MedicalRecordLifecycleStatus status;

    @Field("veterinarian_id")
    private String veterinarianId;

    @Field("notes")
    private String notes;

    // Spring Data usa _class (TypeAlias) para deserializar el subtipo correcto
    @Field("details")
    private MedicalRecordDetails details;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}