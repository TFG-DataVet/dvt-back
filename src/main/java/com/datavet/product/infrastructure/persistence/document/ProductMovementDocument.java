package com.datavet.product.infrastructure.persistence.document;

import com.datavet.product.domain.valueobject.ProductMovementType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "product_movement")
@CompoundIndexes({
        @CompoundIndex(name = "product_idx",      def = "{'product_id': 1}"),
        @CompoundIndex(name = "clinic_idx",        def = "{'clinic_id': 1}"),
        @CompoundIndex(name = "product_type_idx",  def = "{'product_id': 1, 'type': 1}"),
        @CompoundIndex(name = "clinic_type_idx",   def = "{'clinic_id': 1, 'type': 1}"),
        @CompoundIndex(name = "appointment_idx",   def = "{'appointment_id': 1}", sparse = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMovementDocument {

    @Id
    private String id;

    @Field("product_id")
    private String productId;

    @Field("clinic_id")
    private String clinicId;

    private ProductMovementType type;

    private Integer quantity;

    private LocalDateTime date;

    @Field("employee_id")
    private String employeeId;

    @Field("sale_id")
    private String saleId;

    @Field("appointment_id")
    private String appointmentId;

    private String notes;

    @Field("created_at")
    private LocalDateTime createdAt;
}
