package com.datavet.datavet.pet.domain.model.details.surgery;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurgeryProcedure {

    private String name;

    private String description;

    public void validate(){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del procedimiento clinico no puede estar vacío.");
        }

        if (description != null && description.isBlank()) {
            throw new IllegalArgumentException("La descripción de la procedimiento no puede ser nula.");
        }
    }

    public static SurgeryProcedure create(String name, String description){
        SurgeryProcedure surgeryProcedure = SurgeryProcedure.builder()
                .name(name)
                .description(description)
                .build();

        surgeryProcedure.validate();

        return surgeryProcedure;
    }
}
