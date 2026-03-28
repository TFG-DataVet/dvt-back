package com.datavet.auth.infrastructure.adapter.output;

import com.datavet.auth.application.port.out.EmailPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementación MVP del EmailPort.
 * Loguea los emails por consola en lugar de enviarlos realmente.
 * Cuando se implemente SMTP, se reemplaza esta clase sin tocar nada más.
 */
@Slf4j
@Component
public class LogEmailAdapter implements EmailPort {

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        log.info("====================================================");
        log.info("📧 EMAIL DE VERIFICACIÓN");
        log.info("Para:   {}", toEmail);
        log.info("Token:  {}", token);
        log.info("Link:   http://localhost:3000/verify-email?token={}", token);
        log.info("====================================================");
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String clinicName) {
        log.info("====================================================");
        log.info("📧 EMAIL DE BIENVENIDA");
        log.info("Para:    {}", toEmail);
        log.info("Clínica: {}", clinicName);
        log.info("====================================================");
    }

    @Override
    public void sendEmployeeActivationEmail(String toEmail, String token) {
        log.info("====================================================");
        log.info("📧 EMAIL DE ACTIVACIÓN DE EMPLEADO");
        log.info("Para:  {}", toEmail);
        log.info("Token: {}", token);
        log.info("Link:  http://localhost:3000/activate-account?token={}", token);
        log.info("====================================================");
    }
}