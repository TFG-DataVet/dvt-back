package com.datavet.datavet.pet.domain.model;

import com.datavet.datavet.pet.domain.exception.PetValidationException;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OwnerInfo {

    String ownerId;
    String name;
    String lastName;
    Phone phone;

    public void validate(){
        ValidationResult result = new ValidationResult();

        if (name == null || name.isBlank()) {
            result.addError("[ownerInfo - name]" , "El nombre del dueño no puede estar vacio o nulo.");
        }

        if (lastName == null || lastName.isBlank()) {
            result.addError("[ownerInfo - lastName]", "el apellido del dueño no puede estar vacio o nulo.");
        }

        if (phone == null) {
            result.addError("[ownerInfo - Phone]", "El numero de telefono del dueño no puede ser nulo.");
        }

        if (result.hasErrors()) {
            throw new PetValidationException(result);
        }
    }

    public static OwnerInfo from(String name, String lastName, Phone phone) {
        String uuid = UUID.randomUUID().toString();

        OwnerInfo ownerInfo = new OwnerInfo(uuid, name, lastName, phone);

        ownerInfo.validate();

        return  ownerInfo;
    }

    public String getFullName() {
        return name + " " + lastName;
    }
}
