package com.datavet.auth.infrastructure.adapter.output;

import com.datavet.auth.application.port.out.EmailPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendVerificationEmail(String clinicName, String nameOwnerClinic, String toEmail, String token) {
        try{
            Context context = new Context();
            String urlVerification = "http://localhost:3000/verify-email?token=" + token;
            context.setVariable("clinicName", clinicName);
            context.setVariable("name", nameOwnerClinic);
            context.setVariable("url", urlVerification);

            String htmlContent = templateEngine.process("verification_email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Verifica tu cuenta en DataVet");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email HTML de verificación enviado a {}", toEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String clinicName, String nameOwnerClinic) {
        try{
            Context context = new Context();
            context.setVariable("clinicName", clinicName);
            context.setVariable("name", nameOwnerClinic);

            String htmlContent = templateEngine.process("welcome_email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Todo listo, " + nameOwnerClinic + " — Bienvenido a DataVet");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("📧 Email de bienvenida enviado a {}", toEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmployeeActivationEmail(String toEmail, String token, String clinicName, String nameEmployee) {
        try{
            Context context = new Context();
            String url = "http://localhost:3000/activate-account?token=" + token;
            context.setVariable("url", url);
            context.setVariable("nameClinic", clinicName);
            context.setVariable("nameEmployee", nameEmployee);

            String htmlContent = templateEngine.process("new_employee", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Bienvenido a " + clinicName + " — Activa tu cuenta");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("📧 Email de activación enviado a {}", toEmail);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void sendWelcomeEmailToEmployee(String toEmail, String clinicName, String employeeName) {
        try {
            Context context = new Context();
            context.setVariable("clinicName", clinicName);
            context.setVariable("employeeName", employeeName);

            String htmlContent = templateEngine.process("welcome_employee", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Bienvenido a " + clinicName +", " + employeeName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("📧 Email de bienvenida enviado a {}", toEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("Activa tu cuenta en Datavet");
//        message.setText(
//                "Hola,\n\n" +
//                        "Tu cuenta en Datavet ha sido creada.\n\n" +
//                        "Para activarla y establecer tu contraseña haz clic aquí:\n\n" +
//                        "http://localhost:3000/activate-account?token=" + token + "\n\n" +
//                        "Este enlace expira en 24 horas.\n\n" +
//                        "El equipo de Datavet"
//        );
//        mailSender.send(message);