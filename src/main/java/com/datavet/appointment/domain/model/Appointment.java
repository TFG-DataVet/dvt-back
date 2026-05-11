package com.datavet.appointment.domain.model;

import com.datavet.appointment.domain.event.AppointmentCancelledEvent;
import com.datavet.appointment.domain.event.AppointmentCreatedEvent;
import com.datavet.appointment.domain.event.AppointmentStatusChangedEvent;
import com.datavet.appointment.domain.exception.AppointmentStateException;
import com.datavet.appointment.domain.exception.AppointmentValidationException;
import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.shared.domain.model.AggregateRoot;
import com.datavet.shared.domain.model.Document;
import com.datavet.shared.domain.validation.ValidationResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Appointment extends AggregateRoot<String> implements Document<String> {

    private String            id;
    private String            clinicId;
    private boolean           emergency;
    private AppointmentType   type;
    private AppointmentStatus status;
    private LocalDateTime     scheduledAt;

    // Owner info (null for emergencies)
    private String  ownerId;
    private String  ownerName;
    private String  ownerEmail;
    private String  ownerPhone;

    // Pet info (null for emergencies)
    private PetSnapshot pet;

    // Employees
    private String creationEmployeeId;
    private String medicalEmployeeId;

    // Additional
    private String       notes;
    private List<String> productIds;
    private AppointmentSource source;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String getId() {
        return this.id;
    }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    public static Appointment create(
            String clinicId,
            boolean emergency,
            AppointmentType type,
            LocalDateTime scheduledAt,
            String ownerId,
            String ownerName,
            String ownerEmail,
            String ownerPhone,
            PetSnapshot pet,
            String creationEmployeeId,
            String medicalEmployeeId,
            String notes,
            List<String> productIds,
            AppointmentSource source) {

        String uuid = UUID.randomUUID().toString();

        Appointment appt = new Appointment(
                uuid,
                clinicId,
                emergency,
                type,
                AppointmentStatus.RESERVADA,
                scheduledAt,
                ownerId,
                ownerName,
                ownerEmail,
                ownerPhone,
                pet,
                creationEmployeeId,
                medicalEmployeeId,
                notes,
                productIds != null ? new ArrayList<>(productIds) : new ArrayList<>(),
                source != null ? source : AppointmentSource.PANEL,
                LocalDateTime.now(),
                null
        );

        appt.validate();
        appt.addDomainEvent(AppointmentCreatedEvent.of(uuid, clinicId, type, emergency, scheduledAt));
        return appt;
    }

    public static Appointment reconstitute(
            String id,
            String clinicId,
            boolean emergency,
            AppointmentType type,
            AppointmentStatus status,
            LocalDateTime scheduledAt,
            String ownerId,
            String ownerName,
            String ownerEmail,
            String ownerPhone,
            PetSnapshot pet,
            String creationEmployeeId,
            String medicalEmployeeId,
            String notes,
            List<String> productIds,
            AppointmentSource source,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        return new Appointment(
                id, clinicId, emergency, type, status, scheduledAt,
                ownerId, ownerName, ownerEmail, ownerPhone, pet,
                creationEmployeeId, medicalEmployeeId,
                notes, productIds, source, createdAt, updatedAt);
    }

    // -------------------------------------------------------------------------
    // Domain methods
    // -------------------------------------------------------------------------

    public void advanceStatus(AppointmentStatus newStatus) {
        if (!isValidTransition(this.status, newStatus)) {
            throw new AppointmentStateException(this.status, newStatus);
        }

        if ((newStatus == AppointmentStatus.FINALIZADA || newStatus == AppointmentStatus.REQUIERE_SEGUIMIENTO)
                && (this.medicalEmployeeId == null || this.medicalEmployeeId.isBlank())) {
            throw new AppointmentValidationException(
                    "medicalEmployeeId",
                    "Se requiere asignar un veterinario antes de finalizar la cita");
        }

        AppointmentStatus previous = this.status;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(AppointmentStatusChangedEvent.of(this.id, this.clinicId, previous, newStatus));
    }

    public void cancel(String reason) {
        if (this.status != AppointmentStatus.RESERVADA) {
            throw new AppointmentStateException(
                    "Solo se puede cancelar una cita en estado RESERVADA. Estado actual: " + this.status);
        }

        AppointmentStatus previous = this.status;
        this.status = AppointmentStatus.CANCELADA;
        this.updatedAt = LocalDateTime.now();

        addDomainEvent(AppointmentCancelledEvent.of(this.id, this.clinicId, reason));
        addDomainEvent(AppointmentStatusChangedEvent.of(this.id, this.clinicId, previous, AppointmentStatus.CANCELADA));
    }

    public void assignMedicalEmployee(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new AppointmentValidationException(
                    "medicalEmployeeId", "El ID del veterinario no puede ser nulo o vacío");
        }
        this.medicalEmployeeId = employeeId;
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private void validate() {
        ValidationResult result = new ValidationResult();

        if (clinicId == null || clinicId.isBlank()) {
            result.addError("clinicId", "El ID de la clínica es obligatorio");
        }

        if (type == null) {
            result.addError("type", "El tipo de cita es obligatorio");
        }

        if (scheduledAt == null) {
            result.addError("scheduledAt", "La fecha y hora de la cita son obligatorias");
        }

        if (creationEmployeeId == null || creationEmployeeId.isBlank()) {
            result.addError("creationEmployeeId", "El empleado que crea la cita es obligatorio");
        }

        if (!emergency) {
            if (ownerId == null || ownerId.isBlank()) {
                result.addError("ownerId", "El dueño es obligatorio para citas no de emergencia");
            }
            if (pet == null) {
                result.addError("pet", "La mascota es obligatoria para citas no de emergencia");
            }
        }

        if (result.hasErrors()) {
            throw new AppointmentValidationException(result);
        }
    }

    // -------------------------------------------------------------------------
    // State machine
    // -------------------------------------------------------------------------

    private boolean isValidTransition(AppointmentStatus from, AppointmentStatus to) {
        return switch (from) {
            case RESERVADA           -> to == AppointmentStatus.CLIENTE_LLEGADO;
            case CLIENTE_LLEGADO     -> to == AppointmentStatus.PROXIMO_A_ATENDER;
            case PROXIMO_A_ATENDER   -> to == AppointmentStatus.EN_CONSULTA;
            case EN_CONSULTA         -> to == AppointmentStatus.FINALIZADA
                                     || to == AppointmentStatus.REQUIERE_SEGUIMIENTO;
            default                  -> false;
        };
    }
}
