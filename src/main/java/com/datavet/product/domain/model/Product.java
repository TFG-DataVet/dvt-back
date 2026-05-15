package com.datavet.product.domain.model;

import com.datavet.product.domain.details.ProductDetails;
import com.datavet.product.domain.event.ProductCreatedEvent;
import com.datavet.product.domain.event.ProductDeactivatedEvent;
import com.datavet.product.domain.event.ProductStockUpdatedEvent;
import com.datavet.product.domain.event.ProductUpdatedEvent;
import com.datavet.product.domain.exception.ProductValidationException;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.shared.domain.model.AggregateRoot;
import com.datavet.shared.domain.model.Document;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends AggregateRoot<String> implements Document<String> {

    private String          productId;
    private String          clinicId;
    private String          name;
    private String          description;
    private ProductCategory category;
    private String          sku;
    private String          barcode;
    private BigDecimal      price;
    private BigDecimal      taxRate;
    private Integer         stock;
    private Integer         minStock;
    private Boolean         isActive;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;
    private ProductDetails  details;

    @Override
    public String getId() { return this.productId; }

    private void validateBase() {
        ValidationResult result = new ValidationResult();
        if (clinicId == null || clinicId.isBlank())
            result.addError("clinicId", "El identificador de clínica no puede ser nulo");
        if (name == null || name.isBlank())
            result.addError("nombre", "El nombre del producto no puede estar vacío");
        if (details == null)
            result.addError("details", "Los detalles del producto no pueden ser nulos");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            result.addError("precio", "El precio debe ser mayor o igual a cero");
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) < 0)
            result.addError("impuesto", "La tasa de impuesto debe ser mayor o igual a cero");
        if (stock == null || stock < 0)
            result.addError("stock", "El stock no puede ser negativo");
        if (minStock == null || minStock < 0)
            result.addError("stockMínimo", "El stock mínimo no puede ser negativo");
        if (result.hasErrors()) throw new ProductValidationException(result);
    }

    public static Product create(String clinicId, String name, String description,
                                  String sku, String barcode,
                                  BigDecimal price, BigDecimal taxRate,
                                  Integer stock, Integer minStock,
                                  ProductDetails details) {
        details.validate();
        String uuid = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Product product = new Product(uuid, clinicId, name, description,
                details.getCategory(), sku, barcode, price, taxRate,
                stock, minStock, true, now, now, details);

        product.validateBase();
        product.addDomainEvent(ProductCreatedEvent.of(uuid, clinicId, name));
        return product;
    }

    public static Product reconstitute(String productId, String clinicId, String name,
                                        String description, String sku, String barcode,
                                        BigDecimal price, BigDecimal taxRate,
                                        Integer stock, Integer minStock, Boolean isActive,
                                        LocalDateTime createdAt, LocalDateTime updatedAt,
                                        ProductDetails details) {
        return new Product(productId, clinicId, name, description,
                details != null ? details.getCategory() : null,
                sku, barcode, price, taxRate, stock, minStock,
                isActive, createdAt, updatedAt, details);
    }

    public void update(String name, String description, String sku, String barcode,
                       BigDecimal price, BigDecimal taxRate, Integer stock, Integer minStock,
                       ProductDetails details) {
        if (Boolean.FALSE.equals(this.isActive)) {
            ValidationResult result = new ValidationResult();
            result.addError("estado", "No se puede actualizar un producto desactivado");
            throw new ProductValidationException(result);
        }
        details.validate();
        this.name        = name;
        this.description = description;
        this.sku         = sku;
        this.barcode     = barcode;
        this.price       = price;
        this.taxRate     = taxRate;
        this.stock       = stock;
        this.minStock    = minStock;
        this.details     = details;
        this.category    = details.getCategory();
        this.updatedAt   = LocalDateTime.now();
        validateBase();
        addDomainEvent(ProductUpdatedEvent.of(this.productId, this.clinicId, this.name));
    }

    public void applyMovement(Integer quantity, ProductMovementType type) {
        if (Boolean.FALSE.equals(this.isActive)) {
            ValidationResult result = new ValidationResult();
            result.addError("estado", "No se puede registrar un movimiento sobre un producto desactivado");
            throw new ProductValidationException(result);
        }

        int newStock = switch (type) {
            case ENTRY                              -> this.stock + quantity;
            case EXIT_SALE, EXIT_CONSUMPTION        -> this.stock - quantity;
        };

        if (newStock < 0) {
            ValidationResult result = new ValidationResult();
            result.addError("stock", "Stock insuficiente. Disponible: " + this.stock + ", solicitado: " + quantity);
            throw new ProductValidationException(result);
        }

        this.stock     = newStock;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(ProductStockUpdatedEvent.of(this.productId, this.clinicId, this.stock, type));
    }

    public void deactivate(String reason) {
        if (Boolean.FALSE.equals(this.isActive)) {
            ValidationResult result = new ValidationResult();
            result.addError("estado", "El producto ya está desactivado");
            throw new ProductValidationException(result);
        }
        this.isActive  = false;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(ProductDeactivatedEvent.of(this.productId, this.clinicId, this.name, reason));
    }
}
