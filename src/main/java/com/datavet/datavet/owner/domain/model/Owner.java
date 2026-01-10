package com.datavet.datavet.owner.domain.model;

import com.datavet.datavet.owner.domain.event.OwnerCreatedEvent;
import com.datavet.datavet.owner.domain.event.OwnerDeletedEvent;
import com.datavet.datavet.owner.domain.event.OwnerUpdatedEvent;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Document;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Owner extends AggregateRoot<String> implements Document<String> {

    private String id;

    private String clinicId;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotNull
    @Size(min = 9, max = 9)
    private String documentNumber;

    @NotNull
    private Phone phone;

    @NotBlank
    private Email email;

    @NotNull
    private Address address;

    @Builder.Default
    private List<String> petIds = new ArrayList<>();

    private String avatarUrl;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @Override
    public String getId() {
        return this.id;
    }

    public static Owner create(
            String id,
            String clinicId,
            String name,
            String lastName,
            String documentNumber,
            Phone phone,
            Email email,
            Address address,
            String avatarUrl) {
        Owner  owner = Owner.builder()
                .id(id)
                .clinicId(clinicId)
                .name(name)
                .lastName(lastName)
                .documentNumber(documentNumber)
                .phone(phone)
                .email(email)
                .address(address)
                .avatarUrl(avatarUrl)
                .petIds(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .active(true)
                .build();

        owner.addDomainEvent(OwnerCreatedEvent.of(id, name, documentNumber));
        return owner;
    }

    /**
     * Updates owner information and raises a OwnerUpdatedEvent.
     */

    public void update(
            String name,
            String lastName,
            Email email,
            String DocumentNumber,
            Address address,
            Phone phone,
            String avatarUrl
    ){
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.documentNumber = DocumentNumber;
        this.address = address;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.updateAt = LocalDateTime.now();

        addDomainEvent(OwnerUpdatedEvent.of(id, name));
    }

    /**
     * Marks the owner for deletion and raises a OwnerDeletedEvent.
     */

    public void delete(){
        addDomainEvent(OwnerDeletedEvent.of(this.id, this.name));
    }

    public List<String> getPetIds() {
        return Collections.unmodifiableList(petIds);
    }

    public void addPet(String petId) {
        if(petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID cannot be null or blank");
        }

        if (!this.petIds.contains(petId)) {
            this.petIds.add(petId);
            this.updateAt = LocalDateTime.now();
        }
    }

    public void removePet(String petId) {
        if (this.petIds.remove(petId)) {
            this.updateAt = LocalDateTime.now();
        }
    }

    public void desactivate() {
        this.active = false;
        this.updateAt = LocalDateTime.now();
        addDomainEvent(OwnerUpdatedEvent.of(this.id, this.name));
    }

    public void activate() {
        this.active = true;
        this.updateAt = LocalDateTime.now();
    }

    public int getPetCount() {
        return this.petIds.size();
    }
}

