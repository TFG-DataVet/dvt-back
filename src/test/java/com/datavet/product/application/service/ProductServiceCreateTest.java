package com.datavet.product.application.service;

import com.datavet.product.application.factory.ProductDetailsFactory;
import com.datavet.product.application.port.in.command.CreateProductCommand;
import com.datavet.product.application.port.in.command.request.MedicationDetailsRequest;
import com.datavet.product.application.port.out.ProductRepositoryPort;
import com.datavet.product.domain.details.MedicationDetails;
import com.datavet.product.domain.exception.ProductAlreadyExistsException;
import com.datavet.product.domain.model.Product;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - create Tests")
class ProductServiceCreateTest {

    private ProductService productService;

    @Mock private ProductRepositoryPort  productRepositoryPort;
    @Mock private DomainEventPublisher   domainEventPublisher;
    @Mock private ProductDetailsFactory  productDetailsFactory;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                productRepositoryPort, domainEventPublisher, productDetailsFactory);
    }

    // =========================================================================
    // Happy path
    // =========================================================================

    @Test
    @DisplayName("Should create and save a product")
    void create_ShouldSaveProduct() {
        MedicationDetails details = buildMedicationDetails();
        when(productDetailsFactory.create(any())).thenReturn(details);
        when(productRepositoryPort.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct(buildCreateCommand("SKU-001"));

        assertThat(result).isNotNull();
        assertThat(result.getClinicId()).isEqualTo("clinic-1");
        assertThat(result.getCategory()).isEqualTo(ProductCategory.MEDICATION);
        verify(productRepositoryPort).save(any(Product.class));
    }

    @Test
    @DisplayName("Should publish domain events after creating")
    void create_ShouldPublishDomainEvents() {
        when(productDetailsFactory.create(any())).thenReturn(buildMedicationDetails());
        when(productRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        productService.createProduct(buildCreateCommand("SKU-001"));

        verify(domainEventPublisher, atLeastOnce()).publish(any(DomainEvent.class));
    }

    @Test
    @DisplayName("Should clear domain events after publishing")
    void create_ShouldClearEventsAfterPublishing() {
        when(productDetailsFactory.create(any())).thenReturn(buildMedicationDetails());
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        when(productRepositoryPort.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        productService.createProduct(buildCreateCommand("SKU-001"));

        assertThat(captor.getValue().getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Should skip SKU uniqueness check when SKU is null")
    void create_WhenSkuNull_ShouldNotCheckUniqueness() {
        when(productDetailsFactory.create(any())).thenReturn(buildMedicationDetails());
        when(productRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        productService.createProduct(buildCreateCommand(null));

        verify(productRepositoryPort, never()).existsBySkuAndClinicId(any(), any());
    }

    // =========================================================================
    // SKU uniqueness
    // =========================================================================

    @Test
    @DisplayName("Should throw ProductAlreadyExistsException when SKU already exists in clinic")
    void create_WhenSkuAlreadyExists_ShouldThrow() {
        when(productRepositoryPort.existsBySkuAndClinicId("SKU-001", "clinic-1")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(buildCreateCommand("SKU-001")))
                .isInstanceOf(ProductAlreadyExistsException.class);

        verify(productRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should check SKU uniqueness only when SKU is provided")
    void create_WhenSkuProvided_ShouldCheckUniqueness() {
        when(productDetailsFactory.create(any())).thenReturn(buildMedicationDetails());
        when(productRepositoryPort.existsBySkuAndClinicId("SKU-001", "clinic-1")).thenReturn(false);
        when(productRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        productService.createProduct(buildCreateCommand("SKU-001"));

        verify(productRepositoryPort).existsBySkuAndClinicId("SKU-001", "clinic-1");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private CreateProductCommand buildCreateCommand(String sku) {
        return CreateProductCommand.builder()
                .clinicId("clinic-1")
                .name("Amoxicilina 500mg")
                .description("Antibiótico")
                .sku(sku)
                .barcode("BAR-001")
                .price(new BigDecimal("29.99"))
                .taxRate(new BigDecimal("0.21"))
                .stock(100)
                .minStock(10)
                .detailsRequest(new MedicationDetailsRequest())
                .build();
    }

    private MedicationDetails buildMedicationDetails() {
        return MedicationDetails.create(
                "Amoxicilina", "Comprimidos", "500mg", "Pfizer",
                "REG-001", false, "Temperatura ambiente", "BATCH-001",
                null, List.of("Perro", "Gato"), "Oral");
    }
}
