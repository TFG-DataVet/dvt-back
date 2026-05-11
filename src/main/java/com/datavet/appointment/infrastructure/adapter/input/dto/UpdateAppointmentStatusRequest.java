package com.datavet.appointment.infrastructure.adapter.input.dto;

import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAppointmentStatusRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private AppointmentStatus newStatus;

    private String medicalEmployeeId;
}
