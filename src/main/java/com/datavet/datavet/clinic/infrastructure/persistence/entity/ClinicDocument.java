package com.datavet.datavet.clinic.infrastructure.persistence.entity;


import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "clinic")
@CompoundIndexes({
    @CompoundIndex(name = "email_idx", def = "{'email.value':1}", unique = true),
    @CompoundIndex(name = "legal_number_idx", def = "{'legalNumber':1}", unique = true)
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicDocument {

    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Field("legal_name")
    @NotBlank
    @Size(max = 100)
    private String legalName;

    @Field("legal_number")
    @NotBlank
    @Size(max = 150)
    private String legalNumber;

    @NotNull
    private Address address;

    @NotNull
    private Phone phone;

    @NotNull
    private Email email;

    @Field("logo_url")
    private String logoUrl;

    @Field("suscription_status")
    @NotBlank
    private String suscriptionStatus;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
