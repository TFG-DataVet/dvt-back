package com.datavet.product.infrastructure.adapter.input;

import com.datavet.product.application.dto.ProductResponse;
import com.datavet.product.application.mapper.ProductMapper;
import com.datavet.product.application.port.in.ProductUseCase;
import com.datavet.product.application.port.in.command.CreateProductCommand;
import com.datavet.product.application.port.in.command.DeactivateProductCommand;
import com.datavet.product.application.port.in.command.UpdateProductCommand;
import com.datavet.product.domain.model.Product;
import com.datavet.product.domain.valueobject.ClinicArea;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.product.infrastructure.adapter.input.dto.CreateProductRequest;
import com.datavet.product.infrastructure.adapter.input.dto.DeactivateProductRequest;
import com.datavet.product.infrastructure.adapter.input.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;

    @PostMapping("/clinic/{clinicId}")
    public ResponseEntity<ProductResponse> create(
            @PathVariable String clinicId,
            @Valid @RequestBody CreateProductRequest request) {

        CreateProductCommand command = CreateProductCommand.builder()
                .clinicId(clinicId)
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .price(request.getPrice())
                .taxRate(request.getTaxRate())
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .detailsRequest(request.getDetails())
                .build();

        Product product = productUseCase.createProduct(command);
        return ResponseEntity.status(201).body(ProductMapper.toResponse(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(ProductMapper.toResponse(productUseCase.getProductById(id)));
    }

    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<ProductResponse>> getByClinic(
            @PathVariable String clinicId,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) ClinicArea area) {

        List<Product> products;
        if (category != null) {
            products = productUseCase.getProductsByClinicAndCategory(clinicId, category);
        } else if (area != null) {
            products = productUseCase.getProductsByClinicAndArea(clinicId, area);
        } else {
            products = productUseCase.getProductsByClinic(clinicId);
        }

        return ResponseEntity.ok(products.stream().map(ProductMapper::toResponse).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {

        UpdateProductCommand command = UpdateProductCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .price(request.getPrice())
                .taxRate(request.getTaxRate())
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .detailsRequest(request.getDetails())
                .build();

        Product updated = productUseCase.updateProduct(id, command);
        return ResponseEntity.ok(ProductMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable String id,
            @RequestBody(required = false) DeactivateProductRequest request) {

        String reason = (request != null) ? request.getReason() : null;
        productUseCase.deactivateProduct(DeactivateProductCommand.builder()
                .productId(id)
                .reason(reason)
                .build());
        return ResponseEntity.noContent().build();
    }
}
