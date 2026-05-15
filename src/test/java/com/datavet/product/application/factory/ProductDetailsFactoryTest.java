package com.datavet.product.application.factory;

import com.datavet.product.application.port.in.command.request.*;
import com.datavet.product.domain.details.*;
import com.datavet.product.domain.valueobject.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductDetailsFactory Tests")
class ProductDetailsFactoryTest {

    private final ProductDetailsFactory factory = new ProductDetailsFactory();

    @Test
    @DisplayName("Should create MedicationDetails from MedicationDetailsRequest")
    void create_MedicationRequest_ShouldReturnMedicationDetails() {
        MedicationDetailsRequest request = new MedicationDetailsRequest();
        request.setActiveIngredient("Amoxicilina");
        request.setDosageForm("Comprimidos");
        request.setConcentration("500mg");
        request.setManufacturer("Pfizer");
        request.setPrescriptionRequired(true);
        request.setSpecies(List.of("Perro"));

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(MedicationDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.MEDICATION);
        MedicationDetails med = (MedicationDetails) result;
        assertThat(med.getActiveIngredient()).isEqualTo("Amoxicilina");
        assertThat(med.getPrescriptionRequired()).isTrue();
    }

    @Test
    @DisplayName("Should create VaccineDetails from VaccineDetailsRequest")
    void create_VaccineRequest_ShouldReturnVaccineDetails() {
        VaccineDetailsRequest request = new VaccineDetailsRequest();
        request.setActiveIngredient("Parvovirus antigen");
        request.setVaccineType("Modificada en vivo");
        request.setBoosterIntervalDays(365);
        request.setSpecies(List.of("Perro"));

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(VaccineDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.VACCINE);
    }

    @Test
    @DisplayName("Should create FoodDetails from FoodDetailsRequest")
    void create_FoodRequest_ShouldReturnFoodDetails() {
        FoodDetailsRequest request = new FoodDetailsRequest();
        request.setBrand("Royal Canin");
        request.setWeightGrams(5000);
        request.setSpeciesSuitability(List.of("Perro"));

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(FoodDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.FOOD);
    }

    @Test
    @DisplayName("Should create SupplementDetails from SupplementDetailsRequest")
    void create_SupplementRequest_ShouldReturnSupplementDetails() {
        SupplementDetailsRequest request = new SupplementDetailsRequest();
        request.setBrand("VetPlus");
        request.setSupplementType("Articular");
        request.setSpeciesSuitability(List.of("Perro", "Gato"));

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(SupplementDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.SUPPLEMENT);
    }

    @Test
    @DisplayName("Should create HygieneDetails from HygieneDetailsRequest")
    void create_HygieneRequest_ShouldReturnHygieneDetails() {
        HygieneDetailsRequest request = new HygieneDetailsRequest();
        request.setBrand("Virbac");
        request.setScentType("Neutro");
        request.setSpeciesSuitability(List.of("Perro"));
        request.setVolumeMl(250);

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(HygieneDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.HYGIENE);
    }

    @Test
    @DisplayName("Should create GroomingServiceDetails from GroomingServiceDetailsRequest")
    void create_GroomingServiceRequest_ShouldReturnGroomingServiceDetails() {
        GroomingServiceDetailsRequest request = new GroomingServiceDetailsRequest();
        request.setBrand("GroomPro");
        request.setScentType("Lavanda");
        request.setSpeciesSuitability(List.of("Perro"));
        request.setVolumeMl(500);
        request.setDurationMinutes(60);
        request.setBreedSizeTarget("Mediano");
        request.setIncludesScent(true);
        request.setRequiresAppointment(true);

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(GroomingServiceDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.GROOMING_SERVICE);
    }

    @Test
    @DisplayName("Should create AccessoryDetails from AccessoryDetailsRequest")
    void create_AccessoryRequest_ShouldReturnAccessoryDetails() {
        AccessoryDetailsRequest request = new AccessoryDetailsRequest();
        request.setBrand("PetCo");
        request.setSizes(List.of("S", "M", "L"));
        request.setColors(List.of("Rojo", "Azul"));
        request.setMaterial("Nylon");
        request.setSpeciesSuitability(List.of("Perro"));

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(AccessoryDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.ACCESSORY);
    }

    @Test
    @DisplayName("Should create ToyDetails from ToyDetailsRequest")
    void create_ToyRequest_ShouldReturnToyDetails() {
        ToyDetailsRequest request = new ToyDetailsRequest();
        request.setBrand("Kong");
        request.setSizes(List.of("M"));
        request.setColors(List.of("Rojo"));
        request.setMaterial("Caucho");
        request.setSpeciesSuitability(List.of("Perro"));
        request.setToyType("Interactivo");
        request.setDifficultyLevel("Fácil");

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(ToyDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.TOY);
    }

    @Test
    @DisplayName("Should create MedicalSupplyDetails from MedicalSupplyDetailsRequest")
    void create_MedicalSupplyRequest_ShouldReturnMedicalSupplyDetails() {
        MedicalSupplyDetailsRequest request = new MedicalSupplyDetailsRequest();
        request.setManufacturer("BD");
        request.setReferenceCode("BD-2020");
        request.setUnitOfMeasure("Unidad");
        request.setQuantityPerUnit(100);
        request.setIsSterile(true);
        request.setBatchNumber("BATCH-001");
        request.setExpirationDate(LocalDate.now().plusYears(2));

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(MedicalSupplyDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.MEDICAL_SUPPLY);
    }

    @Test
    @DisplayName("Should create AntiparasiticDetails from AntiparasiticDetailsRequest")
    void create_AntiparasiticRequest_ShouldReturnAntiparasiticDetails() {
        AntiparasiticDetailsRequest request = new AntiparasiticDetailsRequest();
        request.setActiveIngredient("Fipronil");
        request.setManufacturer("Merial");
        request.setSpecies(List.of("Perro", "Gato"));
        request.setAntiparasiticType("Antipulgas");
        request.setApplicationForm("Pipeta");
        request.setWeightRangeMinKg(new BigDecimal("2.0"));
        request.setWeightRangeMaxKg(new BigDecimal("10.0"));
        request.setDurationDays(30);

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(AntiparasiticDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.ANTIPARASITIC);
    }

    @Test
    @DisplayName("Should create DiagnosticDetails from DiagnosticDetailsRequest")
    void create_DiagnosticRequest_ShouldReturnDiagnosticDetails() {
        DiagnosticDetailsRequest request = new DiagnosticDetailsRequest();
        request.setManufacturer("Idexx");
        request.setReferenceCode("IDEXX-001");
        request.setUnitOfMeasure("Test");
        request.setQuantityPerUnit(10);
        request.setIsSterile(true);
        request.setTargetAnalyte("Parvovirus");
        request.setCompatibleSpecies(List.of("Perro"));
        request.setSensitivityPercent(new BigDecimal("98.5"));
        request.setSpecificityPercent(new BigDecimal("99.0"));

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(DiagnosticDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.DIAGNOSTIC);
    }

    @Test
    @DisplayName("Should create ProsthesisImplantDetails from ProsthesisImplantDetailsRequest")
    void create_ProsthesisImplantRequest_ShouldReturnProsthesisImplantDetails() {
        ProsthesisImplantDetailsRequest request = new ProsthesisImplantDetailsRequest();
        request.setImplantType("Cadera");
        request.setManufacturer("BioMedtrix");
        request.setReferenceCode("BIO-HIP-001");
        request.setCompatibleSpecies(List.of("Perro"));
        request.setRequiresSurgery(true);
        request.setIsoStandard("ISO 5832");

        ProductDetails result = factory.create(request);

        assertThat(result).isInstanceOf(ProsthesisImplantDetails.class);
        assertThat(result.getCategory()).isEqualTo(ProductCategory.PROSTHESIS_IMPLANT);
    }
}
