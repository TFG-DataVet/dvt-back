package com.datavet.product.application.port.out;

import com.datavet.product.domain.model.Product;
import com.datavet.product.domain.valueobject.ClinicArea;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.shared.application.port.Repository;

import java.util.List;

public interface ProductRepositoryPort extends Repository<Product, String> {
    List<Product> findByClinicId(String clinicId);
    List<Product> findByClinicIdAndCategory(String clinicId, ProductCategory category);
    List<Product> findByClinicIdAndArea(String clinicId, ClinicArea area);
    boolean existsBySkuAndClinicId(String sku, String clinicId);
    boolean existsBySkuAndClinicIdAndIdNot(String sku, String clinicId, String id);
}
