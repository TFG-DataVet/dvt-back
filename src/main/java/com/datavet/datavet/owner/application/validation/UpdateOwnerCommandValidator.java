package com.datavet.datavet.owner.application.validation;

import com.datavet.datavet.owner.application.port.in.command.UpdateOwnerCommand;
import com.datavet.datavet.shared.application.validation.Validator;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import org.springframework.util.StringUtils;

import org.springframework.stereotype.Component;

@Component
public class UpdateOwnerCommandValidator implements Validator<UpdateOwnerCommand> {

    @Override
    public ValidationResult validate(UpdateOwnerCommand command) {
        ValidationResult result = new ValidationResult();

        if (command.getOwnerID() == null) {
            result.addError("ownerID", "Owner ID is required");
        } else if (command.getOwnerID() <= 0) {
            result.addError("ownerID", "Owner ID must be greater than zero");
        }

        if (!StringUtils.hasText(command.getOwnerName())) {
            result.addError("ownerName", "Owner name is required");
        } else if (command.getOwnerName().length() > 50) {
            result.addError("ownerName", "Owner name must not exceed 50 characters");
        }

        if (!StringUtils.hasText(command.getOwnerLastName())) {
            result.addError("ownerLastName", "Owner last name is required");
        } else if (command.getOwnerLastName().length() > 50) {
            result.addError("ownerLastName", "Owner last name must not exceed 50 characters");
        }

        // Validate value objects - they handle their own validation
        if (command.getOwnerAddress() == null) {
            result.addError("address", "Address is required");
        }

        if (command.getOwnerPhone() == null) {
            result.addError("phone", "Phone is required");
        }

        if (command.getOwnerEmail() == null) {
            result.addError("email", "Email is required");
        }

        return result;
    }
}
