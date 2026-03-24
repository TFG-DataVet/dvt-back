package com.datavet.shared.domain.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentId {

    private static final List<String> ALLOWED_TYPES = List.of("DNI", "NIE", "NIF", "PASAPORTE");
    private static final String CONTROL_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    private final String documentType;
    private final String documentNumber;

    public static DocumentId of(String documentType, String documentNumber) {
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

        validateFormat(normalizedType, normalizedNumber); // Bug 1 fix: método ahora es static

        return new DocumentId(normalizedType, normalizedNumber); // Bug 3 fix: usar valores normalizados
    }

    // =========================================================================
    // Validaciones — todos static porque se invocan desde el factory method
    // =========================================================================

    private static void validateFormat(String type, String number) { // Bug 1 fix
        switch (type) {
            case "DNI" -> validateDni(number);
            case "NIE" -> validateNie(number);
            case "NIF" -> validateNif(number);
        }
    }

    private static void validateDni(String number) { // Bug 1 fix
        if (!number.matches("\\d{8}[A-Z]")) {
            throw new IllegalArgumentException(
                    "El DNI debe tener 8 dígitos seguidos de una letra. Ejemplo: 12345678Z."
            );
        }
        validateControlLetter(Integer.parseInt(number.substring(0, 8)), number.charAt(8));
    }

    private static void validateNie(String number) { // Bug 1 fix
        if (!number.matches("[XYZ]\\d{7}[A-Z]")) {
            throw new IllegalArgumentException(
                    "El NIE debe comenzar con X, Y o Z, seguido de 7 dígitos y una letra. Ejemplo: X1234567L."
            );
        }

        char firstChar   = number.charAt(0);
        int  firstDigit  = switch (firstChar) { case 'X' -> 0; case 'Y' -> 1; default -> 2; };
        // Bug 2 fix: String.valueOf garantiza concatenación de strings, no suma aritmética
        int  numericPart = Integer.parseInt(String.valueOf(firstDigit) + number.substring(1, 8));

        validateControlLetter(numericPart, number.charAt(8));
    }

    private static void validateNif(String number) { // Bug 1 fix
        if (!number.matches("[A-Z]\\d{7}[A-Z0-9]")) {
            throw new IllegalArgumentException(
                    "El NIF debe comenzar con una letra, seguida de 7 dígitos y un carácter alfanumérico. Ejemplo: A1234567H."
            );
        }
    }

    private static void validateControlLetter(int numericPart, char providedLetter) { // Bug 1 fix
        char expectedLetter = CONTROL_LETTERS.charAt(numericPart % 23);
        if (providedLetter != expectedLetter) {
            throw new IllegalArgumentException(
                    "La letra de control del documento no es correcta. Se esperaba: " + expectedLetter + "."
            );
        }
    }
}