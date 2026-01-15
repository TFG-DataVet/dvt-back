package com.datavet.datavet.pet.domain.model.details.weight;

import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
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

    public static WeightDetails create(Double value, WeightUnit unit){
        WeightDetails weightDetails = WeightDetails.builder()
                .value(value)
                .unit(unit)
                .build();

        weightDetails.validate();

        return weightDetails;
    }
}
