package com.datavet.datavet.pet.application.factory;

import com.datavet.datavet.pet.application.port.in.command.medicalrecord.details.*;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergyDetails;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergyType;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergySeverity;
import com.datavet.datavet.pet.domain.model.details.consultation.ConsultationDetails;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisDetails;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisCategory;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisSeverity;
import com.datavet.datavet.pet.domain.model.details.document.DocumentDetails;
import com.datavet.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;
import com.datavet.datavet.pet.domain.model.details.surgery.AnesthesiaType;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryDetails;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryProcedure;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryType;
import com.datavet.datavet.pet.domain.model.details.treatment.TreatmentDetails;
import com.datavet.datavet.pet.domain.model.details.treatment.TreatmentMedication;
import com.datavet.datavet.pet.domain.model.details.vaccine.VaccineDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightUnit;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory de aplicación responsable de convertir un MedicalRecordDetailsRequest
 * (datos crudos tipados) en el MedicalRecordDetails de dominio correspondiente.
 *
 * El switch es exhaustivo gracias al sealed interface: el compilador avisa
 * si se añade un nuevo tipo sin actualizar la factory.
 */
@Component
public class MedicalRecordDetailsFactory {

    public MedicalRecordDetails create(MedicalRecordDetailsRequest request) {
        return switch (request) {

            case ConsultationDetailsRequest r -> ConsultationDetails.create(
                    r.getReason(),
                    r.getSymptoms(),
                    r.getClinicalFindings(),
                    r.getDiagnosis(),
                    r.getTreatmentPlan(),
                    r.isFollowUpRequired(),
                    r.getFollowUpDate()
            );

            case VaccineDetailsRequest r -> VaccineDetails.create(
                    r.getVaccineName(),
                    r.getApplicationDate(),
                    r.getNextDoseDate(),
                    r.getBatchNumber(),
                    r.getManufacturer()
            );

            case TreatmentDetailsRequest r -> {
                List<TreatmentMedication> meds = r.getMedications() == null ? List.of()
                        : r.getMedications().stream()
                        .map(m -> TreatmentMedication.create(
                                m.getName(), m.getDosage(), m.getFrequency(),
                                m.getDurationInDays(), m.getNotes()))
                        .toList();
                yield TreatmentDetails.create(
                        r.getTreatmentName(), r.getStartDate(), r.getInstructions(),
                        r.getEstimatedEndDate(), meds,
                        r.isFollowUpRequired(), r.getFollowUpDate()
                );
            }

            case SurgeryDetailsRequest r -> {
                List<SurgeryProcedure> procedures = r.getProcedures().stream()
                        .map(p -> SurgeryProcedure.create(p.getName(), p.getDescription()))
                        .toList();
                yield SurgeryDetails.create(
                        r.getSurgeryName(),
                        SurgeryType.valueOf(r.getSurgeryType()),
                        procedures,
                        AnesthesiaType.valueOf(r.getAnesthesiaType()),
                        r.isHospitalizationRequired(),
                        r.getSurgeryDate()
                );
            }

            case HospitalizationDetailsRequest r -> HospitalizationDetails.create(
                    r.getReason(),
                    r.getDiagnosisAtAdmission(),
                    r.getIntensiveCare(),
                    r.getWard(),
                    r.getNotes()
            );

            case DiagnosisDetailsRequest r -> DiagnosisDetails.create(
                    r.getDiagnosisName(),
                    DiagnosisCategory.valueOf(r.getCategory()),
                    r.getDescription(),
                    DiagnosisSeverity.valueOf(r.getSeverity()),
                    r.getDiagnosedAt(),
                    r.isChronic(),
                    r.isContagious(),
                    r.getSymptoms(),
                    r.getRecommendations(),
                    r.isFollowUpRequired(),
                    r.getFollowUpDate()
            );

            case AllergyDetailsRequest r -> AllergyDetails.create(
                    r.getAllergen(),
                    AllergyType.valueOf(r.getType()),
                    AllergySeverity.valueOf(r.getSeverity()),
                    r.getReactions(),
                    r.isLifeThreatening(),
                    r.getIdentifiedAt(),
                    r.getNotes()
            );

            case WeightDetailsRequest r -> WeightDetails.create(
                    r.getValue(),
                    WeightUnit.valueOf(r.getUnit())
            );

            case DocumentDetailsRequest r -> DocumentDetails.create(
                    r.getDocumentName(), r.getDocumentType(), r.getFileUrl(),
                    r.getMimeType(), r.getUploadedAt(), r.getUploadedBy(),
                    r.getDescription(), r.getFileSizeInBytes(),
                    r.isConfidential(), r.getChecksum()
            );
        };
    }
}