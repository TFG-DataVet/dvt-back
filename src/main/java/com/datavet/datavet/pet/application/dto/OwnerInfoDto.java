package com.datavet.datavet.pet.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerInfoDto {
    private String ownerId;
    private String name;
    private String lastName;

    /** Extrae el valor del Value Object Phone. */
    private String phone;

    @JsonProperty("fullName")
    public String getFullName() {
        return name + " " + lastName;
    }
}
