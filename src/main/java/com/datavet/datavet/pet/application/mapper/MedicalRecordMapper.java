package com.datavet.datavet.pet.application.mapper;

import com.datavet.datavet.pet.application.dto.MedicalRecordResponse;
import com.datavet.datavet.pet.application.dto.MedicalRecordResponse.*;
import com.datavet.datavet.pet.domain.model.MedicalRecord;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergyDetails;
import com.datavet.datavet.pet.domain.model.details.consultation.ConsultationDetails;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisDetails;
import com.datavet.datavet.pet.domain.model.details.document.DocumentDetails;
import com.datavet.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryDetails;
import com.datavet.datavet.pet.domain.model.details.treatment.TreatmentDetails;
import com.datavet.datavet.pet.domain.model.details.vaccine.VaccineDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightDetails;

import java.util.List;

public class MedicalRecordMapper {

    private MedicalRecordMapper() {}

    public static MedicalRecordResponse toResponse(MedicalRecord record) {
        return new MedicalRecordResponse(
                record.getId(),
                record.getPetId(),
                record.getClinicId(),
                record.getCorrectedRecordId(),
                record.getType(),
                record.getStatus(),
                record.getVeterinarianId(),
                record.getNotes(),
                mapDetails(record.getDetails()),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    public static List<MedicalRecordResponse> toResponseList(List<MedicalRecord> records) {
        return records.stream()
                .map(MedicalRecordMapper::toResponse)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Despacho polimórfico de Details
    // -------------------------------------------------------------------------

    private static DetailsDto mapDetails(MedicalRecordDetails details) {
        if (details instanceof ConsultationDetails d) {
            return new ConsultationDetailsDto(
                    d.getReason(), d.getSymptoms(), d.getClinicalFindings(),
                    d.getDiagnosis(), d.getTreatmentPlan(),
                    d.isFollowUpRequired(), d.getFollowUpDate()
            );
        }
        if (details instanceof VaccineDetails d) {
            return new VaccineDetailsDto(
                    d.getVaccineName(), d.getApplicationDate(),
                    d.getNextDoseDate(), d.getBatchNumber(), d.getManufacturer()
            );
        }
        if (details instanceof TreatmentDetails d) {
            List<TreatmentDetailsDto.MedicationDto> meds = d.getMedications() == null
                    ? List.of()
                    : d.getMedications().stream()
                    .map(m -> new TreatmentDetailsDto.MedicationDto(
                            m.getName(), m.getDosage(), m.getFrequency(), m.getDurationInDays(), m.getNotes()))
                    .toList();
            return new TreatmentDetailsDto(
                    d.getTreatmentName(), d.getStartDate(), d.getInstructions(),
                    d.getEstimatedEndDate(), meds, d.getStatus().name(),
                    d.isFollowUpRequired(), d.getFollowUpDate(), d.getCompletedAt()
            );
        }
        if (details instanceof SurgeryDetails d) {
            List<String> procedures = d.getProcedures() == null ? List.of()
                    : d.getProcedures().stream()
                    .map(p -> p.getName())
                    .toList();
            List<String> postOpMeds = d.getPostOpMedications() == null ? List.of()
                    : d.getPostOpMedications().stream()
                    .map(m -> m.getName())
                    .toList();
            return new SurgeryDetailsDto(
                    d.getSurgeryName(), d.getSurgeryType().name(),
                    procedures, d.getAnesthesiaType().name(),
                    d.isHospitalizationRequired(), d.getSurgeryDate(),
                    d.getStatus().name(),
                    d.getOutcome() != null ? d.getOutcome().name() : null,
                    postOpMeds, d.getCompletedAt()
            );
        }
        if (details instanceof HospitalizationDetails d) {
            return new HospitalizationDetailsDto(
                    d.getReason(), d.getDiagnosisAtAdmission(),
                    d.getIntensiveCare() != null && d.getIntensiveCare(),
                    d.getWard(), d.getNotes(), d.getStatus().name(),
                    d.getAdmissionDate(), d.getDischargeDate(),
                    d.getCondition() != null ? d.getCondition().name() : null
            );
        }
        if (details instanceof DiagnosisDetails d) {
            return new DiagnosisDetailsDto(
                    d.getDiagnosisName(), d.getCategory().name(),
                    d.getDescription(), d.getSeverity().name(),
                    d.getDiagnosedAt(), d.isChronic(), d.isContagious(),
                    d.getSymptoms(), d.getRecommendations(),
                    d.isFollowUpRequired(), d.getFollowUpDate()
            );
        }
        if (details instanceof AllergyDetails d) {
            return new AllergyDetailsDto(
                    d.getAllergenName(), d.getType().name(), d.getSeverity().name(),
                    d.getReactions(), d.isLifeThreatening(),
                    d.getIdentifiedAt(), d.getNotes()
            );
        }
        if (details instanceof WeightDetails d) {
            return new WeightDetailsDto(d.getValue(), d.getUnit().name());
        }
        if (details instanceof DocumentDetails d) {
            return new DocumentDetailsDto(
                    d.getDocumentName(), d.getDocumentType(), d.getFileUrl(),
                    d.getMimeType(), d.getUploadedAt(), d.getUploadedBy(),
                    d.getDescription(), d.getFileSizeInBytes(), d.isConfidential()
            );
        }

        throw new IllegalArgumentException(
                "Tipo de MedicalRecordDetails no soportado en el mapper: "
                        + details.getClass().getSimpleName()
        );
    }
}