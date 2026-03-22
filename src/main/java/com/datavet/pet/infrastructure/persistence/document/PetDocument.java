package com.datavet.pet.infrastructure.persistence.document;

import com.datavet.pet.domain.model.Sex;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "pet")
@CompoundIndexes({
    @CompoundIndex(name = "clinic_id_idx", def = "{'clinicId':1}"),
    @CompoundIndex(name = "owner_id_idx", def = "{'owner.ownerId':1}"),
    @CompoundIndex(name = "chip_number_idx", def = "{'chipNumber':1}", unique = true),
    @CompoundIndex(name = "clinic_active_idx", def = "{'clinic_id': 1, 'active': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDocument {

    @Id
    private String id;

    @Field("clinic_id")
    private String clinicId;

    @Field("name")
    private String name;

    @Field("species")
    private String species;

    @Field("breed")
    private String breed;

    @Field("sex")
    private Sex sex;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Field("chip_number")
    private String chipNumber;

    @Field("avatar_url")
    private String avatarUrl;

    @Field("owner")
    private OwnerInfoDocument owner;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("active")
    private boolean active;
}
