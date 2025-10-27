package com.datavet.datavet.clinic.application.validation;

import com.datavet.datavet.clinic.application.port.in.command.CreateClinicCommand;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.application.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Validator for CreateClinicCommand using shared validation framework.
 * Value objects (Address, Email, Phone) handle their own validation.
 */
@Component
public class CreateClinicCommandValidator implements Validator<CreateClinicCommand> {
    
    @Override
    public ValidationResult validate(CreateClinicCommand command) {
        ValidationResult result = new ValidationResult();
        
        // Validate clinic name
        if (!StringUtils.hasText(command.getClinicName())) {
            result.addError("clinicName", "Clinic name is required");
        } else if (command.getClinicName().length() > 100) {
            result.addError("clinicName", "Clinic name must not exceed 100 characters");
        }
        
        // Validate legal name
        if (!StringUtils.hasText(command.getLegalName())) {
            result.addError("legalName", "Legal name is required");
        } else if (command.getLegalName().length() > 150) {
            result.addError("legalName", "Legal name must not exceed 150 characters");
        }
        
        // Validate legal number
        if (!StringUtils.hasText(command.getLegalNumber())) {
            result.addError("legalNumber", "Legal number is required");
        } else if (command.getLegalNumber().length() > 50) {
            result.addError("legalNumber", "Legal number must not exceed 50 characters");
        }
        
        // Validate value objects - they handle their own validation
        if (command.getAddress() == null) {
            result.addError("address", "Address is required");
        }
        
        if (command.getPhone() == null) {
            result.addError("phone", "Phone is required");
        }
        
        if (command.getEmail() == null) {
            result.addError("email", "Email is required");
        }
        
        // Validate logo URL (optional)
        if (command.getLogoUrl() != null && command.getLogoUrl().length() > 255) {
            result.addError("logoUrl", "Logo URL must not exceed 255 characters");
        }
        
        return result;
    }
}