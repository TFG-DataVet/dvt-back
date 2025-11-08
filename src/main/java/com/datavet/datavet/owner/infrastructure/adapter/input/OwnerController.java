package com.datavet.datavet.owner.infrastructure.adapter.input;

import com.datavet.datavet.owner.application.dto.OwnerResponse;
import com.datavet.datavet.owner.application.mapper.OwnerMapper;
import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.OwnerUseCase;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.owner.infrastructure.adapter.input.dto.CreateOwnerRequest;
import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
}
