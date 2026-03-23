package com.datavet.clinic.application.port.in.command;

import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CompleteClinicSetupCommand {
    @NotBlank(message = "El identicador único de la clinica es requerido.")
    private String clinicId;

    @NotBlank(message = "El nombre fiscal de la clinica es requerido.")
    @Size(max = 150, message = "El nombre fiscal de la clinia no debe de tener mas de 150 caracteres.")
    private String legalName;

    @NotBlank(message = "El numero fiscal es requerido")
    @Size(max = 50, message = "El numero fiscal no debe de tener mas de 50 caracteres")
    private String legalNumber;

    @NotNull(message = "El tipo de persona legal es requerido")
    private LegalType legalType;

    @NotNull(message = "La direccion es requerida")
    @Valid
    private Address address;

    @NotNull(message = "El telefono es requerido")
    @Valid
    private Phone phone;

    @NotNull(message = "El email es requerido")
    @Valid
    private Email email;

    @Size(max = 255, message = "La url de la imagen es requerida")
    private String logoUrl;

    @NotNull(message = "El horario de la clinica es requerido")
    private ClinicSchedule schedule;
}

