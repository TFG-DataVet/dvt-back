package com.datavet.appointment.infrastructure.adapter.input.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancelAppointmentRequest {

    private String reason;
}
