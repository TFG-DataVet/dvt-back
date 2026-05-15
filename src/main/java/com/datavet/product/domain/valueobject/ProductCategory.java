package com.datavet.product.domain.valueobject;

import java.util.Arrays;
import java.util.List;

public enum ProductCategory {
    MEDICATION,
    VACCINE,
    ANTIPARASITIC,
    SUPPLEMENT,
    FOOD,
    HYGIENE,
    ACCESSORY,
    TOY,
    MEDICAL_SUPPLY,
    DIAGNOSTIC,
    PROSTHESIS_IMPLANT,
    GROOMING_SERVICE;

    public ClinicArea getArea() {
        return switch (this) {
            case MEDICATION, VACCINE, ANTIPARASITIC, SUPPLEMENT -> ClinicArea.PHARMACY;
            case FOOD, HYGIENE, ACCESSORY, TOY                  -> ClinicArea.STORE;
            case MEDICAL_SUPPLY, DIAGNOSTIC, PROSTHESIS_IMPLANT -> ClinicArea.CLINIC;
            case GROOMING_SERVICE                                -> ClinicArea.GROOMING;
        };
    }

    public static List<ProductCategory> ofArea(ClinicArea area) {
        return Arrays.stream(values())
                .filter(c -> c.getArea() == area)
                .toList();
    }
}