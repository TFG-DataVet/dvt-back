package com.datavet.owner.infrastructure.persistence.document;

import com.datavet.shared.domain.valueobject.Email;
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

/**
 * MongoDB document representing an Owner entity.
 * Maps to the "owners" collection in MongoDB.
 *
 * NOTE: No business logic, no Bean Validation — pure data container.
 * Conversion to/from domain lives exclusively in OwnerRepositoryAdapter.
 */
@Document(collection = "owners")
@CompoundIndexes({
        @CompoundIndex(name = "email_idx",          def = "{'email': 1}",           unique = true),
        @CompoundIndex(name = "document_number_idx", def = "{'document_number': 1}", unique = true),
        @CompoundIndex(name = "phone_idx",           def = "{'phone': 1}",           unique = true),
        @CompoundIndex(name = "clinic_idx",          def = "{'clinic_id': 1}")
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
    private String firstName;

    @Field("last_name")
    private String lastName;

    // DocumentId aplanado — evita problemas de deserialización con Value Objects
    @Field("document_type")
    private String documentType;

    @Field("document_number")
    private String documentNumber;

    // Phone, Email y Address como Strings/campos primitivos
    private String phone;

    private String email;

    // Address aplanada — evita wrappers innecesarios en MongoDB
    private String address;

    private String city;

    @Field("postal_code")
    private String postalCode;

    @Field("pet_ids")
    private List<String> petIds;

    @Field("avatar_url")
    private String avatarUrl;

    private boolean active;

    @Field("accept_terms_and_cond")
    private boolean acceptTermsAndCond;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}