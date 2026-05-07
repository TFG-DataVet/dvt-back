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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;
    private final JwtUtil     jwtUtil;

    // -------------------------------------------------------------------------
    // Paso 1 — Registro
    // No genera tokens, solo crea el usuario y envía el email
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Paso 2 — Verificación de email
    // Genera JWT temporal de onboarding — lo devolvemos en el BODY (no en cookie)
    // porque es un token de transición de un solo uso, no una sesión
    // -------------------------------------------------------------------------

    @GetMapping("/verify-email")
    public ResponseEntity<TokenResponse> verifyEmail(
            @RequestParam String token) {

        User user = authUseCase.verifyEmail(
                VerifyEmailCommand.builder()
                        .token(token)
                        .build()
        );

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

        // El token temporal va en el body — el frontend lo guarda en memoria,
        // no en localStorage. Desaparece si el usuario cierra el tab.
        return ResponseEntity.ok(
                TokenResponse.of(onboardingToken, null, 3600L, userInfo, "COMPLETE_SETUP"));
    }


    // -------------------------------------------------------------------------
    // Reenvío de verificación
    // -------------------------------------------------------------------------

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(
            @RequestBody @Valid ResendVerificationRequest request) {
        authUseCase.resendVerificationEmail(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Login — escribe las cookies HttpOnly
    // -------------------------------------------------------------------------

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse) {

        TokenResponse tokenResponse = authUseCase.login(
                LoginCommand.builder()
                        .email(request.getEmail())
                        .rawPassword(request.getPassword())
                        .build()
        );

        // Si el usuario aún no completó el onboarding, devolvemos el token
        // temporal en el body igual que en el paso 2, sin escribir cookies
        if ("COMPLETE_SETUP".equals(tokenResponse.getNextStep())) {
            return ResponseEntity.ok(tokenResponse);
        }

        // Sesión normal — escribimos los tokens en cookies HttpOnly
        writeAuthCookies(httpResponse, tokenResponse);

        // Devolvemos solo el userInfo en el body, sin los tokens
        return ResponseEntity.ok(tokenResponse.withoutTokens());
    }

    // -------------------------------------------------------------------------
    // Refresh — rota las cookies HttpOnly
    // -------------------------------------------------------------------------

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        // Leemos el refreshToken desde la cookie, no desde el body
        String refreshToken = extractCookieValue(httpRequest, "refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        TokenResponse tokenResponse = authUseCase.refreshToken(refreshToken);

        // Escribimos las nuevas cookies con los tokens rotados
        writeAuthCookies(httpResponse, tokenResponse);

        return ResponseEntity.ok(tokenResponse.withoutTokens());
    }

    // -------------------------------------------------------------------------
    // Logout — vacía las cookies
    // -------------------------------------------------------------------------

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String refreshToken = extractCookieValue(httpRequest, "refreshToken");

        if (refreshToken != null) {
            authUseCase.logout(refreshToken);
        }

        // Sobreescribimos las cookies con valor vacío y MaxAge=0
        // El navegador las elimina automáticamente
        clearAuthCookies(httpResponse);

        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Cambio de contraseña
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Activar cuenta de empleado
    // -------------------------------------------------------------------------

    @PostMapping("/activate-account")
    public ResponseEntity<Void> activateAccount(
            @Valid @RequestBody ActivateAccountRequest request) {
        authUseCase.activateAccount(request.getToken(), request.getPassword());
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Recuperación de contraseña
    // -------------------------------------------------------------------------

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authUseCase.requestPasswordReset(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authUseCase.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }


    // -------------------------------------------------------------------------
    // Me — devuelve el usuario actual si la cookie es válida
    // -------------------------------------------------------------------------

    @GetMapping("/me")
    public ResponseEntity<TokenResponse> me(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        TokenResponse.UserInfo userInfo = new TokenResponse.UserInfo(
                currentUser.getUserId(),
                currentUser.getEmployeeId(),
                currentUser.getClinicId(),
                currentUser.getEmail(),
                currentUser.getRole()
        );

        return ResponseEntity.ok(TokenResponse.of(null, null, 0L, userInfo, null));
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    /**
     * Escribe el accessToken y el refreshToken como cookies HttpOnly.
     * Se llama en login y en refresh.
     */
    private void writeAuthCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie accessCookie = ResponseCookie
                .from("accessToken", tokenResponse.getAccessToken())
                .httpOnly(true)
                .secure(false)          // false en local (HTTP). true en producción (HTTPS)
                .sameSite("Lax")
                .path("/")
                .maxAge(tokenResponse.getExpiresIn())
                .build();

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)          // false en local (HTTP). true en producción (HTTPS)
                .sameSite("Lax")
                .path("/auth/refresh")  // Solo se adjunta en este endpoint
                .maxAge(30L * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        log.debug("ACCESSaccessCookie = {}", accessCookie);
        log.debug("Cookie refreshToken escrita: {}", refreshCookie);
    }

    /**
     * Vacía las cookies sobreescribiéndolas con MaxAge=0.
     * El navegador las elimina automáticamente al recibir esto.
     * Se llama en logout.
     */
    private void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie clearAccess = ResponseCookie
                .from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearRefresh = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
    }

    /**
     * Lee el valor de una cookie por nombre desde la request.
     * Devuelve null si la cookie no existe.
     */
    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}