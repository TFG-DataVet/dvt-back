package com.datavet.product.infrastructure.adapter.input;

import com.datavet.product.application.dto.ProductMovementResponse;
import com.datavet.product.application.mapper.ProductMovementMapper;
import com.datavet.product.application.port.in.ProductMovementUseCase;
import com.datavet.product.application.port.in.command.CreateProductMovementCommand;
import com.datavet.product.domain.model.ProductMovement;
import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.product.infrastructure.adapter.input.dto.CreateProductMovementRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-movement")
@RequiredArgsConstructor
public class ProductMovementController {

    private final ProductMovementUseCase productMovementUseCase;

    @PostMapping("/product/{productId}/clinic/{clinicId}")
    public ResponseEntity<ProductMovementResponse> create(
            @PathVariable String productId,
            @PathVariable String clinicId,
            @Valid @RequestBody CreateProductMovementRequest request) {

        CreateProductMovementCommand command = CreateProductMovementCommand.builder()
                .productId(productId)
                .clinicId(clinicId)
                .type(request.getType())
                .quantity(request.getQuantity())
                .date(request.getDate())
                .employeeId(request.getEmployeeId())
                .appointmentId(request.getAppointmentId())
                .notes(request.getNotes())
                .build();

        ProductMovement movement = productMovementUseCase.createMovement(command);
        return ResponseEntity.status(201).body(ProductMovementMapper.toResponse(movement));
    }

    @GetMapping("/{movementId}/clinic/{clinicId}")
    public ResponseEntity<ProductMovementResponse> getById(
            @PathVariable String movementId,
            @PathVariable String clinicId) {
        return ResponseEntity.ok(
                ProductMovementMapper.toResponse(
                        productMovementUseCase.getMovementById(movementId, clinicId)));
    }

    @GetMapping("/product/{productId}/clinic/{clinicId}")
    public ResponseEntity<List<ProductMovementResponse>> getByProduct(
            @PathVariable String productId,
            @PathVariable String clinicId,
            @RequestParam(required = false) ProductMovementType type) {

        List<ProductMovement> movements = (type != null)
                ? productMovementUseCase.getMovementsByProductAndType(productId, type, clinicId)
                : productMovementUseCase.getMovementsByProduct(productId, clinicId);

        return ResponseEntity.ok(movements.stream().map(ProductMovementMapper::toResponse).toList());
    }

    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<ProductMovementResponse>> getByClinic(
            @PathVariable String clinicId) {
        return ResponseEntity.ok(
                productMovementUseCase.getMovementsByClinic(clinicId)
                        .stream().map(ProductMovementMapper::toResponse).toList());
    }
}
