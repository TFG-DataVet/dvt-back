package com.datavet.datavet.pet.domain.model.details.weight;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
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
        if ( value == null || value <= 0){
            throw new IllegalArgumentException("Weight value must be great than zero");
        }

        if (unit == null){
            throw new IllegalArgumentException("Weight unit is required");
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        return true;
    }

    @Override
    public StatusChangeResult applyAction(RecordAction action) {
        throw new IllegalArgumentException("No se puede aplicar una acción de cambio de estado en un regristro que no tiene estados.");
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
