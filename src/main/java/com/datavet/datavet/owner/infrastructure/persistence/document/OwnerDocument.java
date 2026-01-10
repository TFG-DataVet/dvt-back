package com.datavet.datavet.owner.infrastructure.persistence.document;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
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

/**
 * MongoDB document representing an Owner entity.
 * Maps to the "owners" collection in MongoDB.
 */
@Document(collection = "owners")
@CompoundIndexes({
    @CompoundIndex(name = "email_idx", def = "{'email.value': 1}", unique = true),
    @CompoundIndex(name = "dni_idx", def = "{'dni': 1}", unique = true),
    @CompoundIndex(name = "phone_idx", def = "{'phone.value': 1}", unique = true),
    @CompoundIndex(name = "clinic_idx", def = "{'clinic_id': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDocument {

    @Id
    private String id;

    @Field("clinic_id")
    private String clinicId;

    @Field("first_name")
    @NotBlank
    @Size(max = 50)
    private String firstName;

    @Field("last_name")
    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Size(min = 9, max = 9)
    private String dni;

    private Phone phone;

    private Email email;

    private Address address;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
