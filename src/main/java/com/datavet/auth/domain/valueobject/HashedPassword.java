package com.datavet.auth.domain.valueobject;

import com.datavet.auth.domain.exception.InvalidCredentialsException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value object que representa una contraseña hasheada con bcrypt.
 *
 * Nunca almacena la contraseña en texto plano.
 * La verificación se delega a la capa de infraestructura (BCryptPasswordEncoder)
 * a través del método matches().
 */
@Getter
@EqualsAndHashCode
public class HashedPassword {

    private final String value;

    private HashedPassword(String value) {
        this.value = value;
    }

    /**
     * Crea un HashedPassword a partir de un hash ya generado.
     * Usado al registrar un usuario — el hash lo genera AuthService
     * usando BCryptPasswordEncoder antes de llamar a este método.
     */
    public static HashedPassword ofHash(String hashedValue) {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new InvalidCredentialsException("El hash de la contraseña no puede ser nulo o vacío");
        }

        // BCrypt siempre empieza por $2a$, $2b$ o $2y$
        if (!hashedValue.startsWith("$2")) {
            throw new InvalidCredentialsException("El valor proporcionado no es un hash BCrypt válido");
        }

        return new HashedPassword(hashedValue);
    }

    /**
     * Valida que la contraseña en texto plano cumpla los requisitos mínimos
     * antes de ser hasheada. Llamado en AuthService antes de hashear.
     */
    public static void validateRawPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidCredentialsException("La contraseña no puede estar vacía");
        }

        if (rawPassword.length() < 8) {
            throw new InvalidCredentialsException("La contraseña debe tener al menos 8 caracteres");
        }

        if (rawPassword.length() > 72) {
            // BCrypt tiene un límite de 72 bytes
            throw new InvalidCredentialsException("La contraseña no puede superar 72 caracteres");
        }

        boolean hasUpperCase  = rawPassword.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase  = rawPassword.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit      = rawPassword.chars().anyMatch(Character::isDigit);

        if (!hasUpperCase || !hasLowerCase || !hasDigit) {
            throw new InvalidCredentialsException(
                    "La contraseña debe contener al menos una mayúscula, una minúscula y un número");
        }
    }

    @Override
    public String toString() {
        // Nunca exponemos el hash en logs
        return "HashedPassword{value='[PROTECTED]'}";
    }
}