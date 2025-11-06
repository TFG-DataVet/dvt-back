package com.datavet.datavet.owner.infrastructure.adapter.input;

import com.datavet.datavet.clinic.application.port.in.ClinicUseCase;
import com.datavet.datavet.owner.application.dto.OwnerResponse;
import com.datavet.datavet.owner.application.mapper.OwnerMapper;
import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.OwnerUseCase;
import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.owner.infrastructure.adapter.input.dto.CreateOwnerRequest;
import com.datavet.datavet.owner.infrastructure.adapter.input.dto.UpdateOwnerRequest;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerUseCase ownerUseCase;

    @PostMapping
    public ResponseEntity<OwnerResponse> create(@Valid @RequestBody CreateOwnerRequest request) {
         CreateOwnerCommand  command =  new CreateOwnerCommand(
                request.getName(),
                request.getLastName(),
                request.getDni(),
                new Phone(request.getPhone()),
                new Email(request.getEmail()),
                new Address(request.getAddress(), request.getCity(), request.getPostalCode())
        );

        Owner owner = ownerUseCase.createOwner(command);
        return ResponseEntity.status(201).body(OwnerMapper.toResponse(owner));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getById(@PathVariable Long id) {
        Owner owner = ownerUseCase.getOwnerById(id);
        return ResponseEntity.status(200).body(OwnerMapper.toResponse(owner));
    }

    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAll() {
        List<Owner> owners = ownerUseCase.getAllOwners();
        List<OwnerResponse> responses = owners.stream()
                .map(OwnerMapper::toResponse)
                .toList();
        return ResponseEntity.status(200).body(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateOwnerRequest request) {
        UpdateOwnerCommand command = UpdateOwnerCommand.builder()
                .ownerID(id)
                .ownerName(request.getName())
                .ownerLastName(request.getLastName())
                .ownerDni(request.getDni())
                .ownerPhone(new Phone(request.getPhone()))
                .ownerEmail(new Email(request.getEmail()))
                .ownerAddress(new Address(request.getAddress(), request.getCity(), request.getPostalCode()))
                .build();

        Owner owner = ownerUseCase.updateOwner(command);
        return ResponseEntity.status(200).body(OwnerMapper.toResponse(owner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ownerUseCase.getOwnerById(id);
        ownerUseCase.deleteOwner(id);
        return ResponseEntity.status(204).build();
    }
}
