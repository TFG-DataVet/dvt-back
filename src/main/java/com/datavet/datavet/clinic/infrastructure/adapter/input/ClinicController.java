package com.datavet.datavet.clinic.infrastructure.adapter.input;

import com.datavet.datavet.clinic.application.dto.ClinicResponse;
import com.datavet.datavet.clinic.application.mapper.ClinicMapper;
import com.datavet.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.clinic.application.port.in.command.UpdateClinicCommand;
import com.datavet.datavet.clinic.domain.model.Clinic;
import com.datavet.datavet.clinic.infrastructure.adapter.input.dto.CreateClinicRequest;
import com.datavet.datavet.clinic.infrastructure.adapter.input.dto.UpdateClinicRequest;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/clinic")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicUseCase clinicUseCase;

    @PostMapping
    public ResponseEntity<ClinicResponse> create(@Valid @RequestBody CreateClinicRequest request) {
        // Convert request to command with value objects
        CreateClinicCommand command = new CreateClinicCommand(
                request.getClinicName(),
                request.getLegalName(),
                request.getLegalNumber(),
                new Address(request.getAddress(), request.getCity(), request.getCodePostal()),
                new Phone(request.getPhone()),
                new Email(request.getEmail()),
                request.getLogoUrl()
        );
        
        Clinic clinic = clinicUseCase.createClinic(command);
        return ResponseEntity.status(201).body(ClinicMapper.toResponse(clinic));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateClinicRequest request) {
        // Convert request to command with value objects
        UpdateClinicCommand command = UpdateClinicCommand.builder()
                .clinicId(id)
                .clinicName(request.getClinicName())
                .legalName(request.getLegalName())
                .legalNumber(request.getLegalNumber())
                .address(new Address(request.getAddress(), request.getCity(), request.getCodePostal()))
                .phone(new Phone(request.getPhone()))
                .email(new Email(request.getEmail()))
                .logoUrl(request.getLogoUrl())
                .suscriptionStatus(request.getSuscriptionStatus())
                .build();
        
        Clinic updatedClinic = clinicUseCase.updateClinic(command);
        return ResponseEntity.ok(ClinicMapper.toResponse(updatedClinic));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicResponse> getById(@PathVariable Long id) {
        Clinic clinic = clinicUseCase.getClinicById(id);
        return ResponseEntity.ok(ClinicMapper.toResponse(clinic));
    }

    @GetMapping
    public ResponseEntity<List<ClinicResponse>> getAll() {
        List<Clinic> clinics = clinicUseCase.getAllClinics();
        List<ClinicResponse> responses = clinics.stream()
                .map(ClinicMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Check if clinic exists before deleting
        clinicUseCase.getClinicById(id); // This will throw ClinicNotFoundException if not found
        clinicUseCase.deleteClinic(id);
        return ResponseEntity.ok().build();
    }


}