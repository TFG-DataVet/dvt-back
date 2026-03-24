package com.datavet.shared.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class DocumentId {

    private static final List<String> ALLOWED_TYPES = List.of("DNI", "NIE", "NIF", "PASAPORTE");
    private static final String CONTROL_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    private final String documentType;
    private final String documentNumber;

    public DocumentId(String documentType, String documentNumber) {
        if (documentType == null || documentType.isBlank()) {
            throw new IllegalArgumentException("El tipo de documento no puede ser nulo o vacío.");
        }

        if (!ALLOWED_TYPES.contains(documentType.toUpperCase())) {
            throw new IllegalArgumentException(
                    "El tipo de documento no es válido. Tipos permitidos: " + ALLOWED_TYPES
            );
        }

        if (documentNumber == null || documentNumber.isBlank()) {
            throw new IllegalArgumentException("El número de documento no puede ser nulo o vacío.");
        }

        String normalizedType   = documentType.toUpperCase();
        String normalizedNumber = documentNumber.toUpperCase().trim();

        validateFormat(normalizedType, normalizedNumber);

        this.documentType   = normalizedType;
        this.documentNumber = normalizedNumber;
    }

    private void validateFormat(String type, String number) {
        switch (type) {
            case "DNI" -> validateDni(number);
            case "NIE" -> validateNie(number);
            case "NIF" -> validateNif(number);
        }
    }

    private void validateDni(String number) {
        if (!number.matches("\\d{8}[A-Z]")) {
            throw new IllegalArgumentException(
                    "El DNI debe tener 8 dígitos seguidos de una letra. Ejemplo: 12345678Z."
            );
        }
        validateControlLetter(Integer.parseInt(number.substring(0, 8)), number.charAt(8));
    }

    private void validateNie(String number) {
        if (!number.matches("[XYZ]\\d{7}[A-Z]")) {
            throw new IllegalArgumentException(
                    "El NIE debe comenzar con X, Y o Z, seguido de 7 dígitos y una letra. Ejemplo: X1234567L."
            );
        }

        char firstChar   = number.charAt(0);
        int  firstDigit  = switch (firstChar) { case 'X' -> 0; case 'Y' -> 1; default -> 2; };
        int  numericPart = Integer.parseInt(firstDigit + number.substring(1, 8));

        validateControlLetter(numericPart, number.charAt(8));
    }

    private void validateNif(String number) {
        if (!number.matches("[A-Z]\\d{7}[A-Z0-9]")) {
            throw new IllegalArgumentException(
                    "El NIF debe comenzar con una letra, seguida de 7 dígitos y un carácter alfanumérico. Ejemplo: A1234567H."
            );
        }
    }

    private void validateControlLetter(int numericPart, char providedLetter) {
        char expectedLetter = CONTROL_LETTERS.charAt(numericPart % 23);
        if (providedLetter != expectedLetter) {
            throw new IllegalArgumentException(
                    "La letra de control del documento no es correcta. Se esperaba: " + expectedLetter + "."
            );
        }
    }
}