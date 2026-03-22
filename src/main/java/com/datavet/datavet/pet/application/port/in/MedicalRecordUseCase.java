package com.datavet.datavet.pet.application.port.in;

import com.datavet.datavet.pet.application.port.in.command.medicalrecord.ApplyMedicalRecordActionCommand;
import com.datavet.datavet.pet.application.port.in.command.medicalrecord.CorrectMedicalRecordCommand;
import com.datavet.datavet.pet.application.port.in.command.medicalrecord.CreateMedicalRecordCommand;
import com.datavet.datavet.pet.domain.model.MedicalRecord;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.application.port.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MedicalRecordUseCase extends UseCase {

    MedicalRecord createMedicalRecord(CreateMedicalRecordCommand command);

    MedicalRecord correctMedicalRecord(CorrectMedicalRecordCommand command);

    @Transactional
    MedicalRecord applyAction(ApplyMedicalRecordActionCommand command);

    MedicalRecord getMedicalRecordById(String medicalRecordId);

    List<MedicalRecord> getMedicalRecordsByPet(String petId);

    List<MedicalRecord> getMedicalRecordsByType(String petId, MedicalRecordType type);
}
