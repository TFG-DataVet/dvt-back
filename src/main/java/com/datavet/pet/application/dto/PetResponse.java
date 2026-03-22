package com.datavet.pet.application.dto;

import com.datavet.pet.domain.model.Sex;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PetResponse {

    private String id;
    private String clinicId;
    private String name;
    private String species;
    private String breed;
    private Sex sex;
    private LocalDate dateOfBirth;
    private int ageInYears;
    private String chipNumber;
    private String avatarUrl;

    @JsonProperty("owner")
    private OwnerInfoDto owner;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
