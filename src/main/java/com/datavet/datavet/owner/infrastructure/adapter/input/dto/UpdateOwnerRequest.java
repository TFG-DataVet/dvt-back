package com.datavet.datavet.owner.infrastructure.adapter.input.dto;

import com.datavet.datavet.shared.domain.valueobject.Address;
import com.datavet.datavet.shared.domain.valueobject.Email;
import com.datavet.datavet.shared.domain.valueobject.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOwnerRequest {

    @NotBlank(message = "Owner name is required")
    @Size(max = 50, message = "Owner name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Owner lastname is required")
    @Size(max = 50, message = "Owner lastname must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Owner dni must not be empty")
    @Size(max = 10, message = "Owner DNI must not exceed 10 characters")
    private String dni;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$", message = "Phone number format is invalid")
    private Phone phone;

    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private Email email;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    private String postalCode;

}
