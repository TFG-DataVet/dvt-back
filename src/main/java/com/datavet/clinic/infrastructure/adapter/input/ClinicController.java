package com.datavet.clinic.infrastructure.adapter.input;

import com.datavet.auth.application.dto.TokenResponse;
import com.datavet.auth.application.port.in.AuthUseCase;
import com.datavet.auth.infrastructure.security.AuthenticatedUser;
import com.datavet.clinic.application.dto.ClinicResponse;
import com.datavet.clinic.application.mapper.ClinicMapper;
import com.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.clinic.application.port.in.command.CompleteClinicSetupCommand;
import com.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.clinic.application.port.in.command.CreatePendingClinicCommand;
import com.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.clinic.infrastructure.adapter.input.dto.*;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.List;

@RestController
@RequestMapping("/clinic")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicUseCase clinicUseCase;
    private final AuthUseCase authUseCase;

    /**
     * Paso 1 del onboarding — crea la clínica en estado PENDING_SETUP.
     * Endpoint público, no requiere autenticación.
     */
    @PostMapping("/register")
    public ResponseEntity<ClinicResponse> register(
            @Valid @RequestBody CreatePendingClinicRequest request) {

        CreatePendingClinicCommand command = CreatePendingClinicCommand.builder()
                .clinicName(request.getClinicName())
                .build();

        Clinic clinic = clinicUseCase.createPendingClinic(command);
        return ResponseEntity.status(201).body(ClinicMapper.toResponse(clinic));
    }

    /**
     * Paso 3 del onboarding — completa los datos de la clínica,
     * crea el Employee del CLINIC_OWNER y devuelve JWT definitivo.
     * Requiere JWT temporal con scope ONBOARDING_ONLY.
     */
    @PatchMapping("/{id}/complete-setup")
    public ResponseEntity<TokenResponse> completeSetup(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody CompleteClinicSetupRequest request,
            HttpServletResponse httpResponse) {

        if (!currentUser.hasOnboardingAccess()) {
            throw new AccessDeniedException("Access denied");
        }
        if (!id.equals(currentUser.getClinicId())) {
            throw new AccessDeniedException("Access denied");
        }

        CompleteClinicSetupCommand command = CompleteClinicSetupCommand.builder()
                .clinicId(id)
                .userId(currentUser.getUserId())
                .legalName(request.getLegalName())
                .legalNumber(request.getLegalNumber())
                .legalType(request.getLegalType())
                .address(new Address(
                        request.getAddress(),
                        request.getCity(),
                        request.getCodePostal()))
                .phone(new Phone(request.getPhone()))
                .email(new Email(request.getEmail()))
                .logoUrl(request.getLogoUrl())
                .schedule(ClinicSchedule.of(
                        request.getScheduleOpenDays(),
                        request.getScheduleOpenTime(),
                        request.getScheduleCloseTime(),
                        request.getScheduleNotes()))
                .ownerDocumentNumber(DocumentId.of(
                        request.getOwnerDocumentType(),
                        request.getOwnerDocumentNumber()))
                .ownerAddress(new Address(
                        request.getOwnerAddress(),
                        request.getOwnerCity(),
                        request.getOwnerPostalCode()))
                .ownerAvatarUrl(request.getOwnerAvatarUrl())
                .build();

        TokenResponse tokenResponse = authUseCase.completeOnboarding(command);

        // Escribimos el JWT definitivo como cookie HttpOnly
        // A partir de aquí el usuario tiene sesión completa
        writeAuthCookies(httpResponse, tokenResponse);

        // Devolvemos solo el userInfo en el body, sin tokens
        return ResponseEntity.ok(tokenResponse.withoutTokens());
    }

    /**
     * Crea una clínica completamente configurada.
     * Solo SUPER_ADMIN.
     */
    @PostMapping
    public ResponseEntity<ClinicResponse> create(
            @Valid @RequestBody CreateClinicRequest request) {

        CreateClinicCommand command = CreateClinicCommand.builder()
                .clinicName(request.getClinicName())
                .legalName(request.getLegalName())
                .legalNumber(request.getLegalNumber())
                .legalType(request.getLegalType())
                .address(new Address(request.getAddress(), request.getCity(), request.getCodePostal()))
                .phone(new Phone(request.getPhone()))
                .email(new Email(request.getEmail()))
                .logoUrl(request.getLogoUrl())
                .schedule(ClinicSchedule.of(
                        request.getScheduleOpenDays(),
                        request.getScheduleOpenTime(),
                        request.getScheduleCloseTime(),
                        request.getScheduleNotes()))
                .build();

        Clinic clinic = clinicUseCase.createClinic(command);
        return ResponseEntity.status(201).body(ClinicMapper.toResponse(clinic));
    }

    /**
     * Actualiza los datos de una clínica activa.
     * CLINIC_OWNER y SUPER_ADMIN.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClinicResponse> update(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody UpdateClinicRequest request) {

        if (!currentUser.isSuperAdmin() && !id.equals(currentUser.getClinicId())) {
            throw new AccessDeniedException("Access denied");
        }

        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId(id)
                .clinicName(request.getClinicName())
                .legalName(request.getLegalName())
                .legalNumber(request.getLegalNumber())
                .legalType(request.getLegalType())
                .address(new Address(request.getAddress(), request.getCity(), request.getCodePostal()))
                .phone(new Phone(request.getPhone()))
                .email(new Email(request.getEmail()))
                .logoUrl(request.getLogoUrl())
                .schedule(ClinicSchedule.of(
                        request.getScheduleOpenDays(),
                        request.getScheduleOpenTime(),
                        request.getScheduleCloseTime(),
                        request.getScheduleNotes()))
                .build();

        Clinic updatedClinic = clinicUseCase.updateClinic(command);
        return ResponseEntity.ok(ClinicMapper.toResponse(updatedClinic));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(ClinicMapper.toResponse(clinicUseCase.getClinicById(id)));
    }

    @GetMapping
    public ResponseEntity<List<ClinicResponse>> getAll() {
        List<ClinicResponse> responses = clinicUseCase.getAllClinics()
                .stream()
                .map(ClinicMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * Soft-delete — desactiva la clínica conservando todos sus datos.
     * CLINIC_OWNER y SUPER_ADMIN.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody DeactivateClinicRequest request) {

        if (!currentUser.isSuperAdmin() && !id.equals(currentUser.getClinicId())) {
            throw new AccessDeniedException("Access denied");
        }
        clinicUseCase.deactivateClinic(id, request.getReason());
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
// Helpers privados — exactamente los mismos que en AuthController
// -------------------------------------------------------------------------

    private void writeAuthCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie accessCookie = ResponseCookie
                .from("accessToken", tokenResponse.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(tokenResponse.getExpiresIn())
                .build();

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/auth/refresh")
                .maxAge(30L * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        System.out.println("Cookie acessToken escrito: " + accessCookie);
        System.out.println("Cookie refreshToken escrita: {}" + refreshCookie);
    }
}