package com.datavet.datavet.pet.domain.model.details;

import com.datavet.datavet.pet.domain.valueobject.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeightDetails implements MedicalRecordDetails{

    private Double value;
    private WeightUnit unit;


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

    public void update(Double value, WeightUnit unit){
        this.value = value;
        this.unit = unit;
    }

}
