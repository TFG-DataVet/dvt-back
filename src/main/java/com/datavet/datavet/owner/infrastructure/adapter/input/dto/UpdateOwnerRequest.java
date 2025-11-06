package com.datavet.datavet.owner.infrastructure.adapter.input.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOwnerRequest {

    @NotBlank(message = "Owner name is required")
    @Size(max = 50, message = "Clinic name must not exceed 50 chararters")
    private String name;

    @NotBlank(message = "Owner lastname is required")
    @Size(max = 50, message = "Clinic lastnet must not exceed 50 chararters")
    private String lastName;

    @NotBlank(message = "Owner dni must not be empty")
    @Size(max = 10, message = "Owner DNI must not exceed 10 characters")
    private String dni;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$", message = "Phone number format is invalid")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;


    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    private String postalCode;

}
