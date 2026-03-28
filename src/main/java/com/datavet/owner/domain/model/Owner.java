package com.datavet.owner.domain.model;

import com.datavet.owner.domain.event.OwnerCreatedEvent;
import com.datavet.owner.domain.event.OwnerDeletedEvent;
import com.datavet.owner.domain.event.OwnerUpdatedEvent;
import com.datavet.owner.domain.exception.OwnerValidationException;
import com.datavet.shared.domain.model.AggregateRoot;
import com.datavet.shared.domain.model.Document;
import com.datavet.shared.domain.validation.ValidationResult;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Owner extends AggregateRoot<String> implements Document<String> {

    private String ownerId;
    private String clinicId;
    private String name;
    private String lastName;
    private DocumentId documentNumber;
    private Phone phone;
    private Email email;
    private Address address;
    private List<String> petIds = new ArrayList<>();
    private String avatarUrl;
    private boolean active;
    private boolean acceptTermsAndCond;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public String getId() {
        return this.ownerId;
    }

    private void validate(){
        ValidationResult result = new ValidationResult();

        if (name == null || name.isBlank()) {
            result.addError("Nombre", "El nombre del dueño no puede estar vacio o ser nulo");
        }

        if (lastName == null || lastName.isBlank()) {
            result.addError("Apellido", "El apellido del dueño no puede estar vacio o ser nulo");
        }

        if (documentNumber == null) {
            result.addError("Documento", "El documento del dueño no puede ser nulo");
        }

        if (phone == null) {
            result.addError("Telefono", "El telefono del dueño no puede ser nulo");
        }

        if (address == null) {
            result.addError("Direcciones", "La dirección del dueño no puede estar vacio o ser nulo");
        }

        if (acceptTermsAndCond == false) {
            result.addError("Terminos y condiciones", "Los terminos y condiciones deben ser aceptados para poder continuar");
        }

        if (result.hasErrors()) {
            throw new OwnerValidationException(result);
        }
    }

    public static Owner create(
            String clinicId, String name, String lastName, DocumentId documentNumber, Phone phone,
            Email email, Address address, String avatarUrl, boolean acceptTermsAndCond) {

        String uuid = UUID.randomUUID().toString();

        Owner  owner = new Owner(
                uuid, clinicId, name, lastName, documentNumber, phone,
                email, address, new ArrayList<>(), avatarUrl, true,
                acceptTermsAndCond, LocalDateTime.now(), LocalDateTime.now());

        owner.validate();

        owner.addDomainEvent(OwnerCreatedEvent.of(uuid, name, documentNumber.getDocumentNumber()));
        return owner;
    }

    public static Owner reconstitute(
            String id, String clinicId, String name, String lastName, DocumentId document,
            Phone phone, Email email, Address address, List<String> petIds, String avatarUrl,
            boolean active, boolean acceptTermsAndCond, LocalDateTime createdAt, LocalDateTime updateAt){

        return new Owner(id, clinicId, name, lastName, document,
                phone, email, address, petIds, avatarUrl,
                active, acceptTermsAndCond, createdAt, updateAt);
    }


    /**
     * Updates owner information and raises a OwnerUpdatedEvent.
     */

    public void update(
            String name,
            String lastName,
            Email email,
            DocumentId DocumentNumber,
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

        validate();
        addDomainEvent(OwnerUpdatedEvent.of(ownerId, name));
    }

    /**
     * Marks the owner for deletion and raises a OwnerDeletedEvent.
     */

    public void delete(){
        addDomainEvent(OwnerDeletedEvent.of(this.ownerId, this.name));
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
        addDomainEvent(OwnerUpdatedEvent.of(this.ownerId, this.name));
    }

    public void activate() {
        this.active = true;
        this.updateAt = LocalDateTime.now();
    }

    public int getPetCount() {
        return this.petIds.size();
    }
}

