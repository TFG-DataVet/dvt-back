package com.datavet.datavet.pet.infrastructure.adapter.output;

import com.datavet.datavet.pet.application.port.out.MedicalRecordPort;
import com.datavet.datavet.pet.domain.model.MedicalRecord;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.details.allergy.AllergyDetails;
import com.datavet.datavet.pet.domain.model.details.consultation.ConsultationDetails;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisDetails;
import com.datavet.datavet.pet.domain.model.details.document.DocumentDetails;
import com.datavet.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryDetails;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryMedication;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryOutcome;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryProcedure;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryStatus;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryType;
import com.datavet.datavet.pet.domain.model.details.treatment.TreatmentDetails;
import com.datavet.datavet.pet.domain.model.details.treatment.TreatmentMedication;
import com.datavet.datavet.pet.domain.model.details.treatment.TreatmentStatus;
import com.datavet.datavet.pet.domain.model.details.vaccine.VaccineDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.pet.infrastructure.persistence.document.MedicalRecordDocument;
import com.datavet.datavet.pet.infrastructure.persistence.repository.MongoMedicalRecordRepositoryAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MedicalRecordRepositoryAdapter implements MedicalRecordPort {

    private final MongoMedicalRecordRepositoryAdapter repository;

    private MedicalRecordDocument toDocument(MedicalRecord medicalRecord) {
        return MedicalRecordDocument.builder()
                .id(medicalRecord.getId())
                .petId(medicalRecord.getPetId())
                .clinicId(medicalRecord.getClinicId())
                .correctedRecordId(medicalRecord.getCorrectedRecordId())
                .type(medicalRecord.getType())
                .status(medicalRecord.getStatus())
                .veterinarianId(medicalRecord.getVeterinarianId())
                .notes(medicalRecord.getNotes())
                .details(medicalRecord.getDetails())
                .build();
    }

    private MedicalRecord toDomain(MedicalRecordDocument doc) {
        return MedicalRecord.reconstitute(
                doc.getId(),
                doc.getPetId(),
                doc.getClinicId(),
                doc.getCorrectedRecordId(),
                doc.getType(),
                doc.getStatus(),
                doc.getVeterinarianId(),
                doc.getNotes(),
                doc.getDetails(),
                doc.getCreatedAt(),
                doc.getUpdatedAt());
    }

    @Override
    public MedicalRecord save(MedicalRecord entity) {
        return toDomain(repository.save(toDocument(entity)));
    }

    @Override
    public Optional<MedicalRecord> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<MedicalRecord> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    // specific querys

    @Override
    public List<MedicalRecord> findByPetId(String petId) {
        return repository.findByPetId(petId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<MedicalRecord> findByPetIdAndType(String petId, MedicalRecordType type) {
        return repository.findByPetIdAndType(petId, type).stream().map(this::toDomain).toList();
    }

    @Override
    public List<MedicalRecord> findByPetIdAndStatus(String petId, MedicalRecordLifecycleStatus status) {
        return repository.findByPetIdAndStatus(petId, status).stream().map(this::toDomain).toList();
    }

    @Override
    public List<MedicalRecord> findByClinicId(String clinicId) {
        return repository.findByClinicId(clinicId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<MedicalRecord> findByClinicIdAndType(String clinicId, MedicalRecordType type) {
        return repository.findByClinicIdAndType(clinicId, type).stream().map(this::toDomain).toList();
    }

    @Override
    public List<MedicalRecord> findByCorrectedRecordId(String originalRecordId) {
        return repository.findByCorrectedRecordId(originalRecordId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByPetIdAndStatus(String petId, MedicalRecordLifecycleStatus status) {
        return repository.existsByPetIdAndStatus(petId, status);
    }


}
