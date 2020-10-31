package com.biblioteca.backend.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.EmailService;
import com.biblioteca.backend.service.ILibroService;
import com.biblioteca.backend.service.IPrestamoService;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    // private static final String correoDiego = "luis290613@gmail.com";
    private static final String correoSysadmin = "edi@live.it";

    @Value("{spring.mail.username}")
    private String correoDeveloper;

    @Value("${scheduler.stocklibro}")
    private Integer stock;

    @Autowired
    private IPrestamoService prestamoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ILibroService libroService;

    @Autowired
    private EmailService emailService;

    // ENVIAR CORREO DE PRÉSTAMOS TOTALES CADA MES AL SYSADMIN
    // SE ENVÍA CADA FIN DE MES A LAS 12 AM (MEDIANOCHE)
    // SE PROGRAMA LA TAREA PARA QUE SE REPITA CADA DIA A MEDIANOCHE ...
    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Lima") // firme
    // @Scheduled(cron = "0 */1 * ? * *", zone = "America/Lima") // prueba
    public void enviarEmailPrestamosTotalesMensuales() {
        // .. Y DESPUÈS SE PREGUNTA SI ESTOY EN EL ULTIMO DIA DE ESTE MES
        final Calendar c = Calendar.getInstance();
        if (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE)) {
            // ESTABLECER DATASOURCE
            List<Prestamo> prestamos = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
            if (prestamos.size() > 0) {
                // FILTRAR SOLO LOS RESULTADOS DEL ULTIMO MES
                // O MEJOR DICHO, DEJO SOLAMENTE LOS RESULTADOS DEL ULTIMO MES
                Locale esp = new Locale("es", "PE");
                Calendar calUltimoDiaMes = Calendar.getInstance(esp);
                TimeZone tz = calUltimoDiaMes.getTimeZone();
                ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
                calUltimoDiaMes.set(Calendar.DAY_OF_MONTH, calUltimoDiaMes.getActualMaximum(Calendar.DAY_OF_MONTH));
                calUltimoDiaMes.add(Calendar.MONTH, -1);
                System.out.println("PRESTAMOS TOTALES - FECHA DEL ULTIMO DIA DEL MES ANTERIOR: "
                        + calUltimoDiaMes.getTime().toString().toUpperCase());
                for (int i = 0; i < prestamos.size(); i++) {
                    prestamos.removeIf(n -> n.getFechaDespacho().isBefore(LocalDateTime.ofInstant(calUltimoDiaMes.toInstant(), zid)));
                    if (prestamos.size() == 0) {
                        System.out.println("PRESTAMOS TOTALES - NO HAY PRÉSTAMOS DE "
                                + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, esp).toUpperCase() + " "
                                + LocalDate.now().getYear());
                    } else {
                        System.out.println("PRESTAMOS TOTALES - ID DE PRESTAMO DE "
                                + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, esp).toUpperCase() + " "
                                + LocalDate.now().getYear() + ": " + prestamos.get(i).getId());
                    }
                }
                List<Prestamo> pMesAnterior = new ArrayList<>();
                prestamos = pMesAnterior;
                System.out.println("NRO DE PRESTAMOS DE "
                        + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, esp).toUpperCase() + " "
                        + LocalDate.now().getYear() + ": " + pMesAnterior.size());
                // CREAR EMAIL Y ENVIAR AL SYSADMIN
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("titulo", "Reporte mensual de préstamos");
                    model.put("from", "Biblioteca2020 " + "<" + correoDeveloper + ">");
                    model.put("to", correoSysadmin);
                    model.put("list", prestamos);
                    model.put("subject", "Reporte mensual de préstamos | Biblioteca2020");
                    emailService.enviarEmailwithCronSchedule(model);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                System.out.println("PRESTAMOS TOTALES - NRO DE PRESTAMOS TOTALES: " + prestamos.size());
            }
        } else {
            System.out.println("PRESTAMOS TOTALES - HOY NO ES EL PRIMER DIA DEL MES");
        }
    }

    // ENVIAR CORREO DE USUARIOS REGISTRADOS CADA MES AL SYSADMIN
    // SE ENVÍA CADA FIN DE MES A LAS 12 AM (MEDIANOCHE)
    // SE PROGRAMA LA TAREA PARA QUE SE REPITA CADA DIA A MEDIANOCHE ...
    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Lima") // firme
    // @Scheduled(cron = "0 */1 * ? * *", zone = "America/Lima") // prueba
    public void enviarEmailUsuariosRegistradosMensuales() {
        // .. Y DESPUÈS SE PREGUNTA SI ESTOY EN EL ULTIMO DIA DE ESTE MES
        final Calendar c = Calendar.getInstance();
        if (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE)) {
            // ESTABLECER DATASOURCE
            List<Usuario> usuarios = usuarioService.findByRol("ROLE_USUARIO");
            if (usuarios.size() != 0) {
                // FILTRAR SOLO LOS RESULTADOS DEL ULTIMO MES
                // O MEJOR DICHO, DEJO SOLAMENTE LOS RESULTADOS DEL ULTIMO MES
                Locale esp = new Locale("es", "PE");
                Calendar calUltimoDiaMes = Calendar.getInstance(esp);
                TimeZone tz = calUltimoDiaMes.getTimeZone();
                ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
                calUltimoDiaMes.set(Calendar.DAY_OF_MONTH, calUltimoDiaMes.getActualMaximum(Calendar.DAY_OF_MONTH));
                calUltimoDiaMes.add(Calendar.MONTH, -1);
                System.out.println("USUARIOS TOTALES - FECHA DEL ULTIMO DIA DEL MES ANTERIOR: "
                        + calUltimoDiaMes.getTime().toString().toUpperCase());
                for (int i = 0; i < usuarios.size(); i++) {
                    usuarios.removeIf(n -> n.getFechaRegistro().isBefore(LocalDateTime.ofInstant(calUltimoDiaMes.toInstant(), zid)));
                    if (usuarios.size() == 0) {
                        System.out.println("USUARIOS TOTALES - NO HAY USUARIOS DE "
                                + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, esp).toUpperCase() + " "
                                + LocalDate.now().getYear());
                    } else {
                        System.out.println("USUARIOS TOTALES - ID DE USUARIO DE "
                                + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, esp).toUpperCase() + " "
                                + LocalDate.now().getYear() + ": " + usuarios.get(i).getId());
                    }
                }
                // CREAR EMAIL Y ENVIAR AL SYSADMIN
                try {
                    if (usuarios.size() > 0) {
                        Map<String, Object> model = new HashMap<>();
                        model.put("titulo", "Reporte mensual de usuarios");
                        model.put("from", "Biblioteca2020 " + "<" + correoDeveloper + ">");
                        model.put("to", correoSysadmin);
                        model.put("list", usuarios);
                        model.put("subject", "Reporte mensual de usuarios | Biblioteca2020");
                        emailService.enviarEmailwithCronSchedule(model);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                System.out.println("USUARIOS TOTALES - NRO DE USUARIOS TOTALES: " + usuarios.size());
            }
        } else {
            System.out.println("USUARIOS TOTALES - HOY NO ES EL PRIMER DIA DEL MES");
        }
    }

    // ENVIAR CORREO DE STOCK DE LIBROS MENORES A 20 CADA MES AL SYSADMIN
    // SE ENVÍA CADA DIA A LAS 12 AM (MEDIANOCHE)
    @Scheduled(cron = "0 0 0 * * ?", zone = "America/Lima") // firme
    // @Scheduled(cron = "0 */2 * ? * *", zone = "America/Lima") // prueba
    public void enviarEmailStockLibrosMensuales() {
        List<Libro> libros;
        try {
            libros = libroService.fetchWithCategoriaWithLocal();
            if (libros.size() != 0) {
                // FILTRAR SOLO LOS RESULTADOS CON STOCK MENORES A 20
                for (int i = 0; i < libros.size(); i++) {
                    libros.removeIf(l -> l.getStock() >= stock);
                }
                if (libros.size() == 0) {
                    System.out.println(
                            dateFormat.format(new Date()) + "LIBROS - NO HAY LIBROS CON STOCK MENOR A LAS 20 UNIDADES");
                } else
                    System.out.println(dateFormat.format(new Date())
                        + "LIBROS - LIBROS CON STOCK MENOR A LAS 20 UNIDADES: " + libros.size());
                if (libros.size() > 0) {
                    // CREAR EMAIL Y ENVIAR AL SYSADMIN
                    Map<String, Object> model = new HashMap<>();
                    model.put("titulo", "Reporte de libros con stock bajo");
                    model.put("from", "Biblioteca2020 " + "<" + correoDeveloper + ">");
                    model.put("to", correoSysadmin);
                    model.put("list", libros);
                    model.put("subject", "Reporte de libros con stock bajo | Biblioteca2020");
                    emailService.enviarEmailwithCronSchedule(model);
                }
            } else
                System.out.println(dateFormat.format(new Date()) + "LIBROS - NO HAY LIBROS CON STOCK MENOR A LAS "
                    + stock + " UNIDADES.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}