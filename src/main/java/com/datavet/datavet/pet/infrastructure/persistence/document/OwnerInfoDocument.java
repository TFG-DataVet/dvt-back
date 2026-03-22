package com.datavet.datavet.pet.infrastructure.persistence.document;

import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerInfoDocument {

    @Field("owner_id")
    private String ownerId;

    @Field("name")
    private String name;

    @Field("last_name")
    private String lastName;

    @Field("phone")
    private Phone phone;
}