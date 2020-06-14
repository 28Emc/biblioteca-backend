package com.biblioteca.backend.service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import com.biblioteca.backend.model.Libro;
import com.biblioteca.backend.model.Prestamo;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.view.pdf.GenerarReportePDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private IPrestamoService prestamoService;

    @Value("${spring.mail.stocklibro}")
    private Integer stock;

    @Async
    public void enviarEmail(Map<String, Object> model) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        Prestamo prestamo = null;
        String path = "";
        String cabecera = "<!DOCTYPE html><html><head><meta charset='UTF-8'/><meta name='viewport' content='width=device-width, initial-scale=1.0'/><style> th { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: .5em; text-align: right; } td { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: .5em; padding-left: 2em; text-align: justify; } </style></head>";
        String cuerpo = "";
        String pie = "<div id='footer' style='padding-top: 2em; text-align: center; font-weight: lighter; font-size: 1.1rem;'><p>Biblioteca &copy;2020</p></div></div></body></html>";
        String fechaDespacho = "";
        String fechaDevolucion = "";
        switch (model.get("titulo").toString()) {
            case "Validar Correo":
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString()
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='validar-email' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter;'>Saludos, hemos recibido tu peticiòn de registro a Biblioteca2020.</h3><h3 style='font-weight: lighter; padding-bottom: 1em;'>Para confirmar tu cuenta, hacer click aquí</h3><br /><a style='text-decoration: none; background-color: #4caf50; color: white; padding: 15px 32px; text-decoration: none; font-size: 17px; border-radius: 1.5em; box-shadow: 0.1px 0.1px 0.1px dimgrey;' href="
                        + model.get("enlace").toString() + ">Validar email</a><br /><br /></div>";
                path = "static/img/validar-email.png";
                break;
            case "Recuperar Password":
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString()
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='restablecer-contrasena' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter;'>Saludos, hemos recibido tu peticiòn de recuperación de contraseña.</h3><h3 style='font-weight: lighter; padding-bottom: 1em;'>Para restablecer tu contraseña, hacer click aquí</h3><br /><a style='text-decoration: none; background-color: #eac100; color: white; padding: 15px 32px; text-decoration: none; font-size: 17px; border-radius: 1.5em; box-shadow: 0.1px 0.1px 0.1px dimgrey;' href="
                        + model.get("enlace").toString() + ">Restablecer contraseña</a><br /><br /></div>";
                path = "static/img/recuperacion-password.png";
                break;
            case "Contraseña Actualizada":
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString()
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='actualizar-contrasena' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter;'>Saludos "
                        + model.get("usuario") + ", recientemente ha actualizado su contraseña.</h3></div><br/ ><br/ >";
                path = "static/img/confirmar-recuperacion-password.png";
                break;
            case "Usuario Deshabilitado":
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString()
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='deshabilitar-usuario' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter;'>Saludos "
                        + model.get("usuario") + ", recientemente ha deshabilitado su cuenta.</h3></div><br/ ><br/ >";
                path = "static/img/confirmar-usuario-deshabilitado.png";
                break;
            case "Reactivacion Cuenta":
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString()
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='reactivar-cuenta' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter;'>Saludos "
                        + model.get("usuario")
                        + ", hemos recibido tu peticiòn de reactivación de tu cuenta de usuario de Biblioteca2020.</h3><h3 style='font-weight: lighter; padding-bottom: 1em;'>Para reactivar tu cuenta, hacer click aquí</h3><br /><a style='text-decoration: none; background-color: #3d5af1; color: white; padding: 15px 32px; text-decoration: none; font-size: 17px; border-radius: 1.5em; box-shadow: 0.1px 0.1px 0.1px dimgrey;' href="
                        + model.get("enlace").toString() + ">Habilitar cuenta</a><br /><br /></div>";
                path = "static/img/reactivar-usuario.png";
                break;
            case "Libro Solicitado":
                prestamo = (Prestamo) model.get("prestamo");
                fechaDespacho = prestamoService.mostrarFechaAmigable(prestamo.getFechaDespacho());
                fechaDevolucion = prestamoService.mostrarFechaAmigable(prestamo.getFechaDevolucion());
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString()
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='libro-solicitado' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter; color:black;'>Saludos "
                        + prestamo.getUsuario().getUsuario()
                        + ", hemos recibido tu orden de préstamo</h3><table cellspacing='0' cellpadding='0'><tr style='background-color: #008fa7; color: antiquewhite;'><th>Código</th><td>"
                        + String.valueOf(prestamo.getId()) + "</td></tr><tr><th>Libro</th><td>"
                        + prestamo.getLibro().getTitulo() + " (" + prestamo.getLibro().getAutor() + ")" + "</td></tr>"
                        + "<tr><th>Local</th><td>" + prestamo.getLibro().getLocal().getDireccion() + "</td></tr>"
                        + "<tr><th>Fecha Despacho</th><td>" + fechaDespacho + "</td></tr>"
                        + "<tr><th>Fecha Devolucion</th><td>" + fechaDevolucion + "</td></tr></table></div>";
                path = "static/img/libro-solicitado.png";
                break;
            case "Orden Confirmada":
                prestamo = (Prestamo) model.get("prestamo");
                fechaDespacho = prestamoService.mostrarFechaAmigable(prestamo.getFechaDespacho());
                fechaDevolucion = prestamoService.mostrarFechaAmigable(prestamo.getFechaDevolucion());
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString()
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='orden-confirmada' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter; color:black;'>Saludos "
                        + prestamo.getUsuario().getUsuario()
                        + ", le comunicamos que su orden de préstamo ha sido confirmada.</h3><table cellspacing='0' cellpadding='0'><tr style='background-color: #008fa7; color: antiquewhite;'><th>Código</th><td>"
                        + String.valueOf(prestamo.getId()) + "</td></tr><tr><th>Libro</th><td>"
                        + prestamo.getLibro().getTitulo() + " (" + prestamo.getLibro().getAutor() + ")" + "</td></tr>"
                        + "<tr><th>Local</th><td>" + prestamo.getLibro().getLocal().getDireccion() + "</td></tr>"
                        + "<tr><th>Fecha Despacho</th><td>" + fechaDespacho + "</td></tr>"
                        + "<tr><th>Fecha Devolucion</th><td>" + fechaDevolucion + "</td></tr></table></div>";
                path = "static/img/orden-confirmada.png";
                break;
        }
        helper.setText(cabecera + cuerpo + pie, true);
        helper.setFrom(model.get("from").toString());
        helper.setTo(model.get("to").toString());
        helper.setSubject(model.get("subject").toString());
        helper.addInline("logo-biblioteca2020", new ClassPathResource(path));
        sender.send(message);
    }

    @Async
    public void enviarEmailwithCronSchedule(Map<String, Object> model) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        ByteArrayInputStream bis = null;
        List<Prestamo> prestamos = null;
        List<Usuario> usuarios = null;
        List<Libro> libros = null;
        String path = "";
        String nomReporte = "";
        String cabecera = "<!DOCTYPE html><html><head><meta charset='UTF-8'/><meta name='viewport' content='width=device-width, initial-scale=1.0'/><style> th { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: .5em; text-align: right; } td { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: .5em; padding-left: 2em; text-align: justify; } </style></head>";
        String cuerpo = "";
        String pie = "<div id='footer' style='padding-top: 2em; text-align: center; font-weight: lighter; font-size: 1.1rem;'><p>Biblioteca &copy;2020</p></div></div></body></html>";
        String mesActual = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "PE"))
                .toUpperCase();
        switch (model.get("titulo").toString()) {
            case "Reporte mensual de préstamos":
                prestamos = (List<Prestamo>) model.get("list");
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString() + " | " + mesActual
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='reporte' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter; color:black;'>Saludos, "
                        + "hasta el mes de " + mesActual
                        + ", el total de préstamos registrados en nuestra base de datos ha sido de: " + prestamos.size()
                        + ", distribuidos en todos los locales anexos.</h3><h3>Para mayor detalle, revisar el archivo adjunto.</h3></div>";
                path = "static/img/img-reporte.png";
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos | " + mesActual, prestamos);
                nomReporte = "reporte-prestamos-" + mesActual + ".pdf";
                break;
            case "Reporte mensual de usuarios":
                usuarios = (List<Usuario>) model.get("list");
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString() + " | " + mesActual
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='reporte' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter; color:black;'>Saludos, "
                        + "hasta el mes de " + mesActual
                        + ", el total de usuarios registrado en nuestra base de datos ha sido de: " + usuarios.size()
                        + ", distribuidos en todos los locales anexos.</h3><h3>Para mayor detalle, revisar el archivo adjunto.</h3></div>";
                path = "static/img/img-reporte.png";
                bis = GenerarReportePDF.generarPDFUsuarios("Reporte de usuarios | " + mesActual, usuarios);
                nomReporte = "reporte-usuarios-" + mesActual + ".pdf";
                break;
            case "Reporte de libros con stock bajo":
                libros = (List<Libro>) model.get("list");
                cuerpo = "<body style='color: grey; background-color: rgb(0, 143, 167); font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'><div id='container' style='margin-left: auto; margin-right: auto; padding-left: 5em; padding-right: 5em; margin-top: 7em; padding-bottom: 2em; background-color: white; -webkit-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); -moz-box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); box-shadow: 10px 10px 11px 0px rgba(59, 58, 59, 0.5); border: none; width: 400px; border: solid 1px rgba(59, 58, 59, 0.096);'><h1 id='titulo' style='padding-top: 1em; text-align: center; font-weight: lighter; font-size: 2.3em; color: black;'>"
                        + model.get("titulo").toString()
                        + "</h1><div id='imagen' style='text-align: center;'><img src='cid:logo-biblioteca2020' alt='reporte' width='60%' /></div><div style='font-size: 1.1rem; text-align: center;'><h3 style='font-weight: lighter; color:black;'>Saludos, a continuación se adjunta el reporte de los libros de los locales anexos con un stock bajo (menor a "
                        + stock + ").</h3><h3>Para mayor detalle, revisar el archivo adjunto.</h3></div>";
                path = "static/img/img-reporte.png";
                bis = GenerarReportePDF.generarPDFLibros("Reporte de libros con stock bajo | " + mesActual, libros);
                nomReporte = "reporte-libros-bajo-stock-" + mesActual + ".pdf";
                break;
        }
        helper.setText(cabecera + cuerpo + pie, true);
        helper.setFrom(model.get("from").toString());
        helper.setTo(model.get("to").toString());
        helper.setSubject(model.get("subject").toString());
        helper.addInline("logo-biblioteca2020", new ClassPathResource(path));
        DataSource dataSource = new ByteArrayDataSource(bis, "application/pdf");
        helper.addAttachment(nomReporte, dataSource);

        sender.send(message);
    }

}