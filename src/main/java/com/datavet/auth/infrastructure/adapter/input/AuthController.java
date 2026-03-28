package com.datavet.auth.infrastructure.adapter.input;

import com.datavet.auth.application.port.in.command.*;
import com.datavet.auth.application.dto.RegisterResponse;
import com.datavet.auth.application.dto.TokenResponse;
import com.datavet.auth.application.port.in.AuthUseCase;
import com.datavet.auth.application.port.in.command.ChangePasswordCommand;
import com.datavet.auth.application.port.in.command.LoginCommand;
import com.datavet.auth.application.port.in.command.RegisterClinicOwnerCommand;
import com.datavet.auth.application.port.in.command.VerifyEmailCommand;
import com.datavet.auth.domain.model.User;
import com.datavet.auth.infrastructure.adapter.input.dto.*;
import com.datavet.auth.infrastructure.security.AuthenticatedUser;
import com.datavet.auth.infrastructure.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;
    private final JwtUtil     jwtUtil;

    /**
     * Paso 1 del onboarding — registro del dueño de la clínica.
     * Público, sin autenticación.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authUseCase.registerClinicOwner(
                RegisterClinicOwnerCommand.builder()
                        .clinicName(request.getClinicName())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .rawPassword(request.getPassword())
                        .build()
        );

        return ResponseEntity.status(201).body(
                RegisterResponse.of(
                        user.getId(),
                        user.getClinicId(),
                        user.getEmail().getValue()));
    }

    /**
     * Paso 2 del onboarding — verificación del email.
     * Público, sin autenticación.
     * Devuelve JWT temporal con scope ONBOARDING_ONLY.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<TokenResponse> verifyEmail(
            @RequestParam String token) {

        User user = authUseCase.verifyEmail(
                VerifyEmailCommand.builder()
                        .token(token)
                        .build()
        );

        // Generamos JWT temporal para el paso 3
        String onboardingToken = jwtUtil.generateOnboardingToken(
                user.getId(),
                user.getClinicId(),
                user.getEmail().getValue(),
                user.getRole()
        );

        TokenResponse.UserInfo userInfo = new TokenResponse.UserInfo(
                user.getId(),
                user.getEmployeeId(),
                user.getClinicId(),
                user.getEmail().getValue(),
                user.getRole()
        );

        return ResponseEntity.ok(
                TokenResponse.of(onboardingToken, null, 3600L, userInfo, "COMPLETE_SETUP"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(
            @RequestBody @Valid ResendVerificationRequest request) {
        authUseCase.resendVerificationEmail(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    /**
     * Login normal.
     * Público, sin autenticación.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {

        TokenResponse response = authUseCase.login(
                LoginCommand.builder()
                        .email(request.getEmail())
                        .rawPassword(request.getPassword())
                        .build()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Refresh del access token.
     * Público — el refresh token es la credencial.
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        TokenResponse response = authUseCase.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Logout — revoca el refresh token.
     * Requiere autenticación.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authUseCase.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambio de contraseña.
     * Requiere autenticación — el userId viene del SecurityContext.
     */
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {

        authUseCase.changePassword(
                ChangePasswordCommand.builder()
                        .userId(currentUser.getUserId())
                        .currentRawPassword(request.getCurrentPassword())
                        .newRawPassword(request.getNewPassword())
                        .build()
        );

        return ResponseEntity.noContent().build();
    }

    /** Activar cuenta de empleado*/
    @PostMapping("/activate-account")
    public ResponseEntity<Void> activateAccount(
            @Valid @RequestBody ActivateAccountRequest request) {
        authUseCase.activateAccount(request.getToken(), request.getPassword());
        return ResponseEntity.noContent().build();
    }
}