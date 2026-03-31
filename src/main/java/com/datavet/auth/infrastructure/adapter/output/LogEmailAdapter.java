package com.datavet.auth.infrastructure.adapter.output;

import com.datavet.auth.application.port.out.EmailPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Implementación MVP del EmailPort.
 * Loguea los emails por consola en lugar de enviarlos realmente.
 * Cuando se implemente SMTP, se reemplaza esta clase sin tocar nada más.
 */
@Slf4j
public class LogEmailAdapter implements EmailPort {

    @Override
    public void sendVerificationEmail(String clinicName, String nameOwnerClinic, String toEmail, String token) {
        log.info("====================================================");
        log.info("📧 EMAIL DE VERIFICACIÓN");
        log.info("Para:   {}", toEmail);
        log.info("Token:  {}", token);
        log.info("Link:   http://localhost:3000/verify-email?token={}", token);
        log.info("====================================================");
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String clinicName, String nameOwnerClinic) {
        log.info("====================================================");
        log.info("📧 EMAIL DE BIENVENIDA");
        log.info("Para:    {}", toEmail);
        log.info("Clínica: {}", clinicName);
        log.info("====================================================");
    }

    @Override
    public void sendEmployeeActivationEmail(String toEmail, String token, String clinicName, String nameEmployee) {
        log.info("====================================================");
        log.info("📧 EMAIL DE ACTIVACIÓN DE EMPLEADO");
        log.info("Para:  {}", toEmail);
        log.info("Token: {}", token);
        log.info("Link:  http://localhost:3000/activate-account?token={}", token);
        log.info("====================================================");
    }

    @Override
    public void sendWelcomeEmailToEmployee(String toEmail, String clinicName, String employeeName) {
        log.info("====================================================");
        log.info("📧 EMAIL DE BIENVENIDA DE EMPLEADO");
        log.info("Para:  {}", toEmail);
        log.info("====================================================");
    }
}