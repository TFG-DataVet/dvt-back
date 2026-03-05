package com.datavet.datavet.pet.domain.model.details.treatment;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TreatmentDetails implements MedicalRecordDetails{

    private String treatmentName;
    private LocalDate startDate;
    private String instructions;
    private LocalDate estimatedEndDate;
    private List<TreatmentMedication> medications;
    private TreatmentStatus status;
    private boolean followUpRequired;
    private LocalDate followUpDate;
    private LocalDate completedAt;

    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.TREATMENT;
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof TreatmentDetails prev)) throw new MedicalRecordValidationException("Treatment - instanceOf", "No es posible corregir un registro con un tipo de detalle diferente.");

        if (prev.status == TreatmentStatus.FINISHED) {
            throw new MedicalRecordValidationException("Treatment - canCorrect, status", "No se puede corregir un tratamiento en estado terminal.");
        }

        if (!java.util.Objects.equals(this.treatmentName, prev.treatmentName)) return false;
        if (!java.util.Objects.equals(this.startDate, prev.startDate)) return false;
        if (this.status != prev.status) return false;
        if (!java.util.Objects.equals(this.completedAt, prev.completedAt)) return false;

        boolean instructionsChanged = !Objects.equals(this.instructions, prev.instructions);
        boolean medicationsChanged =!Objects.equals(this.medications, prev.medications);
        boolean followUpRequiredChanged = this.followUpRequired != prev.followUpRequired;
        boolean followUpDateChanged =!Objects.equals(this.followUpDate, prev.followUpDate);
        boolean estimatedEndDateChanged = !Objects.equals(this.estimatedEndDate, prev.estimatedEndDate);

        return instructionsChanged
                || medicationsChanged
                || followUpRequiredChanged
                || followUpDateChanged
                || estimatedEndDateChanged;
    }

    @Override
    public void validate() {
        ValidationResult result = new ValidationResult();

        if (treatmentName == null || treatmentName.isBlank()) result.addError("Treatment - name", "El nombre del tratamiento no puede ser nulo.");
        if (startDate == null || startDate.isAfter(LocalDate.now())) result.addError("Treatment - startDate", "La fecha de comienzo del tratamiento no puede ser mayor a hoy.");
        if (estimatedEndDate == null ||(startDate != null && estimatedEndDate.isBefore(startDate))) result.addError("Treatment - estimatedEndDate", "La fecha de culminación del tratamiento no puede ser anteerior a la fecha de comienzo");
        if (instructions == null || instructions.isBlank()) result.addError("Treatment - instructions", "La instrucciones del tratamiento no pueden estar vacias.");
        if (status == null) result.addError("Treatment - status", "El estado del tratamiento no puede estar vacio");
        if (!followUpRequired && followUpDate != null) result.addError("Treatment - followUpRequired & followUpDate", "No debe existir fecha de seguimiento si no se requiere seguimiento.");
        if (followUpRequired && followUpDate == null) result.addError("Treatment - followUpRequired & followUpDate", "Se requiere una fecha de seguimiento si el paciente necesaria seguimiento.");
//        if (followUpDate == null) result.addError("Treatment - followUpRequired", "La fecha de seguimiento no puede ser null.");
        if (followUpRequired && estimatedEndDate != null && (followUpDate == null || followUpDate.isBefore(estimatedEndDate))) result.addError("Treatment - followUpRequired & followUpDate.isBefore ", "La fecha de seguimiento no puede ser anterior al fin del tratamiento.");
        if (medications.isEmpty()) result.addError("Treatment - medication.isEmpty", "Los medicamentos estan vacios.");

        if (result.hasErrors()) throw new MedicalRecordValidationException(result);
    }

    public static TreatmentDetails create(
            String treatmentName,
            LocalDate startDate,
            String instructions,
            LocalDate endDate,
            List<TreatmentMedication> medications,
            boolean followUpRequired,
            LocalDate followUpDate) {
        try {
            TreatmentDetails treatmentDetails = TreatmentDetails.builder()
                    .treatmentName(treatmentName)
                    .startDate(startDate)
                    .instructions(instructions)
                    .estimatedEndDate(endDate)
                    .medications(medications)
                    .status(TreatmentStatus.PLANNED)
                    .followUpRequired(followUpRequired)
                    .followUpDate(followUpDate)
                    .build();

            treatmentDetails.validate();
            return treatmentDetails;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public StatusChangeResult applyAction(RecordAction action) {
        var previous = this.status;
        var previosCompletedAt = completedAt;

        try{
            var next = this.status.next(action);

            this.status = next;

            if (next == TreatmentStatus.FINISHED){
                this.completedAt = LocalDate.now();
            }

            validate();

            return StatusChangeResult.of(previous, next);
        } catch (RuntimeException e) {
            this.status = previous;
            this.completedAt  = previosCompletedAt;
            throw e;
        }
    }
}
