package com.datavet.clinic.application.dto;

import com.datavet.clinic.domain.model.ClinicStatus;
import com.datavet.clinic.domain.model.LegalType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ClinicResponse {

    private String       clinicId;
    private String       clinicName;
    private String       legalName;
    private String       legalNumber;
    private LegalType    legalType;
    private AddressDto   address;
    private String       phone;
    private String       email;
    private String       logoUrl;
    private ScheduleDto  schedule;
    private ClinicStatus status;

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

    @Getter
    @AllArgsConstructor
    public static class ScheduleDto {
        private String    openDays;
        private LocalTime openTime;
        private LocalTime closeTime;
        private String    notes;
    }
}