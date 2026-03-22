package com.datavet.pet.testutil.medicalrecord;

import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.model.details.surgery.*;

import java.time.LocalDateTime;
import java.util.List;

public class SurgeryDetailsTestDataBuilder {

    private static final String DEFAULT_SURGERY_NAME = "Osteosíntesis de fémur";
    private static final SurgeryType DEFAULT_SURGERY_TYPE = SurgeryType.CORRECTIVE;
    private static final List<SurgeryProcedure> DEFAULT_PROCEDURES = List.of(
            SurgeryProcedure.create("Reducción de fractura", "Reposición del fragmento óseo"),
            SurgeryProcedure.create("Fijación interna", "Colocación de placa y tornillos")
    );
    private static final AnesthesiaType DEFAULT_ANESTHESIA_TYPE = AnesthesiaType.GENERAL;
    private static final boolean DEFAULT_HOSPITALIZATION_REQUIRED = true;
    private static final LocalDateTime DEFAULT_SURGERY_DATE = LocalDateTime.now().plusDays(3);

    public static final SurgeryMedication DEFAULT_POST_OP_MEDICATION =
            SurgeryMedication.create("Meloxicam", "0.2mg/kg", "Cada 24h", 7, "Anti-inflamatorio");

    public static SurgeryDetails aValidScheduledSurgery() {
        return SurgeryDetails.create(DEFAULT_SURGERY_NAME, DEFAULT_SURGERY_TYPE,
                DEFAULT_PROCEDURES, DEFAULT_ANESTHESIA_TYPE,
                DEFAULT_HOSPITALIZATION_REQUIRED, DEFAULT_SURGERY_DATE);
    }

    public static SurgeryDetails aScheduledSurgeryWithoutHospitalization() {
        return SurgeryDetails.create(DEFAULT_SURGERY_NAME, DEFAULT_SURGERY_TYPE,
                DEFAULT_PROCEDURES, DEFAULT_ANESTHESIA_TYPE, false, DEFAULT_SURGERY_DATE);
    }

    public static SurgeryDetails anAdmittedSurgery() {
        SurgeryDetails d = aValidScheduledSurgery();
        d.applyAction(RecordAction.ADMIT);
        return d;
    }

    public static SurgeryDetails anInProgressSurgery() {
        SurgeryDetails d = anAdmittedSurgery();
        d.applyAction(RecordAction.START);
        return d;
    }

    public static SurgeryDetails aCompletedSurgery() {
        SurgeryDetails d = anInProgressSurgery();
        d.changedOutcome(SurgeryOutcome.SUCCESSFUL);
        d.addPostOpMedication(DEFAULT_POST_OP_MEDICATION);
        d.applyAction(RecordAction.COMPLETE);
        return d;
    }

    public static SurgeryDetails aDeceasedSurgery() {
        SurgeryDetails d = anInProgressSurgery();
        d.changedOutcome(SurgeryOutcome.FAILED);
        d.addPostOpMedication(DEFAULT_POST_OP_MEDICATION);
        d.applyAction(RecordAction.DECLARE_DECEASED);
        return d;
    }

    public static SurgeryDetails aCancelledSurgery() {
        SurgeryDetails d = aValidScheduledSurgery();
        d.applyAction(RecordAction.CANCEL);
        return d;
    }

    public static SurgeryDetails aSurgeryWithName(String name) {
        return SurgeryDetails.create(name, DEFAULT_SURGERY_TYPE, DEFAULT_PROCEDURES,
                DEFAULT_ANESTHESIA_TYPE, DEFAULT_HOSPITALIZATION_REQUIRED, DEFAULT_SURGERY_DATE);
    }

    public static SurgeryDetails aSurgeryWithType(SurgeryType type) {
        return SurgeryDetails.create(DEFAULT_SURGERY_NAME, type, DEFAULT_PROCEDURES,
                DEFAULT_ANESTHESIA_TYPE, DEFAULT_HOSPITALIZATION_REQUIRED, DEFAULT_SURGERY_DATE);
    }

    public static SurgeryDetails aSurgeryWithProcedures(List<SurgeryProcedure> procedures) {
        return SurgeryDetails.create(DEFAULT_SURGERY_NAME, DEFAULT_SURGERY_TYPE, procedures,
                DEFAULT_ANESTHESIA_TYPE, DEFAULT_HOSPITALIZATION_REQUIRED, DEFAULT_SURGERY_DATE);
    }

    public static SurgeryDetails aSurgeryWithDate(LocalDateTime surgeryDate) {
        return SurgeryDetails.create(DEFAULT_SURGERY_NAME, DEFAULT_SURGERY_TYPE, DEFAULT_PROCEDURES,
                DEFAULT_ANESTHESIA_TYPE, DEFAULT_HOSPITALIZATION_REQUIRED, surgeryDate);
    }

    public static SurgeryMedication aValidSurgeryMedication() {
        return SurgeryMedication.create("Tramadol", "5mg/kg", "Cada 8h", 5, "Analgésico");
    }

    public static SurgeryMedication aSurgeryMedicationWithName(String name) {
        return SurgeryMedication.create(name, "5mg/kg", "Cada 8h", 5, null);
    }

    public static SurgeryProcedure aValidSurgeryProcedure() {
        return SurgeryProcedure.create("Lavado articular", "Irrigación con solución salina");
    }
}