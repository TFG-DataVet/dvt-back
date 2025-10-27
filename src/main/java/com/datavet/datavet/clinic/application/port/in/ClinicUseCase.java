package com.datavet.datavet.clinic.application.port.in;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.shared.application.port.UseCase;

import java.util.List;

public interface ClinicUseCase extends UseCase {
    Clinic          createClinic    (CreateClinicCommand command);
    Clinic          updateClinic    (UpdateClinicCommand command);
    void            deleteClinic    (Long id);
    Clinic          getClinicById   (Long id);
    List<Clinic>    getAllClinics    ();
}