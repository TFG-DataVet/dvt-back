package com.datavet.clinic.domain.model;

import com.datavet.clinic.domain.event.ClinicCreatedEvent;
import com.datavet.clinic.domain.event.ClinicDeactivatedEvent;
import com.datavet.clinic.domain.event.ClinicPendingCreatedEvent;
import com.datavet.clinic.domain.event.ClinicUpdatedEvent;
import com.datavet.clinic.domain.exception.ClinicValidationException;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.model.AggregateRoot;
import com.datavet.shared.domain.model.Document;
import com.datavet.shared.domain.validation.ValidationResult;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class Clinic extends AggregateRoot<String> implements Document<String> {

    private String clinicID;
    private String clinicName;
    private String legalName;
    private String legalNumber;
    private LegalType legalType;
    private Address address;
    private Phone phone;
    private Email email;
    private String logoUrl;
    private ClinicSchedule schedule;
    private ClinicStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String getId() {
        return this.clinicID;
    }

    private void validateComplete(){
        ValidationResult result = new ValidationResult();

        if (clinicName == null || clinicName.isBlank() ){
            result.addError("Nombre", "El nombre de la clinica no puede estar vacio o ser nulo");
        }

        if (legalName == null || legalName.isBlank() ){
            result.addError("Nombre legal","El nombre legal de la clinica no puede estar vacio o ser nulo");
        }

        if (legalNumber == null || legalNumber.isBlank()){
            result.addError("Numero legal","El numero legal de la veterinaria no puede estar vacio o ser nulo");
        }

        if (legalType == null) {
            result.addError("Tipo de persona", "El tipo de persona jurídica no puede ser nulo");
        }

        if (address == null){
            result.addError("Dirección", "La dirección en la clinica no puede estar vacia o ser nula");
        } else {
            if (address.getCity().isBlank() || address.getCity() == null){
                result.addError("Ciudad","La ciudad en la dirección no puede estar vacía o ser nula");
            }

            if (address.getStreet().isBlank() || address.getStreet() == null) {
                result.addError("Calle", "La calle en la dirección no puede estar vacia o ser nula");
            }

            if (address.getPostalCode().isBlank() || address.getPostalCode() == null) {
                result.addError("Código postal", "El codigo postal en la dirección no puede estar vacio o ser nulo");
            }
        }

        if (phone == null) {
            result.addError("Telefono", "El numero de telefono de la clinica no puede estar vacio o ser nulo");
        }

        if (email == null) {
            result.addError("Email","El email de la clinica no puede estar vacio o ser nulo");
        }

        if (schedule == null) {
            result.addError("Horario", "El horario de atención de la clinica no puede ser nulo");
        }

        if (createdAt == null || createdAt.isAfter(LocalDateTime.now())) {
            result.addError("Fecha de creación", "La fecha de creación de la clinica no puede ser mayor a de la fecha actual");
        }

        if (result.hasErrors()){
            throw new ClinicValidationException(result);
        }

    }

    private void validateInPending(){
        ValidationResult result = new ValidationResult();

        if (clinicName == null || clinicName.isBlank()) {
            result.addError("Nombre", "El nombre de la clinica no puede estar vacio o ser nulo");
        }

        if (email == null) {
            result.addError("Email", "El email del responsable de la clinica veterinaria no puede ser nulo");
        }

        if (phone == null) {
            result.addError("Telefono", "El numero de telefono de la clinica no puede ser nulo");
        }

        if (result.hasErrors()){
            throw new ClinicValidationException(result);
        }
    }

    /**
     * Crea una clínica completamente configurada.
     * Usado exclusivamente por SUPER_ADMIN.
     */
    public static Clinic create(String clinicName, String legalName, String legalNumber,
                                LegalType legalType, Address address, Phone phone, Email email,
                                String logoUrl, ClinicSchedule schedule) {
        String uuid = UUID.randomUUID().toString();

        Clinic clinic = new Clinic(
                uuid,
                clinicName,
                legalName,
                legalNumber,
                legalType,
                address,
                phone,
                email,
                logoUrl,
                schedule,
                ClinicStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());

        clinic.validateComplete();
        clinic.addDomainEvent(ClinicCreatedEvent.of(uuid, clinicName, legalName));
        return clinic;
    }

    /**
     * Crea una clínica en estado PENDING_SETUP.
     * Usado por el flujo de onboarding cuando el dueño se registra.
     */
    public static Clinic createPending(String clinicName, Email ownerEmail, Phone ownerPhone) {
        String uuid = UUID.randomUUID().toString();

        Clinic clinic = new Clinic(
                uuid,
                clinicName,
                null,
                null,
                null,
                null,
                ownerPhone,
                ownerEmail,
                null,
                null,
                ClinicStatus.PENDING_SETUP,
                LocalDateTime.now(),
                null);

        clinic.validateInPending();
        clinic.addDomainEvent(ClinicPendingCreatedEvent.of(uuid, clinicName));
        return clinic;
    }

    // -------------------------------------------------------------------------
    // Métodos de dominio
    // -------------------------------------------------------------------------

    /**
     * Completa el setup de la clínica tras la verificación del email.
     * Transiciona de PENDING_SETUP a ACTIVE.
     * Usado en el paso 3 del onboarding.
     */
    public void completeSetup(String legalName, String legalNumber, LegalType legalType,
                              Address address, Phone phone, Email email,
                              String logoUrl, ClinicSchedule schedule) {
        if (this.status != ClinicStatus.PENDING_SETUP) {
            ValidationResult result = new ValidationResult();
            result.addError("ClinicStatus",
                    "Solo una clínica en estado PENDING_SETUP puede completar su configuración");
            throw new ClinicValidationException(result);
        }

        this.legalName        = legalName;
        this.legalNumber      = legalNumber;
        this.legalType        = legalType;
        this.address          = address;
        this.phone            = phone;
        this.email            = email;
        this.logoUrl          = logoUrl;
        this.schedule         = schedule;
        this.status           = ClinicStatus.ACTIVE;
        this.updatedAt        = LocalDateTime.now();

        this.validateComplete();
        addDomainEvent(ClinicCreatedEvent.of(this.clinicID, this.clinicName, this.legalName));
    }

    /**
     * Reconstituye una clínica desde persistencia.
     * No lanza eventos ni re-valida — los datos ya fueron validados al persistir.
     * Usado exclusivamente por la capa de infraestructura.
     */
    public static Clinic reconstitute(String clinicID,
                                      String clinicName,
                                      String legalName,
                                      String legalNumber,
                                      LegalType legalType,
                                      Address address,
                                      Phone phone,
                                      Email email,
                                      String logoUrl,
                                      ClinicSchedule
                                      schedule,
                                      ClinicStatus status,
                                      LocalDateTime createdAt,
                                      LocalDateTime updatedAt) {
        return new Clinic(
                clinicID,
                clinicName,
                legalName,
                legalNumber,
                legalType,
                address,
                phone,
                email,
                logoUrl,
                schedule,
                status,
                LocalDateTime.now(),
                LocalDateTime.now());
    }


    /**
     * Actualiza los datos de una clínica activa.
     * Usado por CLINIC_OWNER y SUPER_ADMIN.
     */
    public void update(String clinicName, String legalName, String legalNumber,
                       LegalType legalType, Address address, Phone phone, Email email,
                       String logoUrl, ClinicSchedule schedule) {

        if (this.status == ClinicStatus.DEACTIVATED) {
            ValidationResult result = new ValidationResult();
            result.addError("ClinicStatus", "No se puede actualizar una clínica desactivada");
            throw new ClinicValidationException(result);
        }

        this.clinicName       = clinicName;
        this.legalName        = legalName;
        this.legalNumber      = legalNumber;
        this.legalType        = legalType;
        this.address          = address;
        this.phone            = phone;
        this.email            = email;
        this.logoUrl          = logoUrl;
        this.schedule         = schedule;
        this.updatedAt        = LocalDateTime.now();

        this.validateComplete();
        addDomainEvent(ClinicUpdatedEvent.of(this.clinicID, this.clinicName));
    }

    /**
     * Soft-delete: desactiva la clínica conservando todos sus datos.
     * Sustituye al hard-delete anterior.
     */
    public void deactivate(String reason) {
        if (this.status == ClinicStatus.DEACTIVATED) {
            ValidationResult result = new ValidationResult();
            result.addError("ClinicStatus", "La clínica ya está desactivada");
            throw new ClinicValidationException(result);
        }

        this.status    = ClinicStatus.DEACTIVATED;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(ClinicDeactivatedEvent.of(this.clinicID, this.clinicName, reason));
    }
}