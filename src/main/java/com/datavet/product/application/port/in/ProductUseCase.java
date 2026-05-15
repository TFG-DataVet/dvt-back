package com.datavet.product.application.port.in;

import com.datavet.product.application.port.in.command.CreateProductCommand;
import com.datavet.product.application.port.in.command.DeactivateProductCommand;
import com.datavet.product.application.port.in.command.UpdateProductCommand;
import com.datavet.product.domain.model.Product;
import com.datavet.product.domain.valueobject.ClinicArea;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.shared.application.port.UseCase;

import java.util.List;

public interface ProductUseCase extends UseCase {
    Product         createProduct                   (CreateProductCommand command);
    Product         updateProduct                   (String id, UpdateProductCommand command);
    void            deactivateProduct               (DeactivateProductCommand command);
    Product         getProductById                  (String id);
    List<Product>   getProductsByClinic             (String clinicId);
    List<Product>   getProductsByClinicAndCategory  (String clinicId, ProductCategory category);
    List<Product>   getProductsByClinicAndArea      (String clinicId, ClinicArea area);
}
