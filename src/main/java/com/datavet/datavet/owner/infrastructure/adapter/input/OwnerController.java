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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
