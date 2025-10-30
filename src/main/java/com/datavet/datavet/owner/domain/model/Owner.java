package com.datavet.datavet.owner.domain.model;

import com.datavet.datavet.owner.domain.event.OwnerCreatedEvent;
import com.datavet.datavet.owner.domain.event.OwnerDeletedEvent;
import com.datavet.datavet.owner.domain.event.OwnerUpdatedEvent;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Entity;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Owner extends AggregateRoot<Long> implements Entity<Long> {

    private Long ownerID;

    private Long clinicID;

    @NotBlank
    @Size(max = 50)
    private String ownerName;

    @NotBlank
    @Size(max = 100)
    private String ownerLastName;

    @NotNull
    @Size(min = 9, max = 9)
    private String ownerDni;

    @NotNull
    private Phone ownerPhone;

    @NotBlank
    private Email ownerEmail;

    @NotNull
    private String ownerPassword;

    @NotNull
    private Address ownerAddress;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @Override
    public Long getId() {
        return this.ownerID;
    }

    public static Owner create(
            Long ownerID,
            Long clinicID,
            String name,
            String lastName,
            String dni,
            Phone phone,
            Email email,
            String password,
            Address address
    )
         {
        Owner  owner = Owner.builder()
                .ownerID(ownerID)
                .clinicID(clinicID)
                .ownerName(name)
                .ownerLastName(lastName)
                .ownerDni(dni)
                .ownerPhone(phone)
                .ownerEmail(email)
                .ownerPassword(password)
                .ownerAddress(address)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        owner.addDomainEvent(OwnerCreatedEvent.of(ownerID, name, dni));
        return owner;
    }

    /**
     * Updates owner information and raises a OwnerUpdatedEvent.
     */

    public void update(
            String name,
            String lastName,
            Email email,
            String dni,
            Address address,
            Phone phone
    ){
        this.ownerName = name;
        this.ownerLastName = lastName;
        this.ownerEmail = email;
        this.ownerDni = dni;
        this.ownerAddress = address;
        this.ownerPhone = phone;
        this.updateAt = LocalDateTime.now();

        addDomainEvent(OwnerUpdatedEvent.of(ownerID, name));
    }

    /**
     * Marks the owner for deletion and raises a OwnerDeletedEvent.
     */

    public void delete(){
        addDomainEvent(OwnerDeletedEvent.of(this.ownerID, this.ownerName));
    }

}
