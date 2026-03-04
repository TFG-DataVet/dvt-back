package com.datavet.datavet.pet.domain.model.details.weight;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeightDetails implements MedicalRecordDetails {

    private Double value;
    private WeightUnit unit;

    @Override
    public MedicalRecordType getType(){
        return MedicalRecordType.WEIGHT;
    }

    @Override
    public void validate() {
        ValidationResult result = new ValidationResult();
        if ( value == null || value <= 0) result.addError("Weight - name", "El valor del peso no puede ser nulo, ni menor a 0");
        if (unit == null) result.addError("Weight - unit", "Weight unit is required");

        if (result.hasErrors()) throw new MedicalRecordValidationException(result);
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        return true;
    }

    public static WeightDetails create(Double value, WeightUnit unit){
        try {
            WeightDetails weightDetails = new WeightDetails(value,unit);
            weightDetails.validate();

            return weightDetails;
        } catch (RuntimeException e) {
            throw e;
        }

    }
}
