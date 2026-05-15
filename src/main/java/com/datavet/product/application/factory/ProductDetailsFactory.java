package com.datavet.product.application.factory;

import com.datavet.product.application.port.in.command.request.*;
import com.datavet.product.domain.details.*;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailsFactory {

    public ProductDetails create(ProductDetailsRequest request) {
        return switch (request) {
            case MedicationDetailsRequest r -> MedicationDetails.create(
                    r.getActiveIngredient(), r.getDosageForm(), r.getConcentration(),
                    r.getManufacturer(), r.getRegistrationNumber(), r.getPrescriptionRequired(),
                    r.getStorageConditions(), r.getBatchNumber(), r.getExpirationDate(),
                    r.getSpecies(), r.getAdministrationRoute());

            case VaccineDetailsRequest r -> VaccineDetails.create(
                    r.getActiveIngredient(), r.getDosageForm(), r.getConcentration(),
                    r.getManufacturer(), r.getRegistrationNumber(), r.getPrescriptionRequired(),
                    r.getStorageConditions(), r.getBatchNumber(), r.getExpirationDate(),
                    r.getSpecies(), r.getAdministrationRoute(),
                    r.getVaccineType(), r.getBoosterIntervalDays(), r.getDiseaseProtection());

            case FoodDetailsRequest r -> FoodDetails.create(
                    r.getBrand(), r.getWeightGrams(), r.getLifeStageSuitability(),
                    r.getSpeciesSuitability(), r.getFlavors(), r.getIngredients(),
                    r.getNutritionalInfo(), r.getIsVeterinaryDiet(), r.getStorageConditions());

            case SupplementDetailsRequest r -> SupplementDetails.create(
                    r.getBrand(), r.getWeightGrams(), r.getLifeStageSuitability(),
                    r.getSpeciesSuitability(), r.getFlavors(), r.getIngredients(),
                    r.getNutritionalInfo(), r.getSupplementType());

            case HygieneDetailsRequest r -> HygieneDetails.create(
                    r.getBrand(), r.getScentType(), r.getSpeciesSuitability(), r.getVolumeMl());

            case GroomingServiceDetailsRequest r -> GroomingServiceDetails.create(
                    r.getBrand(), r.getScentType(), r.getSpeciesSuitability(), r.getVolumeMl(),
                    r.getDurationMinutes(), r.getBreedSizeTarget(),
                    r.getIncludesScent(), r.getRequiresAppointment());

            case AccessoryDetailsRequest r -> AccessoryDetails.create(
                    r.getBrand(), r.getSizes(), r.getColors(),
                    r.getMaterial(), r.getSpeciesSuitability());

            case ToyDetailsRequest r -> ToyDetails.create(
                    r.getBrand(), r.getSizes(), r.getColors(), r.getMaterial(),
                    r.getSpeciesSuitability(), r.getToyType(),
                    r.getDifficultyLevel(), r.getSafetyWarnings());

            case MedicalSupplyDetailsRequest r -> MedicalSupplyDetails.create(
                    r.getManufacturer(), r.getReferenceCode(), r.getUnitOfMeasure(),
                    r.getQuantityPerUnit(), r.getIsSterile(),
                    r.getBatchNumber(), r.getExpirationDate());

            case AntiparasiticDetailsRequest r -> AntiparasiticDetails.create(
                    r.getActiveIngredient(), r.getManufacturer(), r.getBatchNumber(),
                    r.getExpirationDate(), r.getSpecies(), r.getStorageConditions(),
                    r.getAntiparasiticType(), r.getApplicationForm(),
                    r.getWeightRangeMinKg(), r.getWeightRangeMaxKg(),
                    r.getDurationDays(), r.getActiveSubstances(), r.getBreedSizeTarget());

            case DiagnosticDetailsRequest r -> DiagnosticDetails.create(
                    r.getManufacturer(), r.getReferenceCode(), r.getUnitOfMeasure(),
                    r.getQuantityPerUnit(), r.getIsSterile(), r.getBatchNumber(),
                    r.getExpirationDate(), r.getTargetAnalyte(), r.getCompatibleSpecies(),
                    r.getSensitivityPercent(), r.getSpecificityPercent());

            case ProsthesisImplantDetailsRequest r -> ProsthesisImplantDetails.create(
                    r.getImplantType(), r.getManufacturer(), r.getReferenceCode(),
                    r.getCompatibleSpecies(), r.getRequiresSurgery(), r.getIsoStandard(),
                    r.getBatchNumber(), r.getExpirationDate());
        };
    }
}
