package com.datavet.datavet.pet.application.port.in.command.medicalrecord.details;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "detailsType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConsultationDetailsRequest.class,    name = "CONSULTATION"),
        @JsonSubTypes.Type(value = VaccineDetailsRequest.class,         name = "VACCINE"),
        @JsonSubTypes.Type(value = TreatmentDetailsRequest.class,       name = "TREATMENT"),
        @JsonSubTypes.Type(value = SurgeryDetailsRequest.class,         name = "SURGERY"),
        @JsonSubTypes.Type(value = HospitalizationDetailsRequest.class, name = "HOSPITALIZATION"),
        @JsonSubTypes.Type(value = DiagnosisDetailsRequest.class,       name = "DIAGNOSIS"),
        @JsonSubTypes.Type(value = AllergyDetailsRequest.class,         name = "ALLERGY"),
        @JsonSubTypes.Type(value = WeightDetailsRequest.class,          name = "WEIGHT"),
        @JsonSubTypes.Type(value = DocumentDetailsRequest.class,        name = "DOCUMENT")
})

public sealed interface MedicalRecordDetailsRequest
        permits ConsultationDetailsRequest,
        VaccineDetailsRequest,
        TreatmentDetailsRequest,
        SurgeryDetailsRequest,
        HospitalizationDetailsRequest,
        DiagnosisDetailsRequest,
        AllergyDetailsRequest,
        WeightDetailsRequest,
        DocumentDetailsRequest {
}