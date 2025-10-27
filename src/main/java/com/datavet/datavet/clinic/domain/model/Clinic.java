package com.datavet.datavet.clinic.domain.model;

import com.datavet.datavet.clinic.domain.event.ClinicCreatedEvent;
import com.datavet.datavet.clinic.domain.event.ClinicDeletedEvent;
import com.datavet.datavet.clinic.domain.event.ClinicUpdatedEvent;
import com.datavet.datavet.shared.domain.model.AggregateRoot;
import com.datavet.datavet.shared.domain.model.Entity;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Clinic extends AggregateRoot<Long> implements Entity<Long> {
    private Long clinicID;
    
    @NotBlank
    @Size(max = 100)
    private String clinicName;
    
    @NotBlank
    @Size(max = 150)
    private String legalName;
    
    @NotBlank
    @Size(max = 50)
    private String legalNumber; //Pasar a NIF
    
    @NotNull
    private Address address;

    private Phone phone;

    @NotNull
    private Email email;

    @Size(max = 255)
    private String logoUrl;
    
    @Size(max = 50)
    private String suscriptionStatus;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Returns the unique identifier of this clinic entity.
     * Implementation of Entity<Long> interface.
     */
    @Override
    public Long getId() {
        return this.clinicID;
    }

    /**
     * Creates a new clinic and raises a ClinicCreatedEvent.
     * This method should be called when a clinic is first created.
     */
    public static Clinic create(Long clinicID, String clinicName, String legalName, String legalNumber,
                               Address address, Phone phone, Email email, String logoUrl, String subscriptionStatus) {
        Clinic clinic = Clinic.builder()
                .clinicID(clinicID)
                .clinicName(clinicName)
                .legalName(legalName)
                .legalNumber(legalNumber)
                .address(address)
                .phone(phone)
                .email(email)
                .logoUrl(logoUrl)
                .suscriptionStatus(subscriptionStatus)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        clinic.addDomainEvent(ClinicCreatedEvent.of(clinicID, clinicName, legalName));
        return clinic;
    }

    /**
     * Updates clinic information and raises a ClinicUpdatedEvent.
     */
    public void update(String clinicName, String legalName, String legalNumber,
                      Address address, Phone phone, Email email, String logoUrl, String subscriptionStatus) {
        this.clinicName = clinicName;
        this.legalName = legalName;
        this.legalNumber = legalNumber;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.logoUrl = logoUrl;
        this.suscriptionStatus = subscriptionStatus;
        this.updatedAt = LocalDateTime.now();
        
        addDomainEvent(ClinicUpdatedEvent.of(this.clinicID, this.clinicName));
    }

    /**
     * Marks the clinic for deletion and raises a ClinicDeletedEvent.
     */
    public void delete() {
        addDomainEvent(ClinicDeletedEvent.of(this.clinicID, this.clinicName));
    }
}