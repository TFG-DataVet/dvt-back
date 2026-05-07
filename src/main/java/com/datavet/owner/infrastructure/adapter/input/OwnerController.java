package com.datavet.owner.infrastructure.adapter.input;

import com.datavet.auth.infrastructure.security.AuthenticatedUser;
import com.datavet.owner.application.dto.OwnerResponse;
import com.datavet.owner.application.mapper.OwnerMapper;
import com.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.owner.application.port.in.OwnerUseCase;
import com.datavet.owner.domain.model.Owner;
import com.datavet.owner.infrastructure.adapter.input.dto.CreateOwnerRequest;
import com.datavet.owner.infrastructure.adapter.input.dto.UpdateOwnerRequest;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerUseCase ownerUseCase;

    @PostMapping
    public ResponseEntity<OwnerResponse> create(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody CreateOwnerRequest request) {
        CreateOwnerCommand command = CreateOwnerCommand.builder()
                .clinicId(currentUser.getClinicId())
                .ownerName(request.getName())
                .ownerLastName(request.getLastName())
                .ownerDni(DocumentId.of(request.getDocumentId(), request.getDocumentNumber()))
                .ownerPhone(new Phone(request.getPhone()))
                .ownerEmail(new Email(request.getEmail()))
                .ownerAddress(new Address(request.getAddress(), request.getCity(), request.getPostalCode()))
                .url(request.getUrl())
                .acceptTermsAndCond(request.isAcceptTermsAndCond())
                .build();

        Owner owner = ownerUseCase.createOwner(command);
        return ResponseEntity.status(201).body(OwnerMapper.toResponse(owner));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getOwner(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Owner owner = ownerUseCase.getOwnerById(id, currentUser.getClinicId());
        return ResponseEntity.ok(OwnerMapper.toResponse(owner));
    }

    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAllOwners(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<OwnerResponse> responses = ownerUseCase.getOwnersByClinic(currentUser.getClinicId())
                .stream()
                .map(OwnerMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponse> updateOwner(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody UpdateOwnerRequest request) {

        UpdateOwnerCommand command = UpdateOwnerCommand.builder()
                .ownerID(id)
                .clinicId(currentUser.getClinicId())
                .ownerName(request.getName())
                .ownerLastName(request.getLastName())
                .ownerDni(DocumentId.of(request.getDocumentId(), request.getDocumentNumber()))
                .ownerPhone(new Phone(request.getPhone()))
                .ownerEmail(new Email(request.getEmail()))
                .ownerAddress(new Address(request.getAddress(), request.getCity(), request.getPostalCode()))
                .build();

        Owner owner = ownerUseCase.updateOwner(command);
        return ResponseEntity.ok(OwnerMapper.toResponse(owner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        ownerUseCase.deleteOwner(id, currentUser.getClinicId());
        return ResponseEntity.noContent().build();
    }

}
