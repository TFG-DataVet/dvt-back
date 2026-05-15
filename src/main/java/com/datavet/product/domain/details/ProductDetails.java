package com.datavet.product.domain.details;

import com.datavet.product.domain.valueobject.ProductCategory;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MedicationDetails.class,        name = "MEDICATION"),
        @JsonSubTypes.Type(value = VaccineDetails.class,           name = "VACCINE"),
        @JsonSubTypes.Type(value = FoodDetails.class,              name = "FOOD"),
        @JsonSubTypes.Type(value = SupplementDetails.class,        name = "SUPPLEMENT"),
        @JsonSubTypes.Type(value = HygieneDetails.class,           name = "HYGIENE"),
        @JsonSubTypes.Type(value = GroomingServiceDetails.class,   name = "GROOMING_SERVICE"),
        @JsonSubTypes.Type(value = AccessoryDetails.class,         name = "ACCESSORY"),
        @JsonSubTypes.Type(value = ToyDetails.class,               name = "TOY"),
        @JsonSubTypes.Type(value = MedicalSupplyDetails.class,     name = "MEDICAL_SUPPLY"),
        @JsonSubTypes.Type(value = AntiparasiticDetails.class,     name = "ANTIPARASITIC"),
        @JsonSubTypes.Type(value = DiagnosticDetails.class,        name = "DIAGNOSTIC"),
        @JsonSubTypes.Type(value = ProsthesisImplantDetails.class, name = "PROSTHESIS_IMPLANT")
})
public abstract class ProductDetails {
    public abstract ProductCategory getCategory();
    public abstract void validate();
}
