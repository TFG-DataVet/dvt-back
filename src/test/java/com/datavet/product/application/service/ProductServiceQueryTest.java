package com.datavet.product.application.service;

import com.datavet.product.application.factory.ProductDetailsFactory;
import com.datavet.product.application.port.in.command.DeactivateProductCommand;
import com.datavet.product.application.port.in.command.UpdateProductCommand;
import com.datavet.product.application.port.in.command.request.MedicationDetailsRequest;
import com.datavet.product.application.port.out.ProductRepositoryPort;
import com.datavet.product.domain.details.MedicationDetails;
import com.datavet.product.domain.exception.ProductAlreadyExistsException;
import com.datavet.product.domain.exception.ProductNotFoundException;
import com.datavet.product.domain.model.Product;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.shared.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - query/update/deactivate Tests")
class ProductServiceQueryTest {

    private ProductService productService;

    @Mock private ProductRepositoryPort productRepositoryPort;
    @Mock private DomainEventPublisher  domainEventPublisher;
    @Mock private ProductDetailsFactory productDetailsFactory;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                productRepositoryPort, domainEventPublisher, productDetailsFactory);
    }

    // =========================================================================
    // getProductById
    // =========================================================================

    @Test
    @DisplayName("getProductById: should return product when found")
    void getById_WhenFound_ShouldReturn() {
        Product product = buildProduct("prod-1");
        when(productRepositoryPort.findById("prod-1")).thenReturn(Optional.of(product));

        Product result = productService.getProductById("prod-1");

        assertThat(result.getProductId()).isEqualTo("prod-1");
    }

    @Test
    @DisplayName("getProductById: should throw when not found")
    void getById_WhenNotFound_ShouldThrow() {
        when(productRepositoryPort.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById("unknown"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // =========================================================================
    // getProductsByClinic
    // =========================================================================

    @Test
    @DisplayName("getProductsByClinic: should delegate to repository")
    void getByClinic_ShouldDelegate() {
        List<Product> products = List.of(buildProduct("p1"), buildProduct("p2"));
        when(productRepositoryPort.findByClinicId("clinic-1")).thenReturn(products);

        List<Product> result = productService.getProductsByClinic("clinic-1");

        assertThat(result).hasSize(2);
        verify(productRepositoryPort).findByClinicId("clinic-1");
    }

    // =========================================================================
    // getProductsByClinicAndCategory
    // =========================================================================

    @Test
    @DisplayName("getProductsByClinicAndCategory: should delegate to repository with category filter")
    void getByClinicAndCategory_ShouldDelegate() {
        List<Product> products = List.of(buildProduct("p1"));
        when(productRepositoryPort.findByClinicIdAndCategory("clinic-1", ProductCategory.MEDICATION))
                .thenReturn(products);

        List<Product> result = productService.getProductsByClinicAndCategory("clinic-1", ProductCategory.MEDICATION);

        assertThat(result).hasSize(1);
        verify(productRepositoryPort).findByClinicIdAndCategory("clinic-1", ProductCategory.MEDICATION);
    }

    // =========================================================================
    // updateProduct
    // =========================================================================

    @Test
    @DisplayName("updateProduct: should update and save product")
    void update_ShouldSaveUpdatedProduct() {
        Product product = buildProduct("prod-1");
        when(productRepositoryPort.findById("prod-1")).thenReturn(Optional.of(product));
        when(productDetailsFactory.create(any())).thenReturn(buildMedicationDetails());
        when(productRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct("prod-1", buildUpdateCommand("SKU-NEW"));

        assertThat(result.getName()).isEqualTo("Producto Actualizado");
        verify(productRepositoryPort).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct: should throw when product not found")
    void update_WhenNotFound_ShouldThrow() {
        when(productRepositoryPort.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct("unknown", buildUpdateCommand("SKU-NEW")))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("updateProduct: should throw when SKU already used by another product")
    void update_WhenSkuConflict_ShouldThrow() {
        Product product = buildProduct("prod-1");
        when(productRepositoryPort.findById("prod-1")).thenReturn(Optional.of(product));
        when(productRepositoryPort.existsBySkuAndClinicIdAndIdNot("SKU-TAKEN", "clinic-1", "prod-1"))
                .thenReturn(true);

        assertThatThrownBy(() -> productService.updateProduct("prod-1", buildUpdateCommand("SKU-TAKEN")))
                .isInstanceOf(ProductAlreadyExistsException.class);

        verify(productRepositoryPort, never()).save(any());
    }

    // =========================================================================
    // deactivateProduct
    // =========================================================================

    @Test
    @DisplayName("deactivateProduct: should set product as inactive")
    void deactivate_ShouldSetInactive() {
        Product product = buildProduct("prod-1");
        when(productRepositoryPort.findById("prod-1")).thenReturn(Optional.of(product));
        when(productRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        productService.deactivateProduct(DeactivateProductCommand.builder()
                .productId("prod-1")
                .reason("Descontinuado")
                .build());

        verify(productRepositoryPort).save(argThat(p -> Boolean.FALSE.equals(p.getIsActive())));
    }

    @Test
    @DisplayName("deactivateProduct: should throw when product not found")
    void deactivate_WhenNotFound_ShouldThrow() {
        when(productRepositoryPort.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deactivateProduct(
                DeactivateProductCommand.builder().productId("unknown").reason("x").build()))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Product buildProduct(String id) {
        MedicationDetails details = buildMedicationDetails();
        Product product = Product.create(
                "clinic-1", "Amoxicilina 500mg", "Desc",
                "SKU-001", "BAR-001",
                new BigDecimal("29.99"), new BigDecimal("0.21"),
                100, 10, details);
        product.clearDomainEvents();
        // Use reconstitute to set a known ID
        return Product.reconstitute(
                id, "clinic-1", "Amoxicilina 500mg", "Desc", "SKU-001", "BAR-001",
                new BigDecimal("29.99"), new BigDecimal("0.21"),
                100, 10, true, product.getCreatedAt(), product.getUpdatedAt(), details);
    }

    private UpdateProductCommand buildUpdateCommand(String sku) {
        return UpdateProductCommand.builder()
                .name("Producto Actualizado")
                .description("Desc actualizada")
                .sku(sku)
                .barcode("BAR-NEW")
                .price(new BigDecimal("39.99"))
                .taxRate(new BigDecimal("0.21"))
                .stock(80)
                .minStock(5)
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
