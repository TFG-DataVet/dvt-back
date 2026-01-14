package com.datavet.datavet.pet.domain.model.details;

import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryMedication;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryProcedure;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.pet.domain.valueobject.SurgeryOutcome;
import com.datavet.datavet.pet.domain.valueobject.SurgeryType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurgeryDetails implements MedicalRecordDetails {

    private String surgeryName;
    private SurgeryType surgeryType;
    private LocalDate surgeryDate;
    private SurgeryOutcome outcome;
    private List<SurgeryProcedure> procedures;
    private String anesthesiaType;
    private List<SurgeryMedication> postOpMedications;
    private boolean hospitalizationRequired;
    private Integer hospitalizationDays;
    private boolean followUpRequired;
    private LocalDate followUpDate;

    @Override
    public MedicalRecordType getType(){
        return MedicalRecordType.SURGERY;
    }

    @Override
    public void validate(){
        if (surgeryName == null || surgeryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la operación no puede ser null ni estar vacio.");
        }

        if (surgeryType == null) {
            throw new IllegalArgumentException("El tipo de la operación no puede ser nulo.");
        }

        if (surgeryDate == null || surgeryDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de la operación no puede ser nula ni tener una fecha despues de hoy.");
        }

        if (outcome == null) {
            throw new IllegalArgumentException("El resultado de la cirugía no puede ser nulo.");
        }

        if (procedures == null || procedures.isEmpty()) {
            throw new IllegalArgumentException("La cirugía debe tener al menos un procedimiento.");
        }

        for (SurgeryProcedure procedure : procedures) {
            if (procedure == null) {
                throw new IllegalArgumentException("Los procedimientos no pueden contener valores nulos.");
            }
            procedure.validate();
        }

        if (hospitalizationRequired) {
            if (hospitalizationDays == null || hospitalizationDays <= 0) {
                throw new IllegalArgumentException("Los días de hospitalización deben ser mayores a cero.");
            }
        } else {
            if (hospitalizationDays != null) {
                throw new IllegalArgumentException("No debe haber días de hospitalización si no se requiere hospitalización.");
            }
        }

        if (followUpRequired) {
            if (followUpDate == null) {
                throw new IllegalArgumentException("Si se requiere seguimiento de la operación, la fecha de seguimiento no puede ser nula");
            }

            if (followUpDate.isBefore(surgeryDate)) {
                throw new IllegalArgumentException("La fecha del seguimiento no puede ser antes de la operación.");
            }

            if (postOpMedications != null) {
                for (SurgeryMedication medication : postOpMedications) {
                    if (medication == null) {
                        throw new IllegalArgumentException("La lista de medicación postoperatoria no puede contener nulos.");
                    }
                    medication.validate();
                }
            }

        }
    }

    public static SurgeryDetails create(
            String surgeryName,
            SurgeryType surgeryType,
            LocalDate surgeryDate,
            SurgeryOutcome outcome,
            List<SurgeryProcedure> procedures,
            String anesthesiaType,
            List<SurgeryMedication> postOpMedications,
            boolean hospitalizationRequired,
            Integer hospitalizationDays,
            boolean followUpRequired,
            LocalDate followUpDate){
        SurgeryDetails surgeryDetails = SurgeryDetails.builder()
                .surgeryName(surgeryName)
                .surgeryType(surgeryType)
                .surgeryDate(surgeryDate)
                .outcome(outcome)
                .procedures(procedures)
                .anesthesiaType(anesthesiaType)
                .postOpMedications(postOpMedications)
                .hospitalizationRequired(hospitalizationRequired)
                .hospitalizationDays(hospitalizationDays)
                .followUpRequired(followUpRequired)
                .followUpDate(followUpDate)
                .build();

        surgeryDetails.validate();

        return surgeryDetails;
    }
}
