package com.datavet.datavet.pet.infrastructure.persistence.repository;

import com.datavet.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.pet.infrastructure.persistence.document.MedicalRecordDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoMedicalRecordRepositoryAdapter extends MongoRepository<MedicalRecordDocument, String> {

    List<MedicalRecordDocument> findByPetId(String petId);

    List<MedicalRecordDocument> findByPetIdAndType(String petId, MedicalRecordType type);

    List<MedicalRecordDocument> findByPetIdAndStatus(String petId, MedicalRecordLifecycleStatus status);

    List<MedicalRecordDocument> findByClinicId(String clinicId);

    List<MedicalRecordDocument> findByClinicIdAndType(String clinicId, MedicalRecordType type);

    List<MedicalRecordDocument> findByCorrectedRecordId(String correctedRecordId);

    boolean existsByPetIdAndStatus(String petId, MedicalRecordLifecycleStatus status);
}
