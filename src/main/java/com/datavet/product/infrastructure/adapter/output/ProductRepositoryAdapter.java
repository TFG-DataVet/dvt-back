package com.datavet.product.infrastructure.adapter.output;

import com.datavet.product.application.port.out.ProductRepositoryPort;
import com.datavet.product.domain.model.Product;
import com.datavet.product.domain.valueobject.ClinicArea;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.product.infrastructure.persistence.document.ProductDocument;
import com.datavet.product.infrastructure.persistence.repository.MongoProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final MongoProductRepository repository;

    private ProductDocument toDocument(Product product) {
        return ProductDocument.builder()
                .id(product.getProductId())
                .clinicId(product.getClinicId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .price(product.getPrice())
                .taxRate(product.getTaxRate())
                .stock(product.getStock())
                .minStock(product.getMinStock())
                .isActive(product.getIsActive())
                .details(product.getDetails())
                .build();
    }

    private Product toDomain(ProductDocument doc) {
        return Product.reconstitute(
                doc.getId(), doc.getClinicId(), doc.getName(), doc.getDescription(),
                doc.getSku(), doc.getBarcode(), doc.getPrice(), doc.getTaxRate(),
                doc.getStock(), doc.getMinStock(), doc.getIsActive(),
                doc.getCreatedAt(), doc.getUpdatedAt(),
                doc.getDetails());
    }

    @Override
    public Product save(Product product) {
        return toDomain(repository.save(toDocument(product)));
    }

    @Override
    public Optional<Product> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public List<Product> findByClinicId(String clinicId) {
        return repository.findByClinicId(clinicId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Product> findByClinicIdAndCategory(String clinicId, ProductCategory category) {
        return repository.findByClinicIdAndCategory(clinicId, category).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Product> findByClinicIdAndArea(String clinicId, ClinicArea area) {
        return repository.findByClinicIdAndCategoryIn(clinicId, ProductCategory.ofArea(area))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsBySkuAndClinicId(String sku, String clinicId) {
        return repository.existsBySkuAndClinicId(sku, clinicId);
    }

    @Override
    public boolean existsBySkuAndClinicIdAndIdNot(String sku, String clinicId, String id) {
        return repository.existsBySkuAndClinicIdAndIdNot(sku, clinicId, id);
    }
}
