package com.datavet.product.application.service;

import com.datavet.product.application.factory.ProductDetailsFactory;
import com.datavet.product.application.port.in.ProductUseCase;
import com.datavet.product.application.port.in.command.CreateProductCommand;
import com.datavet.product.application.port.in.command.DeactivateProductCommand;
import com.datavet.product.application.port.in.command.UpdateProductCommand;
import com.datavet.product.application.port.out.ProductRepositoryPort;
import com.datavet.product.domain.details.ProductDetails;
import com.datavet.product.domain.exception.ProductAlreadyExistsException;
import com.datavet.product.domain.exception.ProductNotFoundException;
import com.datavet.product.domain.model.Product;
import com.datavet.product.domain.valueobject.ClinicArea;
import com.datavet.product.domain.valueobject.ProductCategory;
import com.datavet.shared.application.service.ApplicationService;
import com.datavet.shared.domain.event.DomainEvent;
import com.datavet.shared.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase, ApplicationService {

    private final ProductRepositoryPort  productRepositoryPort;
    private final DomainEventPublisher   domainEventPublisher;
    private final ProductDetailsFactory  productDetailsFactory;

    @Override
    public Product createProduct(CreateProductCommand command) {
        if (command.getSku() != null && !command.getSku().isBlank()
                && productRepositoryPort.existsBySkuAndClinicId(command.getSku(), command.getClinicId())) {
            throw new ProductAlreadyExistsException("sku", command.getSku());
        }

        ProductDetails details = productDetailsFactory.create(command.getDetailsRequest());

        Product product = Product.create(
                command.getClinicId(), command.getName(), command.getDescription(),
                command.getSku(), command.getBarcode(),
                command.getPrice(), command.getTaxRate(),
                command.getStock(), command.getMinStock(),
                details);

        publishDomainEvents(product);
        return productRepositoryPort.save(product);
    }

    @Override
    public Product updateProduct(String id, UpdateProductCommand command) {
        Product existing = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product", id));

        if (command.getSku() != null && !command.getSku().isBlank()
                && productRepositoryPort.existsBySkuAndClinicIdAndIdNot(
                        command.getSku(), existing.getClinicId(), id)) {
            throw new ProductAlreadyExistsException("sku", command.getSku());
        }

        ProductDetails details = productDetailsFactory.create(command.getDetailsRequest());

        existing.update(command.getName(), command.getDescription(),
                command.getSku(), command.getBarcode(),
                command.getPrice(), command.getTaxRate(),
                command.getStock(), command.getMinStock(),
                details);

        publishDomainEvents(existing);
        return productRepositoryPort.save(existing);
    }

    @Override
    public void deactivateProduct(DeactivateProductCommand command) {
        Product product = productRepositoryPort.findById(command.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product", command.getProductId()));
        product.deactivate(command.getReason());
        publishDomainEvents(product);
        productRepositoryPort.save(product);
    }

    @Override
    public Product getProductById(String id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product", id));
    }

    @Override
    public List<Product> getProductsByClinic(String clinicId) {
        return productRepositoryPort.findByClinicId(clinicId);
    }

    @Override
    public List<Product> getProductsByClinicAndCategory(String clinicId, ProductCategory category) {
        return productRepositoryPort.findByClinicIdAndCategory(clinicId, category);
    }

    @Override
    public List<Product> getProductsByClinicAndArea(String clinicId, ClinicArea area) {
        return productRepositoryPort.findByClinicIdAndArea(clinicId, area);
    }

    private void publishDomainEvents(Product product) {
        List<DomainEvent> events = product.getDomainEvents();
        events.forEach(domainEventPublisher::publish);
        product.clearDomainEvents();
    }
}
