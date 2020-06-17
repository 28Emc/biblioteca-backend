package com.biblioteca.backend.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.biblioteca.backend.model.Libro;
import com.biblioteca.backend.model.Prestamo;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.service.ILibroService;
import com.biblioteca.backend.service.IPrestamoService;
import com.biblioteca.backend.service.IUsuarioService;
import com.biblioteca.backend.view.pdf.GenerarReportePDF;
import com.biblioteca.backend.view.xlsx.GenerarReporteExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(origins = { "*", "http://localhost:4200" })
@RestController
@Api(value = "reportes", description = "Métodos de generación de reportes en PDF y XLSX (Excel)")
public class ReporteController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPrestamoService prestamoService;

    @Autowired
    private ILibroService libroService;

    // ######################## PRÉSTAMOS ########################
    // ######################## PDF ########################
    // GENERAR REPORTE PDF DE PRESTAMOS TOTALES
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/prestamos-totales", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosTotal(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
            List<Prestamo> prestamos = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_SYSADMIN":
                    prestamos = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
                    break;
                case "ROLE_ADMIN":
                    prestamos = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleado(usuarioLogueado.getLocal().getId());
                    break;
                case "ROLE_EMPLEADO":
                    prestamos = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(usuarioLogueado.getId());
                    break;
            }
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos totales", prestamos);
                headers.add("Content-Disposition", "inline; filename=prestamos-total-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF DE PRESTAMOS PENDIENTES
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/prestamos-pendientes", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosPendientes(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
            List<Prestamo> prestamosTotales = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_SYSADMIN":
                    prestamosTotales = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
                    break;
                case "ROLE_ADMIN":
                    prestamosTotales = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleado(usuarioLogueado.getLocal().getId());
                    break;
                case "ROLE_EMPLEADO":
                    prestamosTotales = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(usuarioLogueado.getId());
                    break;
            }
            // FILTRO SOLAMENTE LOS PRESTAMOS PENDIENTES
            for (int i = 0; i < prestamosTotales.size(); i++) {
                prestamosTotales.removeIf(n -> n.isActivo());
            }
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (prestamosTotales.size() != 0) {
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos pendientes", prestamosTotales);
                headers.add("Content-Disposition", "inline; filename=prestamos-pendientes-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF DE PRESTAMOS COMPLETADOS
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/prestamos-completados", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosCompletados(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
            List<Prestamo> prestamosTotales = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_SYSADMIN":
                    prestamosTotales = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
                    break;
                case "ROLE_ADMIN":
                    prestamosTotales = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleado(usuarioLogueado.getLocal().getId());
                    break;
                case "ROLE_EMPLEADO":
                    prestamosTotales = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(usuarioLogueado.getId());
                    break;
            }
            // FILTRO SOLAMENTE LOS PRESTAMOS COMPLETADOS
            for (int i = 0; i < prestamosTotales.size(); i++) {
                prestamosTotales.removeIf(n -> !n.isActivo());
            }
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (prestamosTotales.size() != 0) {
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos completados", prestamosTotales);
                headers.add("Content-Disposition", "inline; filename=prestamos-completados-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: MODIFICAR MÉTODOS PARA GENERAR REPORTES POR EMPLEADO, LIBRO Y USUARIO

    // ######################## EXCEL ########################
    // GENERAR REPORTE EXCEL PRESTAMOS TOTALES
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos totales", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-totales")
    public ResponseEntity<?> repPrestamosTotales(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
            List<Prestamo> prestamos = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_SYSADMIN":
                    prestamos = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
                    break;
                case "ROLE_ADMIN":
                    prestamos = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleado(usuarioLogueado.getLocal().getId());
                    break;
                case "ROLE_EMPLEADO":
                    prestamos = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(usuarioLogueado.getId());
                    break;
            }
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos totales", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-totales.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS PENDIENTES
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos pendientes", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-pendientes")
    public ResponseEntity<?> repPrestamosPendientes(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
            List<Prestamo> prestamos = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_SYSADMIN":
                    prestamos = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
                    break;
                case "ROLE_ADMIN":
                    prestamos = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleado(usuarioLogueado.getLocal().getId());
                    break;
                case "ROLE_EMPLEADO":
                    prestamos = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(usuarioLogueado.getId());
                    break;
            }
            // FILTRO SOLAMENTE LOS PRESTAMOS PENDIENTES
            for (int i = 0; i < prestamos.size(); i++) {
                prestamos.removeIf(n -> n.isActivo());
            }
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos pendientes", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-pendientes.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS COMPLETADOS
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos completados o anulados", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-completados")
    public ResponseEntity<?> repPrestamosCompletados(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
            List<Prestamo> prestamos = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "[ROLE_SYSADMIN]":
                    prestamos = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
                    break;
                case "[ROLE_ADMIN]":
                    prestamos = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleado(usuarioLogueado.getLocal().getId());
                    break;
                case "[ROLE_EMPLEADO]":
                    prestamos = prestamoService
                            .fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(usuarioLogueado.getId());
                    break;
            }
            // FILTRO SOLAMENTE LOS PRESTAMOS PENDIENTES
            for (int i = 0; i < prestamos.size(); i++) {
                prestamos.removeIf(n -> !n.isActivo());
            }
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos completados", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-completados.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS POR EMPLEADO
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos filtrados por id empleado", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN','ROLE_ADMIN')")
    @GetMapping(value = "/reportes/xlsx/prestamos-por-empleado/{id}")
    public ResponseEntity<?> repPrestamosPorEmpleado(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prestamo> prestamos = null;
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            prestamos = prestamoService.fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(Long.parseLong(id));
            if (prestamos.size() != 0 || id == null) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos por empleado", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-por-empleado.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS POR USUARIO
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos filtrados por id usuario", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN','ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-por-usuario/{id}")
    public ResponseEntity<?> repPrestamosPorUsuario(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos = null;
        try {
            prestamos = prestamoService.fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserAll(Long.parseLong(id));
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos por usuario", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-por-usuario.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS POR LIBRO
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos filtrados por id libro", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN','ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-por-libro/{id}")
    public ResponseEntity<?> repPrestamosPorLibro(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prestamo> prestamos = null;
            prestamos = prestamoService.fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibro(Long.parseLong(id));
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos por libro", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-por-libro.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS POR LIBRO SYSADMIN
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos filtrados por id libro y id local (VÁLIDO SOLO PARA SYSADMIN)", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN','ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/prestamos-por-libro/{id}", params = "format=xlsx")
    public ResponseEntity<?> repPrestamosPorLibroSysadmin(
            @RequestParam(value = "buscar_libro", required = false) String buscar_libro,
            @PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // BUSCAR LOS REPORTES POR EL ID LOCAL Y EL ID LIBRO
            Libro libro = libroService.findByTituloAndLocal(buscar_libro, Long.parseLong(id)).get();
            List<Prestamo> prestamos = prestamoService
                    .fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(libro.getId(), Long.parseLong(id));
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos por libro", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-por-libro.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}