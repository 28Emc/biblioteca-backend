package com.biblioteca.backend.service;

import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender sender;

    @Async
    public void enviarEmail(Map<String, Object> model) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String cabecera = "<!DOCTYPE html><html style='height: 100%; margin: 0;'>"
                    + "<head><meta charset='UTF-8'/><meta name='viewport' content='width=device-width, initial-scale=1.0'/></head>";
            String cuerpo = "";
            String pie = "</div><div style='height: 100px;'></div></div><div style='background-color: grey;color: mintcream;text-align: center;font-weight: lighter;font-size: 1.3rem;font-family: Segoe UI, Tahoma, Geneva, Verdana, sans-serif;height: 100px;line-height: 100px;'>"
                    + "<p>Biblioteca &copy;2020</p></div></body></html>";
            switch (model.get("titulo").toString()) {
                case "Validar Correo":
                    cuerpo = "<body style='height: 100%; margin: 0;'><div style='min-height: 100%; margin-bottom: -100px;'>"
                            + "<h1 style='text-align: center;font-weight: lighter;padding-top: 1rem;padding-bottom: 1rem;font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;font-size: 2.5em;'>"
                            + model.get("titulo").toString()
                            + "</h1><div style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='validar-email' width='75%'/>"
                            + "</div><div style='padding-left: 1.5rem;padding-right: 1.5rem;padding-top: 1rem;padding-bottom: 1rem;font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;font-size: 1.2rem;'>"
                            + "<h3 style='text-justify: auto; font-weight: lighter;'>Saludos, hemos recibido tu peticiòn de registro a Biblioteca2020. Para confirmar tu cuenta, hacer click en el siguiente enlace: </h3>"
                            + "<br/><a style='text-justify: auto; font-weight: lighter;' href='"
                            + model.get("enlace").toString() + "'>" + model.get("enlace").toString() + "</a><br/><br/>"
                            + "<h3 style='text-justify: auto; font-weight: lighter;'>Si usted no es el destinatario a quien se dirige el presente correo, favor de contactar al remitente respondiendo al presente correo y eliminar el correo original incluyendo sus archivos, así como cualquier copia del mismo.</h3>";
                    break;

                case "Recuperar Password":
                    cuerpo = "<body style='height: 100%; margin: 0;'><div style='min-height: 100%; margin-bottom: -100px;'>"
                            + "<h1 style='text-align: center;font-weight: lighter;padding-top: 1rem;padding-bottom: 1rem;font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;font-size: 2.5em;'>"
                            + model.get("titulo").toString()
                            + "</h1><div style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='recuperar-contrasena' width='75%'/>"
                            + "</div><div style='padding-left: 1.5rem;padding-right: 1.5rem;padding-top: 1rem;padding-bottom: 1rem;font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;font-size: 1.2rem;'>"
                            + "<h3 style='text-justify: auto; font-weight: lighter;'>Saludos, hemos recibido tu peticiòn de recuperación de contraseña. Para restablecer tu contraseña, hacer click en el siguiente enlace: </h3>"
                            + "<br/><a style='text-justify: auto; font-weight: lighter;' href='"
                            + model.get("enlace").toString() + "'>" + model.get("enlace").toString() + "</a><br/><br/>"
                            + "<h3 style='text-justify: auto; font-weight: lighter;'>Si usted no es el destinatario a quien se dirige el presente correo, favor de contactar al remitente respondiendo al presente correo y eliminar el correo original incluyendo sus archivos, así como cualquier copia del mismo.</h3>";
                    break;
                case "Contraseña Actualizada":
                    cuerpo = "<body style='height: 100%; margin: 0;'><div style='min-height: 100%; margin-bottom: -100px;'>"
                            + "<h1 style='text-align: center;font-weight: lighter;padding-top: 1rem;padding-bottom: 1rem;font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;font-size: 2.5em;'>"
                            + model.get("titulo").toString()
                            + "</h1><div style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='actualizar-contrasena' width='75%'/>"
                            + "</div><div style='padding-left: 1.5rem;padding-right: 1.5rem;padding-top: 1rem;padding-bottom: 1rem;font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;font-size: 1.2rem;'>"
                            + "<h3 style='text-justify: auto; font-weight: lighter;'>Saludos " + model.get("usuario")
                            + ", recientemente ha actualizado su contraseña. Si usted no ha realizado esta solicitud, por favor notificarlo al administrador de Biblioteca respondiendo al siguiente mensaje.<br/><br/>"
                            + "<h3 style='text-justify: auto; font-weight: lighter;'>Si usted no es el destinatario a quien se dirige el presente correo, favor de contactar al remitente respondiendo al presente correo y eliminar el correo original incluyendo sus archivos, así como cualquier copia del mismo.</h3>";
                    break;
            }
            helper.setText(cabecera + cuerpo + pie, true);
            helper.setFrom(model.get("from").toString());
            helper.setTo(model.get("to").toString());
            helper.setSubject(model.get("subject").toString());
            helper.addInline("logo-biblioteca2020", new ClassPathResource("static/img/validar-email-token.png"));
            sender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}