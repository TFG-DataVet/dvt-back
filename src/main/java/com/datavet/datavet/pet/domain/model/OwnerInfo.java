package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OwnerInfo {

    String ownerId;
    String name;
    String lastName;
    Phone phone;

    public String getFullName() {
        return name + " " + lastName;
    }

    public static OwnerInfo from(String ownerId, String name, String lastName, Phone phone) {
        return OwnerInfo.builder()
                .ownerId(ownerId)
                .name(name)
                .lastName(lastName)
                .phone(phone)
                .build();
    }
}
