package com.datavet.product.infrastructure.persistence.document;

import com.datavet.product.domain.details.ProductDetails;
import com.datavet.product.domain.valueobject.ProductCategory;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "product")
@CompoundIndexes({
        @CompoundIndex(name = "clinic_idx",          def = "{'clinic_id': 1}"),
        @CompoundIndex(name = "clinic_category_idx", def = "{'clinic_id': 1, 'category': 1}"),
        @CompoundIndex(name = "sku_clinic_idx",      def = "{'sku': 1, 'clinic_id': 1}", unique = true, sparse = true),
        @CompoundIndex(name = "active_idx",          def = "{'is_active': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDocument {

    @Id
    private String id;

    @Field("clinic_id")
    private String clinicId;

    private String          name;
    private String          description;
    private ProductCategory category;
    private String          sku;
    private String          barcode;
    private BigDecimal      price;

    @Field("tax_rate")
    private BigDecimal taxRate;

    private Integer stock;

    @Field("min_stock")
    private Integer minStock;

    @Field("is_active")
    private Boolean isActive;

    @Field("details")
    private ProductDetails details;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
