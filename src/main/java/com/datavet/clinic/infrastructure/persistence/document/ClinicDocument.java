package com.datavet.clinic.infrastructure.persistence.document;

import com.datavet.clinic.domain.model.ClinicStatus;
import com.datavet.clinic.domain.model.LegalType;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "clinic")
@CompoundIndexes({
        @CompoundIndex(name = "email_idx",        def = "{'email': 1}", unique = true),
        @CompoundIndex(name = "legal_number_idx", def = "{'legalNumber': 1}", unique = true, sparse = true),
        @CompoundIndex(name = "status_idx",       def = "{'status': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicDocument {

    @Id
    private String id;

    @Field("clinic_name")
    private String name;

    @Field("legal_name")
    private String legalName;

    @Field("legal_number")
    private String legalNumber;

    @Field("legal_type")
    private LegalType legalType;

    private Address address;
    private Phone   phone;
    private String   email;

    @Field("logo_url")
    private String logoUrl;

    // Campos del schedule aplanados — evita subdocumento innecesario en Mongo
    @Field("schedule_open_days")
    private String    scheduleOpenDays;

    @Field("schedule_open_time")
    private LocalTime scheduleOpenTime;

    @Field("schedule_close_time")
    private LocalTime scheduleCloseTime;

    @Field("schedule_notes")
    private String    scheduleNotes;

    private ClinicStatus status;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}