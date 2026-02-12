package com.datavet.datavet.pet.domain.model.details.treatment;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

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
        if (!(previous instanceof TreatmentDetails prev)) {
            return false;
        }

        // 🔒 Campos que NO deben cambiar
        if (!java.util.Objects.equals(this.treatmentName, prev.treatmentName)) {
            return false;
        }

        if (!java.util.Objects.equals(this.startDate, prev.startDate)) {
            return false;
        }

        if (this.status != prev.status) {
            return false;
        }

        if (!java.util.Objects.equals(this.completedAt, prev.completedAt)) {
            return false;
        }

        // ✅ Campos que SÍ pueden cambiar
        boolean instructionsChanged =
                !java.util.Objects.equals(this.instructions, prev.instructions);

        boolean medicationsChanged =
                !java.util.Objects.equals(this.medications, prev.medications);

        boolean followUpRequiredChanged =
                this.followUpRequired != prev.followUpRequired;

        boolean followUpDateChanged =
                !java.util.Objects.equals(this.followUpDate, prev.followUpDate);

        boolean estimatedEndDateChanged =
                !java.util.Objects.equals(this.estimatedEndDate, prev.estimatedEndDate);

        return instructionsChanged
                || medicationsChanged
                || followUpRequiredChanged
                || followUpDateChanged
                || estimatedEndDateChanged;
    }

    @Override
    public void validate() {
        if (treatmentName == null || treatmentName.isBlank()) {
            throw new IllegalArgumentException("El nombre del tratamiento no puede ser nulo.");
        }

        if (startDate == null || startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de comienzo del tratamiento no puede ser mayor a hoy.");
        }

        if (estimatedEndDate != null && estimatedEndDate.isBefore(startDate)) {
            throw new IllegalArgumentException("LA fecha de culminación del tratamiento no puede ser anteerior a la fecha de comienzo");
        }

        if (instructions == null || instructions.isBlank()) {
            throw new IllegalArgumentException("La instrucciones del tratamiento no pueden estar vacias.");
        }

        if (status == null) {
            throw new IllegalArgumentException("El estado del tratamiento no puede estar vacio");
        }

        if (!followUpRequired && followUpDate != null) {
            throw new IllegalArgumentException("No debe existir fecha de seguimiento si no se requiere seguimiento.");
        }

        if (followUpRequired && followUpDate == null) {
            throw new IllegalArgumentException("Se requiere una fecha de seguimiento si el paciente necesaria seguimiento.");
        }

        if (followUpRequired && estimatedEndDate != null && followUpDate.isBefore(estimatedEndDate)) {
            throw new IllegalArgumentException(
                    "La fecha de seguimiento no puede ser anterior al fin del tratamiento."
            );
        }

        if (medications != null) {
            for (TreatmentMedication medication : medications) {
                medication.validate();
            }
        }
    }

    public static TreatmentDetails create(
            String treatmentName,
            LocalDate startDate,
            String instructions,
            LocalDate endDate,
            List<TreatmentMedication> medications,
            boolean followUpRequired,
            LocalDate followUpDate
    ) {
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
    }

    @Override
    public StatusChangeResult applyAction(RecordAction action) {
        var previous = this.status;
        var next = this.status.next(action);

        this.status = next;

        if (next == TreatmentStatus.FINISHED){
            this.completedAt = LocalDate.now();
        }

        return StatusChangeResult.of(previous, next);
    }
}
