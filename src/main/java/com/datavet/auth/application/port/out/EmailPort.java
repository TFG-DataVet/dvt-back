package com.datavet.auth.application.port.out;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Puerto de salida para el envío de emails.
 * La implementación real usará SMTP.
 * La implementación de MVP logeará el email por consola.
 */
public interface EmailPort {

    /**
     * Envía el email de verificación al usuario recién registrado.
     *
     * @param toEmail   email del destinatario
     * @param token     token de verificación
     */
    void sendVerificationEmail(String clinicName, String nameOwnerClinic, String toEmail, String token);

    /**
     * Envía el email de bienvenida una vez completado el setup.
     *
     * @param toEmail       email del destinatario
     * @param clinicName    nombre de la clínica
     */
    void sendWelcomeEmail(String toEmail, String clinicName, String nameOwnerClinic);

    void sendEmployeeActivationEmail(String toEmail, String token, String clinicName, String nameEmployee);

    void sendWelcomeEmailToEmployee(String toEmail, String clinicName, String employeeName);
}