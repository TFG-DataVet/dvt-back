package com.datavet.appointment.infrastructure.adapter.input.dto;

import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateAppointmentRequest {

    private boolean emergency;

    @NotNull(message = "El tipo de cita es obligatorio")
    private AppointmentType type;

    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    @Future(message = "La cita debe ser en el futuro")
    private LocalDateTime scheduledAt;

    // Owner — obligatorio si no es emergencia (validado en dominio)
    private String ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;

    // Pet — obligatorio si no es emergencia (validado en dominio)
    private String petId;
    private String petName;
    private String petSpecies;

    // Employees
    @NotNull(message = "El empleado que crea la cita es obligatorio")
    private String creationEmployeeId;

    private String medicalEmployeeId;

    // Optional
    private String       notes;
    private List<String> productIds;
    private AppointmentSource source;
}
