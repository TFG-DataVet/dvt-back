package com.datavet.product.domain.model;

import com.datavet.product.domain.details.MedicationDetails;
import com.datavet.product.domain.details.ProductDetails;
import com.datavet.product.domain.event.ProductCreatedEvent;
import com.datavet.product.domain.event.ProductDeactivatedEvent;
import com.datavet.product.domain.event.ProductUpdatedEvent;
import com.datavet.product.domain.exception.ProductValidationException;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.shared.domain.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Product Domain Model Tests")
class ProductTest {

    // =========================================================================
    // create — fields
    // =========================================================================

    @Test
    @DisplayName("create: should set all fields correctly")
    void create_ShouldSetAllFields() {
        Product product = buildMedicationProduct();

        assertThat(product.getClinicId()).isEqualTo("clinic-1");
        assertThat(product.getName()).isEqualTo("Amoxicilina 500mg");
        assertThat(product.getDescription()).isEqualTo("Antibiótico de amplio espectro");
        assertThat(product.getSku()).isEqualTo("SKU-001");
        assertThat(product.getBarcode()).isEqualTo("BAR-001");
        assertThat(product.getPrice()).isEqualByComparingTo("29.99");
        assertThat(product.getTaxRate()).isEqualByComparingTo("0.21");
        assertThat(product.getStock()).isEqualTo(100);
        assertThat(product.getMinStock()).isEqualTo(10);
        assertThat(product.getIsActive()).isTrue();
        assertThat(product.getCategory()).isEqualTo(ProductCategory.MEDICATION);
        assertThat(product.getDetails()).isInstanceOf(MedicationDetails.class);
    }

    @Test
    @DisplayName("create: should generate a non-null UUID")
    void create_ShouldGenerateUUID() {
        Product product = buildMedicationProduct();
        assertThat(product.getProductId()).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("create: should set createdAt and updatedAt")
    void create_ShouldSetTimestamps() {
        Product product = buildMedicationProduct();
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("create: category should be derived from details")
    void create_CategoryDerivedFromDetails() {
        ProductDetails details = buildMedicationDetails();
        Product product = Product.create(
                "clinic-1", "Test", null, null, null,
                BigDecimal.TEN, BigDecimal.ZERO, 5, 1, details);

        assertThat(product.getCategory()).isEqualTo(details.getCategory());
    }

    @Test
    @DisplayName("create: should raise ProductCreatedEvent")
    void create_ShouldRaiseCreatedEvent() {
        Product product = buildMedicationProduct();

        List<DomainEvent> events = product.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(ProductCreatedEvent.class);

        ProductCreatedEvent event = (ProductCreatedEvent) events.get(0);
        assertThat(event.getClinicId()).isEqualTo("clinic-1");
        assertThat(event.getName()).isEqualTo("Amoxicilina 500mg");
    }

    // =========================================================================
    // create — validations
    // =========================================================================

    @Test
    @DisplayName("create: should throw when clinicId is blank")
    void create_WhenClinicIdBlank_ShouldThrow() {
        assertThatThrownBy(() -> Product.create(
                "", "Producto", null, null, null,
                BigDecimal.TEN, BigDecimal.ZERO, 5, 1, buildMedicationDetails()))
                .isInstanceOf(ProductValidationException.class)
                .hasMessageContaining("clinicId");
    }

    @Test
    @DisplayName("create: should throw when name is blank")
    void create_WhenNameBlank_ShouldThrow() {
        assertThatThrownBy(() -> Product.create(
                "clinic-1", "", null, null, null,
                BigDecimal.TEN, BigDecimal.ZERO, 5, 1, buildMedicationDetails()))
                .isInstanceOf(ProductValidationException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    @DisplayName("create: should throw when details is null")
    void create_WhenDetailsNull_ShouldThrow() {
        assertThatThrownBy(() -> Product.create(
                "clinic-1", "Producto", null, null, null,
                BigDecimal.TEN, BigDecimal.ZERO, 5, 1, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("create: should throw when price is negative")
    void create_WhenPriceNegative_ShouldThrow() {
        assertThatThrownBy(() -> Product.create(
                "clinic-1", "Producto", null, null, null,
                new BigDecimal("-1"), BigDecimal.ZERO, 5, 1, buildMedicationDetails()))
                .isInstanceOf(ProductValidationException.class)
                .hasMessageContaining("precio");
    }

    @Test
    @DisplayName("create: should throw when taxRate is negative")
    void create_WhenTaxRateNegative_ShouldThrow() {
        assertThatThrownBy(() -> Product.create(
                "clinic-1", "Producto", null, null, null,
                BigDecimal.TEN, new BigDecimal("-0.1"), 5, 1, buildMedicationDetails()))
                .isInstanceOf(ProductValidationException.class)
                .hasMessageContaining("impuesto");
    }

    @Test
    @DisplayName("create: should throw when stock is negative")
    void create_WhenStockNegative_ShouldThrow() {
        assertThatThrownBy(() -> Product.create(
                "clinic-1", "Producto", null, null, null,
                BigDecimal.TEN, BigDecimal.ZERO, -1, 1, buildMedicationDetails()))
                .isInstanceOf(ProductValidationException.class)
                .hasMessageContaining("stock");
    }

    @Test
    @DisplayName("create: should throw when minStock is negative")
    void create_WhenMinStockNegative_ShouldThrow() {
        assertThatThrownBy(() -> Product.create(
                "clinic-1", "Producto", null, null, null,
                BigDecimal.TEN, BigDecimal.ZERO, 5, -1, buildMedicationDetails()))
                .isInstanceOf(ProductValidationException.class)
                .hasMessageContaining("stockMínimo");
    }

    // =========================================================================
    // update
    // =========================================================================

    @Test
    @DisplayName("update: should apply all changes and update timestamps")
    void update_ShouldApplyChanges() {
        Product product = buildMedicationProduct();
        LocalDateTime before = product.getUpdatedAt();
        product.clearDomainEvents();

        ProductDetails newDetails = buildMedicationDetails();
        product.update("Nuevo Nombre", "Nueva descripción", "SKU-002", "BAR-002",
                new BigDecimal("49.99"), new BigDecimal("0.10"), 50, 5, newDetails);

        assertThat(product.getName()).isEqualTo("Nuevo Nombre");
        assertThat(product.getDescription()).isEqualTo("Nueva descripción");
        assertThat(product.getSku()).isEqualTo("SKU-002");
        assertThat(product.getBarcode()).isEqualTo("BAR-002");
        assertThat(product.getPrice()).isEqualByComparingTo("49.99");
        assertThat(product.getTaxRate()).isEqualByComparingTo("0.10");
        assertThat(product.getStock()).isEqualTo(50);
        assertThat(product.getMinStock()).isEqualTo(5);
        assertThat(product.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    @DisplayName("update: should raise ProductUpdatedEvent")
    void update_ShouldRaiseUpdatedEvent() {
        Product product = buildMedicationProduct();
        product.clearDomainEvents();

        product.update("Nuevo Nombre", null, null, null,
                BigDecimal.TEN, BigDecimal.ZERO, 50, 5, buildMedicationDetails());

        assertThat(product.getDomainEvents()).hasSize(1);
        assertThat(product.getDomainEvents().get(0)).isInstanceOf(ProductUpdatedEvent.class);
    }

    @Test
    @DisplayName("update: should throw when product is deactivated")
    void update_WhenDeactivated_ShouldThrow() {
        Product product = buildMedicationProduct();
        product.deactivate("Test");
        product.clearDomainEvents();

        assertThatThrownBy(() -> product.update("Nombre", null, null, null,
                BigDecimal.TEN, BigDecimal.ZERO, 5, 1, buildMedicationDetails()))
                .isInstanceOf(ProductValidationException.class)
                .hasMessageContaining("estado");
    }

    // =========================================================================
    // deactivate
    // =========================================================================

    @Test
    @DisplayName("deactivate: should set isActive to false")
    void deactivate_ShouldSetInactive() {
        Product product = buildMedicationProduct();
        product.clearDomainEvents();

        product.deactivate("Descontinuado");

        assertThat(product.getIsActive()).isFalse();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("deactivate: should raise ProductDeactivatedEvent with reason")
    void deactivate_ShouldRaiseDeactivatedEvent() {
        Product product = buildMedicationProduct();
        product.clearDomainEvents();

        product.deactivate("Descontinuado");

        assertThat(product.getDomainEvents()).hasSize(1);
        assertThat(product.getDomainEvents().get(0)).isInstanceOf(ProductDeactivatedEvent.class);

        ProductDeactivatedEvent event = (ProductDeactivatedEvent) product.getDomainEvents().get(0);
        assertThat(event.getReason()).isEqualTo("Descontinuado");
    }

    @Test
    @DisplayName("deactivate: should throw when already deactivated")
    void deactivate_WhenAlreadyDeactivated_ShouldThrow() {
        Product product = buildMedicationProduct();
        product.deactivate("Primera vez");

        assertThatThrownBy(() -> product.deactivate("Segunda vez"))
                .isInstanceOf(ProductValidationException.class)
                .hasMessageContaining("estado");
    }

    // =========================================================================
    // Domain events lifecycle
    // =========================================================================

    @Test
    @DisplayName("getDomainEvents: should return immutable list")
    void getDomainEvents_ShouldReturnImmutableList() {
        Product product = buildMedicationProduct();
        assertThatThrownBy(() -> product.getDomainEvents().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("clearDomainEvents: should empty the events list")
    void clearDomainEvents_ShouldEmptyEvents() {
        Product product = buildMedicationProduct();
        assertThat(product.getDomainEvents()).isNotEmpty();

        product.clearDomainEvents();

        assertThat(product.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("reconstitute: should not raise domain events")
    void reconstitute_ShouldNotRaiseDomainEvents() {
        ProductDetails details = buildMedicationDetails();
        Product product = Product.reconstitute(
                "prod-1", "clinic-1", "Nombre", "Desc", "SKU", "BAR",
                BigDecimal.TEN, BigDecimal.ZERO, 10, 2, true,
                LocalDateTime.now(), LocalDateTime.now(), details);

        assertThat(product.getDomainEvents()).isEmpty();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Product buildMedicationProduct() {
        return Product.create(
                "clinic-1", "Amoxicilina 500mg", "Antibiótico de amplio espectro",
                "SKU-001", "BAR-001",
                new BigDecimal("29.99"), new BigDecimal("0.21"),
                100, 10, buildMedicationDetails());
    }

    private ProductDetails buildMedicationDetails() {
        return MedicationDetails.create(
                "Amoxicilina", "Comprimidos", "500mg", "Pfizer",
                "REG-001", false, "Temperatura ambiente", "BATCH-001",
                null, List.of("Perro", "Gato"), "Oral");
    }
}
