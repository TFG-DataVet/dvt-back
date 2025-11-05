package com.datavet.datavet.owner.infrastructure.persistence.entity;

import com.datavet.datavet.shared.infrastructure.persistence.converter.AddressConverter;
import com.datavet.datavet.shared.infrastructure.persistence.converter.EmailConverter;
import com.datavet.datavet.shared.infrastructure.persistence.converter.PhoneConverter;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import com.datavet.datavet.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "owner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ownerID;

    @Column(nullable = false)
    private Long clinicID;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String lastName;

    @NotBlank
    @Size(max = 9)
    @Column(nullable = false, length = 9)
    private String dni;

    @Convert(converter = PhoneConverter.class)
    @Column(name= "phone", nullable = false, length = 10)
    private Phone phone;


    @Convert(converter = EmailConverter.class)
    @Column(name = "email", nullable = false, length = 100)
    private Email email;

    @Convert(converter = AddressConverter.class)
    @Column(name = "address", nullable = false, length = 500)
    private Address address;

}
