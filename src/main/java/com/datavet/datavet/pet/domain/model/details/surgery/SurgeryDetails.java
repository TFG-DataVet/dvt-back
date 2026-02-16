package com.datavet.datavet.pet.domain.model.details.surgery;
import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
        if(this.status == null) {
            throw new IllegalArgumentException("El estado de la cirugía no debe estar vacio.");
        }

        if (surgeryName == null || surgeryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la cirugía no puede ser null ni estar vacio.");
        }

        if (surgeryType == null) {
            throw new IllegalArgumentException("El tipo de la cirugía no puede ser nulo.");
        }

        if (procedures == null || procedures.isEmpty()) {
            throw new IllegalArgumentException("La cirugía debe tener al menos un procedimiento.");
        }

        if (this.status == SurgeryStatus.SCHEDULED) {
            if (surgeryDate == null) {
                throw new IllegalArgumentException("La cirugía en estado de " + status + " debe de tener una fecha de admición.");
            }

            if (surgeryDate.isBefore(LocalDateTime.now())){
                throw new IllegalArgumentException("La fecha de cirugía en estado de " + status + " no puede antes del día de hoy (" + LocalDateTime.now() + ")");
            }

            if (!postOpMedications.isEmpty()){
                throw new IllegalArgumentException("La cirugía en estado de " + status + " no debe de contener todavía ningún medicamente post-operatorio..");
            }
        }

        if (this.status == SurgeryStatus.IN_PROGRESS) {
            if (anesthesiaType == null) {
                throw new IllegalArgumentException("El tipo de anestecia no puede estar vaci en el estado " + status);
            }
        }

        if (this.status == SurgeryStatus.ADMITTED ||
            this.status == SurgeryStatus.IN_PROGRESS) {
            if (surgeryDate == null) {
                throw new IllegalArgumentException("La cirugía en estado de " + status + " debe de tener una fecha de admición.");
            }
        }

        if (this.status != SurgeryStatus.COMPLETED &&
            this.status != SurgeryStatus.DECEASED) {
            if (outcome != null) {
                throw new IllegalArgumentException("La cirugía en estado de " + status + " no puede contener resultado, hasta ser completada.");
            }
        }

        if (this.status == SurgeryStatus.COMPLETED) {
            if (postOpMedications.isEmpty()){
                throw new IllegalArgumentException("La cirugía en estado de " + status + " debe de tener medicamentos post-operatorios.");
            }
        }

        if (this.status == SurgeryStatus.COMPLETED ||
            this.status == SurgeryStatus.DECEASED) {
            if (completedAt == null) {
                throw new IllegalArgumentException("La cirugía en estado de " + status + " debe de tener una fecha de culminación.");
            }

            if (completedAt.isBefore(surgeryDate)){
                throw new IllegalArgumentException("La fecha de culminación de la cirugía no puede ser anterior a la fecha de admición.");
            }

            if (outcome == null){
                throw new IllegalArgumentException("La cirugía en estado de " + status + " debe de tener un resultado de cirugía.");
            }
        }

        if (this.status == SurgeryStatus.CANCELLED) {
            if(completedAt == null) {
                throw new IllegalArgumentException("La cirugía en estado de " + status + " debe de tener una fecha de culminación.");
            }

            if(completedAt.isBefore(surgeryDate)){
                throw new IllegalArgumentException("La fecha de culminación de la cirugía no puede ser anterior a la fecha de admición.");
            }
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {

        if (!(previous instanceof SurgeryDetails prev)) return false;

        // 1️⃣ Estados terminales del registro anterior → inmutable
        if (prev.status == SurgeryStatus.COMPLETED ||
                prev.status == SurgeryStatus.CANCELLED ||
                prev.status == SurgeryStatus.DECEASED) {
            throw new IllegalArgumentException(
                    "No se puede corregir una cirugía en estado terminal.");
        }

        // 2️⃣ Campos que jamás pueden cambiar
        if (!Objects.equals(this.surgeryName, prev.surgeryName)) return false;
        if (this.surgeryType != prev.surgeryType) return false;
        if (!Objects.equals(this.procedures, prev.procedures)) return false;
        if (this.anesthesiaType != prev.anesthesiaType) return false;
        if (this.hospitalizationRequired != prev.hospitalizationRequired) return false;
        if (this.status != prev.status) return false;
        if (!Objects.equals(this.completedAt, prev.completedAt)) return false;
        if (this.outcome != prev.outcome) return false;

        // 3️⃣ surgeryDate solo puede cambiar si el estado anterior es SCHEDULED
        if (prev.status != SurgeryStatus.SCHEDULED) {
            if (!Objects.equals(this.surgeryDate, prev.surgeryDate)) return false;
        }

        // 4️⃣ Medicamentos solo pueden agregarse
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
                    List.copyOf(procedures),
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
                    throw new IllegalArgumentException(
                            "No se puede completar la cirugía sin resultado."
                    );
                }

                if (this.postOpMedications.isEmpty()) {
                    throw new IllegalArgumentException(
                            "No se puede completar la cirugía sin medicación post-operatoria."
                    );
                }

                this.completedAt = LocalDateTime.now();
            }

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
            throw new IllegalArgumentException("El resultado solo puede definirse cuando la cirugía esta en progreso.");
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
