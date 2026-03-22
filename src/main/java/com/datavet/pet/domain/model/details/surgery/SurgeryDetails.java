package com.datavet.pet.domain.model.details.surgery;

import com.datavet.pet.domain.exception.MedicalRecordApplyActionException;
import com.datavet.pet.domain.exception.MedicalRecordStateException;
import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@TypeAlias("surgery")
@Document
public class SurgeryDetails implements MedicalRecordDetails {

    private final String surgeryName;
    private final SurgeryType surgeryType;
    private final List<SurgeryProcedure> procedures;
    private final AnesthesiaType anesthesiaType;
    private final boolean hospitalizationRequired;
    private LocalDateTime surgeryDate; /* Mutable pero controlado por estado, solo mutable en SCHEDULED*/
    private SurgeryStatus status;
    private SurgeryOutcome outcome; /* Mutable pero solo por estados */
    private List<SurgeryMedication> postOpMedications; /* Mutable pero solo para agregar, no se puede borrar*/
    private LocalDateTime completedAt;

    @Override
    public MedicalRecordType getType(){
        return MedicalRecordType.SURGERY;
    }

    @Override
    public void validate(){
        ValidationResult result = new ValidationResult();

        if(this.status == null) {
            result.addError("status", "El estado de la cirugía no debe estar vacio.");
        }

        if (surgeryName == null || surgeryName.isBlank()) {
            result.addError("surgeryName", "El nombre de la cirugía no puede ser null ni estar vacio.");
        }

        if (surgeryType == null) {
            result.addError("surgeryType", "El tipo de la cirugía no puede ser nulo.");
        }

        if (procedures == null || procedures.isEmpty()) {
            result.addError("procedures", "La cirugía debe tener al menos un procedimiento.");
        }

        if (this.status != null) {
            if (this.status == SurgeryStatus.SCHEDULED) {
                if (surgeryDate == null) {
                    result.addError("surgeryDate", "La cirugía en estado de " + status + " debe de tener una fecha de admición.");
                } else if (surgeryDate.isBefore(LocalDateTime.now())){
                    result.addError("surgeryDate", "La fecha de cirugía en estado de " + status + " no puede antes del día de hoy (" + LocalDateTime.now() + ")");
                }

                if (!postOpMedications.isEmpty()){
                    result.addError("postOpMedications", "La cirugía en estado de " + status + " no debe de contener todavía ningún medicamente post-operatorio.");
                }
            }

            if (this.status == SurgeryStatus.IN_PROGRESS) {
                if (anesthesiaType == null) {
                    result.addError("anesthesiaType", "El tipo de anestecia no puede estar vaci en el estado " + status);
                }
            }

            if (this.status == SurgeryStatus.ADMITTED ||
                this.status == SurgeryStatus.IN_PROGRESS) {
                if (surgeryDate == null) {
                    result.addError("surgeryDate", "La cirugía en estado de " + status + " debe de tener una fecha de admición.");
                }
            }

            if (this.status != SurgeryStatus.COMPLETED &&
                this.status != SurgeryStatus.DECEASED) {
                if (outcome != null) {
                    result.addError("outcome", "La cirugía en estado de " + status + " no puede contener resultado, hasta ser completada.");
                }
            }

            if (this.status == SurgeryStatus.COMPLETED) {
                if (postOpMedications.isEmpty()){
                    result.addError("postOpMedications", "La cirugía en estado de " + status + " debe de tener medicamentos post-operatorios.");
                }
            }

            if (this.status == SurgeryStatus.COMPLETED ||
                this.status == SurgeryStatus.DECEASED) {
                if (completedAt == null) {
                    result.addError("completedAt", "La cirugía en estado de " + status + " debe de tener una fecha de culminación.");
                }

                if (completedAt != null && surgeryDate != null && completedAt.isBefore(surgeryDate)){
                    result.addError("completedAt", "La fecha de culminación de la cirugía no puede ser anterior a la fecha de admición.");
                }

                if (outcome == null){
                    result.addError("outcome", "La cirugía en estado de " + status + " debe de tener un resultado de cirugía.");
                }
            }

            if (this.status == SurgeryStatus.CANCELLED) {
                if(completedAt == null) {
                    result.addError("completedAt", "La cirugía en estado de " + status + " debe de tener una fecha de culminación.");
                }

            }
        }

        if (result.hasErrors()) {
            throw new MedicalRecordValidationException(result);
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof SurgeryDetails prev)) return false;

        if (prev.status == SurgeryStatus.COMPLETED ||
                prev.status == SurgeryStatus.CANCELLED ||
                prev.status == SurgeryStatus.DECEASED) {
            throw new MedicalRecordValidationException("Surgery - instanceOf", "No es posible corregir un registro con un tipo de detalle diferente.");

        }

        if (!Objects.equals(this.surgeryName, prev.surgeryName)) return false;
        if (this.surgeryType != prev.surgeryType) return false;
        if (!Objects.equals(this.procedures, prev.procedures)) return false;
        if (this.anesthesiaType != prev.anesthesiaType) return false;
        if (this.hospitalizationRequired != prev.hospitalizationRequired) return false;
        if (this.status != prev.status) return false;
        if (!Objects.equals(this.completedAt, prev.completedAt)) return false;
        if (this.outcome != prev.outcome) return false;

        if (prev.status != SurgeryStatus.SCHEDULED) {
            if (!Objects.equals(this.surgeryDate, prev.surgeryDate)) return false;
        }

        if (!this.postOpMedications.containsAll(prev.postOpMedications)) return false;

        return true;
    }

    public static SurgeryDetails create(
            String surgeryName,
            SurgeryType surgeryType,
            List<SurgeryProcedure> procedures,
            AnesthesiaType anesthesiaType,
            boolean hospitalizationRequired,
            LocalDateTime surgeryDate){
        try {
            SurgeryDetails surgeryDetails = new SurgeryDetails(
                    surgeryName,
                    surgeryType,
                    procedures != null ? List.copyOf(procedures) : null,
                    anesthesiaType,
                    hospitalizationRequired,
                    surgeryDate,
                    SurgeryStatus.SCHEDULED,
                    null,
                    new ArrayList<>(emptyList()),
                    null);

            surgeryDetails.validate();

            return surgeryDetails;
        } catch (RuntimeException e) {
            throw e;
        }
    }


    @Override
    public StatusChangeResult applyAction(RecordAction action) {
        SurgeryStatus previousStatus = this.status;
        LocalDateTime previousCompletedAt = completedAt;

        try{
            SurgeryStatus next = this.status.next(action);

            if (next == SurgeryStatus.COMPLETED || next == SurgeryStatus.DECEASED) {
                if (this.outcome == null) {
                    throw new MedicalRecordApplyActionException("Surgery - instanceOf", "No es posible corregir un registro con un tipo de detalle diferente.");

                }

                if (this.postOpMedications.isEmpty()) {
                    throw new MedicalRecordApplyActionException("Surgery - instanceOf", "No es posible corregir un registro con un tipo de detalle diferente.");
                }

            }
            this.completedAt = LocalDateTime.now();

            this.status = next;
            this.validate();

            return StatusChangeResult.of(previousStatus, next);
        } catch (RuntimeException e) {
            this.status = previousStatus;
            this.completedAt = previousCompletedAt;
            throw e;
        }
    }

    public void changedOutcome(SurgeryOutcome newOutcome){
        if (this.status != SurgeryStatus.IN_PROGRESS){
            throw new MedicalRecordStateException("Surgery - status", "El resultado solo puede definirse cuando la cirugía esta en progreso.");
        }

        if (newOutcome == null) {
            throw new IllegalArgumentException("El resultado no puede ser nulo.");
        }

        this.outcome = newOutcome;

        validate();
    }

    public void addPostOpMedication(SurgeryMedication medication){
        if (this.status != SurgeryStatus.IN_PROGRESS){
            throw new IllegalArgumentException("Solo se puede agregar medicación postoperatoria antes de dar como completado el registro de la cirugía.");
        }

        if (medication == null){
            throw new IllegalArgumentException("La medicación no puede ser nula.");
        }

        this.postOpMedications.add(medication);

        validate();
    }

    public void rescheduled(LocalDateTime newDate){
        if (this.status != SurgeryStatus.SCHEDULED){
            throw new IllegalArgumentException("Solo se puede reprogramar una cirugía en estado Porgramada.");
        }

        if (newDate == null) {
            throw new IllegalArgumentException("La nueva fecha ser nula.");
        }

        if (newDate.isBefore(LocalDateTime.now())){
          throw new IllegalArgumentException("La nueva fecha no puede ser anterior al día actial.");
        }

        this.surgeryDate = newDate;

        validate();
    }

}
