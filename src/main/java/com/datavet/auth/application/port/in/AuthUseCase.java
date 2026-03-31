package com.datavet.auth.application.port.in;

import com.datavet.auth.application.port.in.command.*;
import com.datavet.auth.application.dto.TokenResponse;
import com.datavet.auth.domain.model.User;
import com.datavet.clinic.application.port.in.command.CompleteClinicSetupCommand;
import com.datavet.shared.application.port.UseCase;

public interface AuthUseCase extends UseCase {

    // Onboarding
    User        registerClinicOwner (RegisterClinicOwnerCommand command);
    User        verifyEmail         (VerifyEmailCommand command);

    // Acceso
    TokenResponse login             (LoginCommand command);
    TokenResponse refreshToken      (String refreshToken);
    void          logout            (String refreshToken);

    // Gestión de credenciales
    void          changePassword    (ChangePasswordCommand command);

    // Creación de usuarios empleados (llamado desde EmployeeService)
    User          createEmployeeUser(CreateEmployeeUserCommand command);

    // Desactivación (llamado desde EmployeeService al desactivar Employee)
    void          deactivateUser    (String userId, String reason);

    TokenResponse completeOnboarding(CompleteClinicSetupCommand command);

    void resendVerificationEmail(String email);

    void activateAccount(String token, String rawPassword);
}