package com.datavet.owner.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerResponse {

    private String ownerId;
    private String firstName;
    private String lastName;
    private String dni;
    private String phone;
    private String email;
    @JsonProperty("address")
    private AddressDto address;

    @Getter
    @AllArgsConstructor
    public static class AddressDto {
        private String street;
        private String city;
        private String postalCode;

        @JsonProperty("fullAddress")
        public String getFullAddress() {
            StringBuilder sb = new StringBuilder(street);
            sb.append(", ").append(city);
            if (postalCode != null && !postalCode.isEmpty()) {
                sb.append(" ").append(postalCode);
            }
            return sb.toString();
        }
    }

}
