package com.datavet.clinic.application.port.in;

import com.datavet.clinic.application.port.in.command.CompleteClinicSetupCommand;
import com.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.clinic.application.port.in.command.CreatePendingClinicCommand;
import com.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.shared.application.port.UseCase;

import java.util.List;

public interface ClinicUseCase extends UseCase {
    Clinic          createClinic        (CreateClinicCommand command);
    Clinic          createPendingClinic (CreatePendingClinicCommand command);
    Clinic          completeClinicSetup (CompleteClinicSetupCommand command);
    Clinic          updateClinic        (UpdateClinicCommand command);
    void            deactivateClinic    (String id, String reason);
    Clinic          getClinicById       (String id);
    List<Clinic>    getAllClinics        ();
}