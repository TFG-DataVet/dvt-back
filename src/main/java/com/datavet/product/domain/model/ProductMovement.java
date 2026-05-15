package com.datavet.product.domain.model;

import com.datavet.product.domain.event.ProductMovementCreatedEvent;
import com.datavet.product.domain.exception.ProductMovementValidationException;
import com.datavet.product.domain.valueobject.ProductMovementType;
import com.datavet.shared.domain.model.AggregateRoot;
import com.datavet.shared.domain.model.Document;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMovement extends AggregateRoot<String> implements Document<String> {

    private String              id;
    private String              productId;
    private String              clinicId;
    private ProductMovementType type;
    private Integer             quantity;
    private LocalDateTime       date;
    private String              employeeId;
    private String              saleId;
    private String              appointmentId;
    private String              notes;
    private LocalDateTime       createdAt;

    @Override
    public String getId() { return this.id; }

    public static ProductMovement create(String productId,
                                         String clinicId,
                                         ProductMovementType type,
                                         Integer quantity,
                                         LocalDateTime date,
                                         String employeeId,
                                         String appointmentId,
                                         String notes) {
        ValidationResult result = new ValidationResult();

        if (productId == null || productId.isBlank())
            result.addError("productId", "El identificador del producto no puede ser nulo");
        if (clinicId == null || clinicId.isBlank())
            result.addError("clinicId", "El identificador de clínica no puede ser nulo");
        if (type == null)
            result.addError("type", "El tipo de movimiento no puede ser nulo");
        if (quantity == null || quantity <= 0)
            result.addError("quantity", "La cantidad del movimiento debe ser mayor a cero");
        if (employeeId == null || employeeId.isBlank())
            result.addError("employeeId", "El identificador del empleado no puede ser nulo");
        if (date == null)
            result.addError("date", "La fecha del movimiento no puede ser nula");
        if (type == ProductMovementType.EXIT_CONSUMPTION
                && (appointmentId == null || appointmentId.isBlank()))
            result.addError("appointmentId", "El identificador de la cita es obligatorio para un consumo interno");

        if (result.hasErrors()) throw new ProductMovementValidationException(result);

        String uuid   = UUID.randomUUID().toString();
        String saleId = (type == ProductMovementType.EXIT_SALE || type == ProductMovementType.EXIT_CONSUMPTION)
                ? UUID.randomUUID().toString()
                : null;
        String resolvedAppointmentId = (type == ProductMovementType.EXIT_CONSUMPTION) ? appointmentId : null;

        ProductMovement movement = new ProductMovement(
                uuid, productId, clinicId, type, quantity, date,
                employeeId, saleId, resolvedAppointmentId, notes, LocalDateTime.now());

        movement.addDomainEvent(
                ProductMovementCreatedEvent.of(uuid, productId, clinicId, type, quantity));

        return movement;
    }

    public static ProductMovement reconstitute(String id, String productId, String clinicId,
                                                ProductMovementType type, Integer quantity,
                                                LocalDateTime date, String employeeId,
                                                String saleId, String appointmentId,
                                                String notes, LocalDateTime createdAt) {
        return new ProductMovement(id, productId, clinicId, type, quantity, date,
                employeeId, saleId, appointmentId, notes, createdAt);
    }
}
