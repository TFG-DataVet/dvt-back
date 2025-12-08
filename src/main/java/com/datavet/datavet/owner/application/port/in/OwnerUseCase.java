package com.datavet.datavet.owner.application.port.in;

import com.datavet.datavet.owner.application.port.in.command.CreateOwnerCommand;
import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.owner.domain.model.Owner;
import com.datavet.datavet.shared.application.port.UseCase;

import java.util.List;

public interface OwnerUseCase extends UseCase {
    Owner       createOwner     (CreateOwnerCommand command);
    Owner       updateOwner     (UpdateOwnerCommand command);
    void        deleteOwner     (String id);
    Owner       getOwnerById   (String id);
    List<Owner> getAllOwners();
}
