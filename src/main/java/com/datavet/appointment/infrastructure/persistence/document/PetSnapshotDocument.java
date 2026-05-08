package com.datavet.appointment.infrastructure.persistence.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetSnapshotDocument {

    @Field("pet_id")
    private String petId;

    @Field("name")
    private String name;

    @Field("species")
    private String species;
}
