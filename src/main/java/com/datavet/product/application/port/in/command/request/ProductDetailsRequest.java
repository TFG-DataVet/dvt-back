package com.datavet.product.application.port.in.command.request;

import com.datavet.product.domain.valueobject.ProductCategory;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true,
        include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MedicationDetailsRequest.class,       name = "MEDICATION"),
        @JsonSubTypes.Type(value = VaccineDetailsRequest.class,          name = "VACCINE"),
        @JsonSubTypes.Type(value = FoodDetailsRequest.class,             name = "FOOD"),
        @JsonSubTypes.Type(value = SupplementDetailsRequest.class,       name = "SUPPLEMENT"),
        @JsonSubTypes.Type(value = HygieneDetailsRequest.class,          name = "HYGIENE"),
        @JsonSubTypes.Type(value = GroomingServiceDetailsRequest.class,  name = "GROOMING_SERVICE"),
        @JsonSubTypes.Type(value = AccessoryDetailsRequest.class,        name = "ACCESSORY"),
        @JsonSubTypes.Type(value = ToyDetailsRequest.class,              name = "TOY"),
        @JsonSubTypes.Type(value = MedicalSupplyDetailsRequest.class,    name = "MEDICAL_SUPPLY"),
        @JsonSubTypes.Type(value = AntiparasiticDetailsRequest.class,    name = "ANTIPARASITIC"),
        @JsonSubTypes.Type(value = DiagnosticDetailsRequest.class,       name = "DIAGNOSTIC"),
        @JsonSubTypes.Type(value = ProsthesisImplantDetailsRequest.class,name = "PROSTHESIS_IMPLANT")
})
public sealed abstract class ProductDetailsRequest
        permits MedicationDetailsRequest, VaccineDetailsRequest, FoodDetailsRequest,
                SupplementDetailsRequest, HygieneDetailsRequest, GroomingServiceDetailsRequest,
                AccessoryDetailsRequest, ToyDetailsRequest, MedicalSupplyDetailsRequest,
                AntiparasiticDetailsRequest, DiagnosticDetailsRequest,
                ProsthesisImplantDetailsRequest {

    public abstract ProductCategory getCategory();
}
