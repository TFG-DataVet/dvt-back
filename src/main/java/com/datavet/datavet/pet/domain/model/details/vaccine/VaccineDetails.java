package com.datavet.datavet.pet.domain.model.details.vaccine;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
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
        ValidationResult result = new ValidationResult();

        if (vaccineName == null || vaccineName.isBlank()) result.addError("Vaccine - name", "El nombre de la vacuna no puede ser nulo");
        if (applicationDate == null || applicationDate.isAfter(LocalDate.now())) result.addError("Vaccine - applicationDate", "La fecha de aplicación no puede ser nula o estar vacia.");
        if (nextDoseDate != null && (applicationDate != null && nextDoseDate.isBefore(applicationDate))) result.addError("Vaccine - nextDoseDate", "La fecha de la siguiente dosis no puede ser nula ni anterior al día de hoy.");
        if (batchNumber == null || batchNumber.isBlank()) result.addError("Vaccine - batchNumber", "El numero del batch no puede ser nulo ni estar vacio.");

        if (result.hasErrors()) throw new MedicalRecordValidationException(result);
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        if (!(previous instanceof VaccineDetails)) throw new MedicalRecordValidationException("Vaccine instanceOf", "No es posible corregir un registro con un tipo de detalle diferente.");

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
