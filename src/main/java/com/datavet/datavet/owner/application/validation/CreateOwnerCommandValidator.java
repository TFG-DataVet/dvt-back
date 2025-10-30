package com.datavet.datavet.owner.application.validation;

import com.datavet.datavet.owner.application.port.command.CreateOwnerCommand;
import com.datavet.datavet.shared.application.validation.Validator;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CreateOwnerCommandValidator implements Validator<CreateOwnerCommand> {

    @Override
    public ValidationResult validate(CreateOwnerCommand command) {
        ValidationResult result = new ValidationResult();

        // Validate owner name
        if (!StringUtils.hasText(command.getOwnerName())) {
            result.addError("ownerName", "Owner name cannot be empty");
        } else if (command.getOwnerName().length() > 100) {
            result.addError("ownerName", "Owner name cannot be longer than 100 characters");
        }

        // Validate owner lastname
        if (!StringUtils.hasText(command.getOwnerLastName())) {
            result.addError("ownerLastName", "Owner last name cannot be empty");
        } else if (command.getOwnerLastName().length() > 100) {
            result.addError("ownerLastName", "Owner last name cannot be longer than 100 characters");
        }

        // Validate owner dni
        if (!StringUtils.hasText(command.getOwnerDni())) {
            result.addError("ownerDni", "Owner dni cannot be empty");
        } else if (command.getOwnerDni().length() > 100) {
            result.addError("ownerDni", "Owner dni cannot be longer than 100 characters");
        }

        if (command.getOwnerPhone() == null) {
            result.addError("ownerPhone", "Owner phone cannot be empty");
        }

        if(command.getOwnerEmail() == null) {
            result.addError("ownerEmail", "Owner email cannot be empty");
        }

        return result;

    }
}
