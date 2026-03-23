package com.datavet.clinic.application.port.in.command;

import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/**
 * Command para crear una clínica en estado PENDING_SETUP.
 * Usado en el paso 1 del onboarding — registro inicial del dueño.
 */
@Value
@Builder
public class CreatePendingClinicCommand {

    @NotNull(message = "El identificador único de la clinica es requerido.")
    String clinicName;

    @NotNull(message = "El email es requerido")
    @Valid
    Email  email;

    @NotNull(message = "El telefono es requerido")
    @Valid
    Phone  phone;
}