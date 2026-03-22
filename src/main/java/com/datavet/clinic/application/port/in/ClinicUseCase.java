package com.datavet.clinic.application.port.in;

import com.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.shared.application.port.UseCase;

import java.util.List;

public interface ClinicUseCase extends UseCase {
    Clinic          createClinic    (CreateClinicCommand command);
    Clinic          updateClinic    (UpdateClinicCommand command);
    void            deleteClinic    (String id);
    Clinic          getClinicById   (String id);
    List<Clinic>    getAllClinics    ();
}