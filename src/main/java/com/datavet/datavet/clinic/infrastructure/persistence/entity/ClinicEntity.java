package com.datavet.datavet.clinic.infrastructure.persistence.entity;

import com.datavet.datavet.clinic.infrastructure.persistence.converter.AddressConverter;
import com.datavet.datavet.clinic.infrastructure.persistence.converter.EmailConverter;
import com.datavet.datavet.clinic.infrastructure.persistence.converter.PhoneConverter;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.datavet.datavet.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "clinic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClinicEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clinicId;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String clinicName;
    
    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String legalName;
    
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String legalNumber;
    
    @Convert(converter = AddressConverter.class)
    @Column(name = "address", nullable = false, length = 500)
    private Address address;
    
    @Convert(converter = PhoneConverter.class)
    @Column(name = "phone", length = 15)
    private Phone phone;
    
    @Convert(converter = EmailConverter.class)
    @Column(name = "email", nullable = false, length = 100)
    private Email email;
    
    @Size(max = 255)
    @Column(length = 255)
    private String logoUrl;
    
    @Size(max = 50)
    @Column(length = 50)
    private String suscriptionStatus;
}