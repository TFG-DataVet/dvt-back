package com.datavet.product.application.port.in;

import com.datavet.product.application.port.in.command.CreateProductMovementCommand;
import com.datavet.product.domain.model.ProductMovement;
import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.shared.application.port.UseCase;

import java.util.List;

public interface ProductMovementUseCase extends UseCase {
    ProductMovement             createMovement                  (CreateProductMovementCommand command);
    ProductMovement             getMovementById                 (String movementId, String clinicId);
    List<ProductMovement>       getMovementsByProduct           (String productId, String clinicId);
    List<ProductMovement>       getMovementsByProductAndType    (String productId, ProductMovementType type, String clinicId);
    List<ProductMovement>       getMovementsByClinic            (String clinicId);
}
