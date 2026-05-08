package com.datavet.appointment.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PetSnapshot {

    private String petId;
    private String name;
    private String species;

    public static PetSnapshot of(String petId, String name, String species) {
        return new PetSnapshot(petId, name, species);
    }
}
