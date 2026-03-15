package com.datavet.datavet.pet.application.port.out;

import com.datavet.datavet.pet.domain.model.MedicalRecord;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordLifecycleStatus;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.application.port.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface MedicalRecordPort extends Repository<MedicalRecord, String> {

    List<MedicalRecord> findByPetId(String petId);

    List<MedicalRecord> findByPetIdAndType(String petId, MedicalRecordType type);

    List<MedicalRecord> findByPetIdAndStatus(String petId, MedicalRecordLifecycleStatus status);

    List<MedicalRecord> findByClinicId(String clinicId);

    List<MedicalRecord> findByClinicIdAndType(String clinicId, MedicalRecordType type);

    List<MedicalRecord> findByCorrectedRecordId(String originalRecordId);

    boolean existsByPetIdAndStatus(String petId, MedicalRecordLifecycleStatus status);
}
