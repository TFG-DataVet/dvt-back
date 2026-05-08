package com.datavet.appointment.infrastructure.adapter.input;

import com.datavet.appointment.application.dto.AppointmentResponse;
import com.datavet.appointment.application.mapper.AppointmentMapper;
import com.datavet.appointment.application.port.in.AppointmentUseCase;
import com.datavet.appointment.application.port.in.command.CancelAppointmentCommand;
import com.datavet.appointment.application.port.in.command.CreateAppointmentCommand;
import com.datavet.appointment.application.port.in.command.UpdateAppointmentStatusCommand;
import com.datavet.appointment.domain.model.Appointment;
import com.datavet.appointment.domain.valueobject.AppointmentSource;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.appointment.infrastructure.adapter.input.dto.CancelAppointmentRequest;
import com.datavet.appointment.infrastructure.adapter.input.dto.CreateAppointmentRequest;
import com.datavet.appointment.infrastructure.adapter.input.dto.UpdateAppointmentStatusRequest;
import com.datavet.auth.domain.model.UserRole;
import com.datavet.auth.infrastructure.security.AuthenticatedUser;
import com.datavet.employee.application.port.in.EmployeeUseCase;
import com.datavet.employee.domain.model.Employee;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private static final List<String> BLOCKED_SPECIALITIES = List.of("CLEANING", "MAINTENANCE");

    private final AppointmentUseCase appointmentUseCase;
    private final EmployeeUseCase    employeeUseCase;

    // =========================================================================
    // POST /appointments — crear cita
    // =========================================================================

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody CreateAppointmentRequest request) {

        checkAgendaAccess(currentUser);

        AppointmentSource source = request.getSource() != null
                ? request.getSource()
                : AppointmentSource.PANEL;

        CreateAppointmentCommand command = CreateAppointmentCommand.builder()
                .clinicId(currentUser.getClinicId())
                .emergency(request.isEmergency())
                .type(request.getType())
                .scheduledAt(request.getScheduledAt())
                .ownerId(request.getOwnerId())
                .ownerName(request.getOwnerName())
                .ownerEmail(request.getOwnerEmail())
                .ownerPhone(request.getOwnerPhone())
                .petId(request.getPetId())
                .petName(request.getPetName())
                .petSpecies(request.getPetSpecies())
                .creationEmployeeId(request.getCreationEmployeeId())
                .medicalEmployeeId(request.getMedicalEmployeeId())
                .notes(request.getNotes())
                .productIds(request.getProductIds())
                .source(source)
                .build();

        Appointment appointment = appointmentUseCase.create(command);
        return ResponseEntity.status(201).body(AppointmentMapper.toResponse(appointment));
    }

    // =========================================================================
    // PATCH /appointments/{id}/status — avanzar estado
    // =========================================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {

        checkAgendaAccess(currentUser);

        UpdateAppointmentStatusCommand command = UpdateAppointmentStatusCommand.builder()
                .appointmentId(id)
                .clinicId(currentUser.getClinicId())
                .newStatus(request.getNewStatus())
                .medicalEmployeeId(request.getMedicalEmployeeId())
                .build();

        Appointment appointment = appointmentUseCase.updateStatus(command);
        return ResponseEntity.ok(AppointmentMapper.toResponse(appointment));
    }

    // =========================================================================
    // DELETE /appointments/{id} — cancelar cita (soft cancel)
    // =========================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody(required = false) CancelAppointmentRequest request) {

        checkAgendaAccess(currentUser);

        String reason = request != null ? request.getReason() : null;

        CancelAppointmentCommand command = CancelAppointmentCommand.builder()
                .appointmentId(id)
                .clinicId(currentUser.getClinicId())
                .reason(reason)
                .build();

        appointmentUseCase.cancel(command);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // GET /appointments/{id} — obtener por ID
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        checkAgendaAccess(currentUser);

        Appointment appointment = appointmentUseCase.getById(id, currentUser.getClinicId());
        return ResponseEntity.ok(AppointmentMapper.toResponse(appointment));
    }

    // =========================================================================
    // GET /appointments — listar con filtros opcionales
    // =========================================================================

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) AppointmentType   type,
            @RequestParam(required = false) String            ownerId) {

        checkAgendaAccess(currentUser);

        List<Appointment> appointments = appointmentUseCase.getByClinic(
                currentUser.getClinicId(), date, status, type, ownerId);

        return ResponseEntity.ok(AppointmentMapper.toResponseList(appointments));
    }

    // =========================================================================
    // Acceso a agenda — empleados con especialidad CLEANING o MAINTENANCE bloqueados
    // =========================================================================

    private void checkAgendaAccess(AuthenticatedUser user) {
        if (user.getRole() != UserRole.CLINIC_STAFF) {
            return;
        }

        Employee employee = employeeUseCase.getEmployeeById(
                user.getEmployeeId(), user.getClinicId());

        String speciality = employee.getSpeciality();
        if (speciality != null && BLOCKED_SPECIALITIES.contains(speciality.toUpperCase())) {
            throw new AccessDeniedException("No tienes acceso a la agenda de citas");
        }
    }
}
