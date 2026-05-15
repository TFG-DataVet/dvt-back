package com.datavet.product.infrastructure.persistence.repository;

import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.product.infrastructure.persistence.document.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoProductRepository extends MongoRepository<ProductDocument, String> {
    List<ProductDocument> findByClinicId(String clinicId);
    List<ProductDocument> findByClinicIdAndCategory(String clinicId, ProductCategory category);
    List<ProductDocument> findByClinicIdAndCategoryIn(String clinicId, List<ProductCategory> categories);
    boolean existsBySkuAndClinicId(String sku, String clinicId);
    boolean existsBySkuAndClinicIdAndIdNot(String sku, String clinicId, String id);
}
