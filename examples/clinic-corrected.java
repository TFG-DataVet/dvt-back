// ============================================================================
// CORRECCIÓN: Clinic usando componentes compartidos
// ============================================================================

// ✅ CORRECTO: Clinic.java usando shared components
package com.datavet.datavet.clinic.domain.model;

import com.datavet.datavet.shared.domain.model.Entity;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.datavet.datavet.shared.domain.valueobject.Address;
import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Clinic implements Entity<ClinicId> {  // ← Implementa Entity compartido
    
    private ClinicId id;  // ← Value Object para ID
    
    @NotBlank
    @Size(max = 100)
    private String clinicName;
    
    @NotBlank
    @Size(max = 150)
    private String legalName;
    
    @NotBlank
    @Size(max = 50)
    private String legalNumber;
    
    // ✅ USA componentes compartidos
    private Address address;        // ← Value Object compartido
    private Phone phone;           // ← Value Object compartido  
    private Email email;           // ← Value Object compartido
    
    @Size(max = 255)
    private String logoUrl;
    
    private SubscriptionStatus subscriptionStatus;  // ← Enum del dominio
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // ✅ Métodos de dominio (lógica de negocio)
    public void activate() {
        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
    }
    
    public void deactivate() {
        this.subscriptionStatus = SubscriptionStatus.INACTIVE;
    }
    
    public boolean isActive() {
        return this.subscriptionStatus == SubscriptionStatus.ACTIVE;
    }
    
    // ✅ Validaciones de dominio
    public void validateForUpdate() {
        if (!isActive()) {
            throw new ClinicInactiveException(this.id);
        }
    }
}

// ============================================================================
// ✅ Value Objects específicos del dominio Clinic
// ============================================================================

// ClinicId.java
package com.datavet.datavet.clinic.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ClinicId {
    private final Long value;
    
    public ClinicId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Clinic ID must be positive");
        }
        this.value = value;
    }
}

// SubscriptionStatus.java
package com.datavet.datavet.clinic.domain.model;

public enum SubscriptionStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    TRIAL
}

// ============================================================================
// ✅ CORRECTO: ClinicEntity.java (infraestructura)
// ============================================================================

package com.datavet.datavet.clinic.infrastructure.persistence.entity;

import com.datavet.datavet.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clinic")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClinicEntity extends BaseEntity {  // ← Hereda de BaseEntity compartido
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clinicId;
    
    @Column(nullable = false, length = 100)
    private String clinicName;
    
    @Column(nullable = false, length = 150)
    private String legalName;
    
    @Column(nullable = false, length = 50, unique = true)
    private String legalNumber;
    
    // ✅ Campos simples para persistencia (los Value Objects se convierten aquí)
    @Column(nullable = false, length = 200)
    private String address;
    
    @Column(length = 15)
    private String phone;
    
    @Column(nullable = false, length = 100, unique = true)
    private String email;
    
    @Column(length = 255)
    private String logoUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SubscriptionStatus subscriptionStatus;
    
    // createdAt y updatedAt vienen de BaseEntity
}

// ============================================================================
// ✅ CORRECTO: Mapper que convierte entre Domain y Entity
// ============================================================================

package com.datavet.datavet.clinic.application.mapper;

import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.clinic.domain.model.ClinicId;
import com.datavet.datavet.clinic.infrastructure.persistence.entity.ClinicEntity;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.application.mapper.Mapper;

@Component
public class ClinicEntityMapper implements Mapper<Clinic, ClinicEntity> {
    
    public ClinicEntity toEntity(Clinic clinic) {
        return ClinicEntity.builder()
            .clinicId(clinic.getId().getValue())
            .clinicName(clinic.getClinicName())
            .legalName(clinic.getLegalName())
            .legalNumber(clinic.getLegalNumber())
            // ✅ Convierte Value Objects a strings para persistencia
            .address(clinic.getAddress().toString())
            .phone(clinic.getPhone().toString())
            .email(clinic.getEmail().toString())
            .logoUrl(clinic.getLogoUrl())
            .subscriptionStatus(clinic.getSubscriptionStatus())
            .build();
    }
    
    public Clinic toDomain(ClinicEntity entity) {
        return Clinic.builder()
            .id(new ClinicId(entity.getClinicId()))
            .clinicName(entity.getClinicName())
            .legalName(entity.getLegalName())
            .legalNumber(entity.getLegalNumber())
            // ✅ Convierte strings a Value Objects
            .address(new Address(entity.getAddress()))
            .phone(new Phone(entity.getPhone()))
            .email(new Email(entity.getEmail()))
            .logoUrl(entity.getLogoUrl())
            .subscriptionStatus(entity.getSubscriptionStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}