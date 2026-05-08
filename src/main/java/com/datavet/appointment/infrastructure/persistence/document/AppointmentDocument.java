package com.datavet.appointment.infrastructure.persistence.document;

import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "appointments")
@CompoundIndexes({
        @CompoundIndex(name = "clinic_idx",       def = "{'clinic_id': 1}"),
        @CompoundIndex(name = "clinic_date_idx",  def = "{'clinic_id': 1, 'scheduled_at': 1}"),
        @CompoundIndex(name = "clinic_status_idx",def = "{'clinic_id': 1, 'status': 1}"),
        @CompoundIndex(name = "owner_idx",        def = "{'owner_id': 1}", sparse = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDocument {

    @Id
    private String id;

    @Field("clinic_id")
    private String clinicId;

    @Field("emergency")
    private boolean emergency;

    @Field("type")
    private AppointmentType type;

    @Field("status")
    private AppointmentStatus status;

    @Field("scheduled_at")
    private LocalDateTime scheduledAt;

    @Field("owner_id")
    private String ownerId;

    @Field("owner_name")
    private String ownerName;

    @Field("owner_email")
    private String ownerEmail;

    @Field("owner_phone")
    private String ownerPhone;

    @Field("pet")
    private PetSnapshotDocument pet;

    @Field("creation_employee_id")
    private String creationEmployeeId;

    @Field("medical_employee_id")
    private String medicalEmployeeId;

    @Field("notes")
    private String notes;

    @Field("product_ids")
    private List<String> productIds;

    @Field("source")
    private AppointmentSource source;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
