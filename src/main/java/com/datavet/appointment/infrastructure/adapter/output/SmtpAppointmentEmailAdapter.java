package com.datavet.appointment.infrastructure.adapter.output;

import com.datavet.appointment.application.port.out.AppointmentEmailPort;
import com.datavet.appointment.domain.model.Appointment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpAppointmentEmailAdapter implements AppointmentEmailPort {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender       mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendAppointmentCreatedEmail(
            String toEmail,
            String ownerName,
            Appointment appointment) {

        try {
            Context context = new Context();
            context.setVariable("ownerName",    ownerName);
            context.setVariable("appointmentId", appointment.getId());
            context.setVariable("type",          appointment.getType().name());
            context.setVariable("scheduledAt",   appointment.getScheduledAt().format(DATE_FMT));
            context.setVariable("petName",
                    appointment.getPet() != null ? appointment.getPet().getName() : "N/A");

            String htmlContent = templateEngine.process("appointment_created_email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Cita confirmada en DataVet");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de confirmación de cita enviado a {}", toEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
