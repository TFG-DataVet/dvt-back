package com.datavet.pet.application.dto;

import com.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MedicalRecordResponse {

    private String id;
    private String petId;
    private String clinicId;
    private String correctedRecordId;
    private MedicalRecordType type;
    private MedicalRecordLifecycleStatus status;
    private String veterinarianId;
    private String notes;

    @JsonProperty("details")
    private DetailsDto details;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "detailType",
            visible = true
    )

    @JsonSubTypes({
            @JsonSubTypes.Type(value = ConsultationDetailsDto.class,    name = "CONSULTATION"),
            @JsonSubTypes.Type(value = VaccineDetailsDto.class,         name = "VACCINE"),
            @JsonSubTypes.Type(value = TreatmentDetailsDto.class,       name = "TREATMENT"),
            @JsonSubTypes.Type(value = SurgeryDetailsDto.class,         name = "SURGERY"),
            @JsonSubTypes.Type(value = HospitalizationDetailsDto.class, name = "HOSPITALIZATION"),
            @JsonSubTypes.Type(value = DiagnosisDetailsDto.class,       name = "DIAGNOSIS"),
            @JsonSubTypes.Type(value = AllergyDetailsDto.class,         name = "ALLERGY"),
            @JsonSubTypes.Type(value = WeightDetailsDto.class,          name = "WEIGHT"),
            @JsonSubTypes.Type(value = DocumentDetailsDto.class,        name = "DOCUMENT")
    })

    @Getter
    @AllArgsConstructor
    public abstract static class DetailsDto {
        private String detailType;
    }

    // --- CONSULTATION --------------------------------------------------------

    @Getter
    public static class ConsultationDetailsDto extends DetailsDto {
        private String reason;
        private List<String> symptoms;
        private String clinicalFindings;
        private String diagnosis;
        private String treatmentPlan;
        private boolean followUpRequired;
        private LocalDate followUpDate;

        public ConsultationDetailsDto(String reason, List<String> symptoms,
                                      String clinicalFindings, String diagnosis, String treatmentPlan,
                                      boolean followUpRequired, LocalDate followUpDate) {
            super("CONSULTATION");
            this.reason = reason;
            this.symptoms = symptoms;
            this.clinicalFindings = clinicalFindings;
            this.diagnosis = diagnosis;
            this.treatmentPlan = treatmentPlan;
            this.followUpRequired = followUpRequired;
            this.followUpDate = followUpDate;
        }
    }

    // --- VACCINE -------------------------------------------------------------

    @Getter
    public static class VaccineDetailsDto extends DetailsDto {
        private String vaccineName;
        private LocalDate applicationDate;
        private LocalDate nextDoseDate;
        private String batchNumber;
        private String manufacturer;

        public VaccineDetailsDto(String vaccineName, LocalDate applicationDate,
                                 LocalDate nextDoseDate, String batchNumber, String manufacturer) {
            super("VACCINE");
            this.vaccineName = vaccineName;
            this.applicationDate = applicationDate;
            this.nextDoseDate = nextDoseDate;
            this.batchNumber = batchNumber;
            this.manufacturer = manufacturer;
        }
    }

    // --- TREATMENT -----------------------------------------------------------

    @Getter
    public static class TreatmentDetailsDto extends DetailsDto {
        private String treatmentName;
        private LocalDate startDate;
        private String instructions;
        private LocalDate estimatedEndDate;
        private List<MedicationDto> medications;
        private String status;
        private boolean followUpRequired;
        private LocalDate followUpDate;
        private LocalDate completedAt;

        public TreatmentDetailsDto(String treatmentName, LocalDate startDate,
                                   String instructions, LocalDate estimatedEndDate,
                                   List<MedicationDto> medications, String status,
                                   boolean followUpRequired, LocalDate followUpDate, LocalDate completedAt) {
            super("TREATMENT");
            this.treatmentName = treatmentName;
            this.startDate = startDate;
            this.instructions = instructions;
            this.estimatedEndDate = estimatedEndDate;
            this.medications = medications;
            this.status = status;
            this.followUpRequired = followUpRequired;
            this.followUpDate = followUpDate;
            this.completedAt = completedAt;
        }

        @Getter
        @AllArgsConstructor
        public static class MedicationDto {
            private String name;
            private String dosage;
            private String frequency;
            private Integer durationInDays;
            private String notes;
        }
    }

    // --- SURGERY -------------------------------------------------------------

    @Getter
    public static class SurgeryDetailsDto extends DetailsDto {
        private String surgeryName;
        private String surgeryType;
        private List<String> procedures;
        private String anesthesiaType;
        private boolean hospitalizationRequired;
        private LocalDateTime surgeryDate;
        private String status;
        private String outcome;
        private List<String> postOpMedications;
        private LocalDateTime completedAt;

        public SurgeryDetailsDto(String surgeryName, String surgeryType,
                                 List<String> procedures, String anesthesiaType,
                                 boolean hospitalizationRequired, LocalDateTime surgeryDate,
                                 String status, String outcome, List<String> postOpMedications,
                                 LocalDateTime completedAt) {
            super("SURGERY");
            this.surgeryName = surgeryName;
            this.surgeryType = surgeryType;
            this.procedures = procedures;
            this.anesthesiaType = anesthesiaType;
            this.hospitalizationRequired = hospitalizationRequired;
            this.surgeryDate = surgeryDate;
            this.status = status;
            this.outcome = outcome;
            this.postOpMedications = postOpMedications;
            this.completedAt = completedAt;
        }
    }

    // --- HOSPITALIZATION -----------------------------------------------------

    @Getter
    public static class HospitalizationDetailsDto extends DetailsDto {
        private String reason;
        private String diagnosisAtAdmission;
        private boolean intensiveCare;
        private String ward;
        private String notes;
        private String status;
        private LocalDateTime admissionDate;
        private LocalDateTime dischargeDate;
        private String condition;

        public HospitalizationDetailsDto(String reason, String diagnosisAtAdmission,
                                         boolean intensiveCare, String ward, String notes, String status,
                                         LocalDateTime admissionDate, LocalDateTime dischargeDate, String condition) {
            super("HOSPITALIZATION");
            this.reason = reason;
            this.diagnosisAtAdmission = diagnosisAtAdmission;
            this.intensiveCare = intensiveCare;
            this.ward = ward;
            this.notes = notes;
            this.status = status;
            this.admissionDate = admissionDate;
            this.dischargeDate = dischargeDate;
            this.condition = condition;
        }
    }

    // --- DIAGNOSIS -----------------------------------------------------------

    @Getter
    public static class DiagnosisDetailsDto extends DetailsDto {
        private String diagnosisName;
        private String category;
        private String description;
        private String severity;
        private LocalDate diagnosedAt;
        private boolean chronic;
        private boolean contagious;
        private List<String> symptoms;
        private List<String> recommendations;
        private boolean followUpRequired;
        private LocalDate followUpDate;

        public DiagnosisDetailsDto(String diagnosisName, String category,
                                   String description, String severity, LocalDate diagnosedAt,
                                   boolean chronic, boolean contagious, List<String> symptoms,
                                   List<String> recommendations, boolean followUpRequired,
                                   LocalDate followUpDate) {
            super("DIAGNOSIS");
            this.diagnosisName = diagnosisName;
            this.category = category;
            this.description = description;
            this.severity = severity;
            this.diagnosedAt = diagnosedAt;
            this.chronic = chronic;
            this.contagious = contagious;
            this.symptoms = symptoms;
            this.recommendations = recommendations;
            this.followUpRequired = followUpRequired;
            this.followUpDate = followUpDate;
        }
    }

    // --- ALLERGY -------------------------------------------------------------

    @Getter
    public static class AllergyDetailsDto extends DetailsDto {
        private String allergen;
        private String type;
        private String severity;
        private List<String> reactions;
        private boolean lifeThreatening;
        private LocalDate identifiedAt;
        private String notes;

        public AllergyDetailsDto(String allergen, String type, String severity,
                                 List<String> reactions, boolean lifeThreatening,
                                 LocalDate identifiedAt, String notes) {
            super("ALLERGY");
            this.allergen = allergen;
            this.type = type;
            this.severity = severity;
            this.reactions = reactions;
            this.lifeThreatening = lifeThreatening;
            this.identifiedAt = identifiedAt;
            this.notes = notes;
        }
    }

    // --- WEIGHT --------------------------------------------------------------

    @Getter
    public static class WeightDetailsDto extends DetailsDto {
        private Double value;
        private String unit;

        public WeightDetailsDto(Double value, String unit) {
            super("WEIGHT");
            this.value = value;
            this.unit = unit;
        }
    }

    // --- DOCUMENT ------------------------------------------------------------

    @Getter
    public static class DocumentDetailsDto extends DetailsDto {
        private String documentName;
        private String documentType;
        private String fileUrl;
        private String mimeType;
        private LocalDateTime uploadedAt;
        private String uploadedBy;
        private String description;
        private Long fileSizeInBytes;
        private boolean confidential;

        public DocumentDetailsDto(String documentName, String documentType,
                                  String fileUrl, String mimeType, LocalDateTime uploadedAt,
                                  String uploadedBy, String description, Long fileSizeInBytes,
                                  boolean confidential) {
            super("DOCUMENT");
            this.documentName = documentName;
            this.documentType = documentType;
            this.fileUrl = fileUrl;
            this.mimeType = mimeType;
            this.uploadedAt = uploadedAt;
            this.uploadedBy = uploadedBy;
            this.description = description;
            this.fileSizeInBytes = fileSizeInBytes;
            this.confidential = confidential;
        }
    }
}