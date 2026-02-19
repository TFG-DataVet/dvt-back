package com.datavet.datavet.pet.domain.model.details.vaccine;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccineDetails implements MedicalRecordDetails {

    private String vaccineName;
    private LocalDate applicationDate;
    private LocalDate nextDoseDate;
    private String batchNumber;
    private String manufacturer;

    @Override
    public MedicalRecordType getType(){
        return MedicalRecordType.VACCINE;
    }

    @Override
    public void validate() {
        if (vaccineName == null || vaccineName.isBlank()) {
            throw new IllegalArgumentException("Vaccine name cannot be null");
        }

        if (applicationDate == null || applicationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("ApplicationDate cannot be null or cannot have a future date");
        }

        if (nextDoseDate != null && nextDoseDate.isBefore(applicationDate)) {
            throw new IllegalArgumentException("Next dose date cannot be before application date");
        }

        if (batchNumber == null || batchNumber.isBlank()) {
            throw new IllegalArgumentException("BatchNumber cannot be null");
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof VaccineDetails)){
            throw new IllegalArgumentException("No es posible corregir un registro con un tipo de detalle diferente.");
        }

        VaccineDetails prev = (VaccineDetails) previous;

        boolean vaccineNameChanged = !Objects.equals(this.vaccineName, prev.vaccineName);
        boolean applicationDateChanged = !Objects.equals(this.applicationDate, prev.applicationDate);
        boolean batchNumberChanged = !Objects.equals(this.batchNumber, prev.batchNumber);
        boolean manufacturer = !Objects.equals(this.manufacturer, prev.manufacturer);

        return  vaccineNameChanged ||
                applicationDateChanged ||
                batchNumberChanged ||
                manufacturer;
    }

    @Override
    public StatusChangeResult applyAction(RecordAction action) {
        throw new IllegalArgumentException("No se puede aplicar una acción de cambio de estado en un regristro que no tiene estados.");
    }

    public static VaccineDetails create(String vaccineName,
                                 LocalDate applicationDate,
                                 LocalDate nextDoseDate,
                                 String batchNumber,
                                 String manufacturer) {
        try{
            VaccineDetails vaccineDetails = new VaccineDetails(
                    vaccineName,
                    applicationDate,
                    nextDoseDate,
                    batchNumber,
                    manufacturer);

            vaccineDetails.validate();

            return vaccineDetails;
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
