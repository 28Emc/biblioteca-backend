package com.biblioteca.backend.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Categoria.Categoria;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.ICategoriaService;
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
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Api(value = "reportes", description = "Métodos de generación de reportes en PDF y XLSX (Excel)")
public class ReporteController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPrestamoService prestamoService;

    @Autowired
    private ILibroService libroService;

    @Autowired
    private ICategoriaService categoriaService;

    // ######################## PRÉSTAMOS ########################
    // ######################## PDF ########################
    // GENERAR REPORTE PDF DE PRESTAMOS TOTALES
    @ApiOperation(value = "Generación de reporte en formato pdf de préstamos totales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/prestamos-totales", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosTotal(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Prestamo> prestamos = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_ADMIN":
                    prestamos = prestamoService
                            .findAllByAdmin(usuarioLogueado);
                    break;
                case "ROLE_EMPLEADO":
                    prestamos = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getIdUsuario().equals(usuarioLogueado.getId()))
                            .collect(Collectors.toList());
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
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF DE PRESTAMOS PENDIENTES
    @ApiOperation(value = "Generación de reporte en formato pdf de préstamos pendientes", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/prestamos-pendientes", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosPendientes(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Prestamo> prestamosTotales = null;
            switch (usuarioLogueado.getRol().getAuthority()) {
                case "ROLE_ADMIN":
                    prestamosTotales = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getEstado().equals("E1"))
                            .collect(Collectors.toList());
                    break;
                case "ROLE_EMPLEADO":
                    prestamosTotales = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getIdUsuario().equals(usuarioLogueado.getId()) &&
                                    prestamo.getEstado().equals("E1"))
                            .collect(Collectors.toList());
                    break;
            }
            // FILTRO SOLAMENTE LOS PRESTAMOS PENDIENTES
            for (int i = 0; i < prestamosTotales.size(); i++) {
                prestamosTotales.removeIf(n -> n.getEstado().equals("E1"));
            }
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (prestamosTotales.size() != 0) {
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos pendientes", prestamosTotales);
                headers.add("Content-Disposition", "inline; filename=prestamos-pendientes-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF DE PRESTAMOS COMPLETADOS
    @ApiOperation(value = "Generación de reporte en formato pdf de préstamos terminados o anulados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/prestamos-completados", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosCompletados(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Prestamo> prestamosTotales = null;
            switch (usuarioLogueado.getRol().getAuthority()) {
                case "ROLE_ADMIN":
                    prestamosTotales = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getEstado().equals("E3"))
                            .collect(Collectors.toList());
                    break;
                case "ROLE_EMPLEADO":
                    prestamosTotales = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getIdUsuario().equals(usuarioLogueado.getId()) &&
                                    prestamo.getEstado().equals("E3"))
                            .collect(Collectors.toList());
                    break;
            }
            // FILTRO SOLAMENTE LOS PRESTAMOS COMPLETADOS
            for (int i = 0; i < prestamosTotales.size(); i++) {
                prestamosTotales.removeIf(n -> !n.getEstado().equals("E3"));
            }
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (prestamosTotales.size() != 0) {
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos completados", prestamosTotales);
                headers.add("Content-Disposition", "inline; filename=prestamos-completados-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF DE PRESTAMOS POR EMPLEADO
    @ApiOperation(value = "Generación de reporte en formato pdf de préstamos por id empleado", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/reportes/pdf/prestamos-por-empleado/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosPorEmpleado(@PathVariable("id") Long id,
                                                            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prestamo> prestamos = prestamoService
                    .findAllByAdmin(usuarioLogueado)
                    .stream()
                    .filter(prestamo -> prestamo.getEmpleado().getId().equals(id))
                    .collect(Collectors.toList());
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos por empleado", prestamos);
                headers.add("Content-Disposition", "inline; filename=prestamos-por-empleado-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF DE PRESTAMOS POR USUARIO
    @ApiOperation(value = "Generación de reporte en formato pdf de préstamos por id usuario", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/prestamos-por-usuario/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosPorUsuario(@PathVariable("id") Long id,
                                                           Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prestamo> prestamos = prestamoService
                    .findAllByAdmin(usuarioLogueado)
                    .stream()
                    .filter(prestamo -> prestamo.getIdUsuario().equals(id))
                    .collect(Collectors.toList());
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos Por usuario", prestamos);
                headers.add("Content-Disposition", "inline; filename=prestamos-por-usuario-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF DE PRESTAMOS POR LIBRO
    @ApiOperation(value = "Generación de reporte en formato pdf de préstamos por id libro", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = {"/reportes/pdf/prestamos-por-libro/{id}",
            "/reportes/pdf/prestamos-por-libro/{id}"}, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfPrestamosPorLibro(@PathVariable(value = "id", required = false) Long id,
                                                         Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prestamo> prestamos = prestamoService
                    .findAllByAdmin(usuarioLogueado)
                    .stream()
                    .filter(prestamo -> prestamo.getLibro().getId().equals(id))
                    .collect(Collectors.toList());
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                bis = GenerarReportePDF.generarPDFPrestamos("Reporte de préstamos Por libro", prestamos);
                headers.add("Content-Disposition", "inline; filename=prestamos-por-libro-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ######################## EXCEL ########################
    // GENERAR REPORTE EXCEL PRESTAMOS TOTALES
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos totales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-totales")
    public ResponseEntity<?> repPrestamosTotales(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Prestamo> prestamos = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_ADMIN":
                    prestamos = prestamoService.findAllByAdmin(usuarioLogueado);
                    break;
                case "ROLE_EMPLEADO":
                    prestamos = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getEmpleado().getId().equals(usuarioLogueado.getId()))
                            .collect(Collectors.toList());
                    break;
            }
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos totales", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-totales.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS PENDIENTES
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos pendientes", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-pendientes")
    public ResponseEntity<?> repPrestamosPendientes(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Prestamo> prestamos = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_ADMIN":
                    prestamos = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getEstado().equals("E1"))
                            .collect(Collectors.toList());
                    break;
                case "ROLE_EMPLEADO":
                    prestamos = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getEstado().equals("E1") &&
                                    prestamo.getEmpleado().getId().equals(usuarioLogueado.getId()))
                            .collect(Collectors.toList());
                    break;
            }
            // FILTRO SOLAMENTE LOS PRESTAMOS PENDIENTES
            for (int i = 0; i < prestamos.size(); i++) {
                prestamos.removeIf(n -> n.getEstado().equals("E1"));
            }
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos pendientes", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-pendientes.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS COMPLETADOS
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos completados o anulados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-completados")
    public ResponseEntity<?> repPrestamosCompletados(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Prestamo> prestamos = null;
            switch (usuarioLogueado.getRol().getAuthority().toString()) {
                case "ROLE_ADMIN":
                    prestamos = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getEstado().equals("E3"))
                            .collect(Collectors.toList());
                    break;
                case "ROLE_EMPLEADO":
                    prestamos = prestamoService
                            .findAllByAdmin(usuarioLogueado)
                            .stream()
                            .filter(prestamo -> prestamo.getEstado().equals("E1") &&
                                    prestamo.getEmpleado().getId().equals(usuarioLogueado.getId()))
                            .collect(Collectors.toList());
                    break;
            }
            // FILTRO SOLAMENTE LOS PRESTAMOS PENDIENTES
            for (int i = 0; i < prestamos.size(); i++) {
                prestamos.removeIf(n -> !n.getEstado().equals("E1"));
            }
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos completados", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-completados.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS POR EMPLEADO
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos filtrados por id empleado", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/reportes/xlsx/prestamos-por-empleado/{id}")
    public ResponseEntity<?> repPrestamosPorEmpleado(@PathVariable("id") Long id,
                                                     Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prestamo> prestamos = null;
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            prestamos = prestamoService
                    .findAllByAdmin(usuarioLogueado)
                    .stream()
                    .filter(prestamo -> prestamo.getEmpleado().getId().equals(id))
                    .collect(Collectors.toList());
            if (prestamos.size() != 0 || id == null) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos por empleado", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-por-empleado.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS POR USUARIO
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos filtrados por id usuario", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-por-usuario/{id}")
    public ResponseEntity<?> repPrestamosPorUsuario(@PathVariable("id") Long id,
                                                    Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos = null;
        try {
            prestamos = prestamoService
                    .findAllByAdmin(usuarioLogueado)
                    .stream()
                    .filter(prestamo -> prestamo.getIdUsuario().equals(id))
                    .collect(Collectors.toList());
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos por usuario", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-por-usuario.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL PRESTAMOS POR LIBRO
    @ApiOperation(value = "Generación de reporte en formato xlsx de préstamos filtrados por id libro", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/prestamos-por-libro/{id}")
    public ResponseEntity<?> repPrestamosPorLibro(@PathVariable("id") Long id,
                                                  Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prestamo> prestamos = null;
            prestamos = prestamoService
                    .findAllByAdmin(usuarioLogueado)
                    .stream()
                    .filter(prestamo -> prestamo.getLibro().getId().equals(id))
                    .collect(Collectors.toList());
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            if (prestamos.size() != 0) {
                in = GenerarReporteExcel.generarExcelPrestamos("Reporte de préstamos por libro", prestamos);
                headers.add("Content-Disposition", "attachment; filename=listado-prestamos-por-libro.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ######################## USUARIOS ########################
    // ######################## PDF ########################
    // GENERAR REPORTE PDF USUARIOS TOTALES
    @ApiOperation(value = "Generación de reporte en formato pdf de usuarios totales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/usuarios-totales", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfUsuariosTotal() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Usuario> usuarios = null;
            usuarios = usuarioService.findAllUsers();
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (usuarios.size() != 0) {
                bis = GenerarReportePDF.generarPDFUsuarios("Reporte de usuarios totales", usuarios);
                headers.add("Content-Disposition", "inline; filename=listado-usuarios-totales.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF USUARIOS POR ESTADO
    @ApiOperation(value = "Generación de reporte en formato pdf de usuarios por su estado", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "Solo puede escoger entre disponibles y no disponibles"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El reporte no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/pdf/usuarios-por-estado/{estado}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfUsuariosPorEstado(@PathVariable("estado") String estado) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Usuario> usuarios = null;
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            usuarios = usuarioService.findAllUsers();
            String titulo = "";
            String tituloPdf = "";
            // USO UN STRING EN VEZ DE UN BOOLEAN PARA HACER SALTAR LA EXCEPCION
            if (estado.equals("true")) {
                // FILTRO SOLAMENTE LOS USUARIOS ACTIVOS
                for (int i = 0; i < usuarios.size(); i++) {
                    usuarios.removeIf(n -> n.isActivo());
                }
                titulo = "listado-usuarios-disponibles";
                tituloPdf = "Reporte de usuarios disponibles";
            } else if (estado.equals("false")) {
                // FILTRO SOLAMENTE LOS USUARIOS INACTIVOS
                for (int i = 0; i < usuarios.size(); i++) {
                    usuarios.removeIf(n -> !n.isActivo());
                }
                titulo = "listado-usuarios-no-disponibles";
                tituloPdf = "Reporte de usuarios no disponibles";
            } else if (!estado.equals("true") || estado.equals("false")) {
                response.put("message", "Lo sentimos, solo puede escoger entre disponibles y no disponibles");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            if (usuarios.size() != 0) {
                bis = GenerarReportePDF.generarPDFUsuarios(tituloPdf, usuarios);
                headers.add("Content-Disposition", "inline; filename=" + titulo + ".pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, el reporte solicitado no existe");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException | IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ######################## EXCEL ########################
    // GENERAR REPORTE EXCEL USUARIOS TOTALES
    @ApiOperation(value = "Generación de reporte en formato xlsx de usuarios totales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a ete recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/usuarios-totales")
    public ResponseEntity<?> generarExcelUsuariosTotal() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Usuario> usuarios = null;
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            usuarios = usuarioService.findAllUsers();
            if (usuarios.size() != 0) {
                bis = GenerarReporteExcel.generarExcelUsuarios("Reporte de usuarios totales", usuarios);
                headers.add("Content-Disposition", "attachment; filename=listado-usuarios-totales.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL USUARIOS POR ESTADO
    @ApiOperation(value = "Generación de reporte en formato xlsx de usuarios por su estado", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "Solo puede escoger entre disponibles y no disponibles"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El reporte no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/reportes/xlsx/usuarios-por-estado/{estado}")
    public ResponseEntity<?> repUsuariosPorEstado(@PathVariable("estado") String estado) {
        Map<String, Object> response = new HashMap<>();
        try {
            ByteArrayInputStream in;
            List<Usuario> usuarios = null;
            usuarios = usuarioService.findAllUsers();
            var headers = new HttpHeaders();
            String titulo = "";
            String tituloExcel = "";
            // USO UN STRING EN VEZ DE UN BOOLEAN PARA HACER SALTAR LA EXCEPCION
            if (estado.equals("true")) {
                for (int i = 0; i < usuarios.size(); i++) {
                    usuarios.removeIf(n -> n.isActivo());
                }
                titulo = "listado-usuarios-disponibles";
                tituloExcel = "Reporte de usuarios disponibles";
            } else if (estado.equals("false")) {
                for (int i = 0; i < usuarios.size(); i++) {
                    usuarios.removeIf(n -> !n.isActivo());
                }
                titulo = "listado-usuarios-no-disponibles";
                tituloExcel = "Reporte de usuarios no disponibles";
            } else if (!estado.equals("true") || estado.equals("false")) {
                response.put("message", "Lo sentimos, solo puede escoger entre disponibles y no disponibles");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            if (usuarios.size() != 0) {
                in = GenerarReporteExcel.generarExcelUsuarios(tituloExcel, usuarios);
                headers.add("Content-Disposition", "attachment; filename=" + titulo + ".xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, el reporte solicitado no existe");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException | IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ######################## LIBROS ########################
    // ######################## PDF ########################
    // GENERAR REPORTE PDF LIBROS UNICOS
    @ApiOperation(value = "Generación de reporte en formato pdf de libros totales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tiene acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = {"/locales/{idLocal}/libros/reportes/pdf/libros-unicos",
            "/locales/libros/reportes/pdf/libros-unicos"}, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfLibrosUnicos(@PathVariable(value = "idLocal") Optional<Long> idLocal,
                                                    Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Libro> libros = libroService.fetchWithCategoriaWithLocal();
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            String rol = "";

            rol = usuarioLogueado.getRol().getAuthority();
            if (libros.size() != 0) {
                bis = GenerarReportePDF.generarPDFLibros(rol, "Reporte de Libros", libros);
                headers.add("Content-Disposition", "inline; filename=listado-libros-unicos.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF LIBROS POR CATEGORÍA
    @ApiOperation(value = "Generación de reporte en formato pdf de libros por id categoría", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = {"/locales/{idLocal}/libros/reportes/pdf/libros-por-categoria/{id_categoria}",
            "/locales/libros/reportes/pdf/libros-por-categoria/{id_categoria}"}, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfLibrosPorCategoria(@PathVariable(value = "idLocal") Optional<Long> idLocal,
                                                          @PathVariable(name = "id_categoria", required = false) Long id, Authentication authentication) throws Exception {
        Map<String, Object> response = new HashMap<>();
        try {
            String rol = "";
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            Categoria categoria = categoriaService.findById(id).get();
            List<Libro> libros = libroService.findByCategoria(categoria.getNombre());

            rol = usuarioLogueado.getRol().getAuthority();
            if (libros.size() != 0) {
                bis = GenerarReportePDF.generarPDFLibros(rol, "Reporte de Libros Por Categoría", libros);
                headers.add("Content-Disposition", "inline; filename=listado-libros-por-categoria.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF LIBROS POR ESTADO
    @ApiOperation(value = "Generación de reporte en formato pdf de libros por su estado", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "Solo puede escoger entre disponibles y no disponibles"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El reporte no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = {"/locales/{idLocal}/libros/reportes/pdf/libros-por-estado/{estado}",
            "/locales/libros/reportes/pdf/libros-por-estado/{estado}"}, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfLibrosPorEstado(@PathVariable(value = "idLocal") Optional<Long> idLocal,
                                                       @PathVariable("estado") String estado, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            String titulo = "";
            String tituloPdf = "";
            String rol = "";
            List<Libro> libros = new ArrayList<>();
            // USO UN STRING EN VEZ DE UN BOOLEAN PARA HACER SALTAR LA EXCEPCION
            if (estado.equals("true")) {
                libros = libroService.findByIsActivo(true);
                titulo = "listado-libros-disponibles";
                tituloPdf = "Reporte de Libros Disponibles";
            } else if (estado.equals("false")) {
                libros = libroService.findByIsActivo(false);
                titulo = "listado-libros-no-disponibles";
                tituloPdf = "Reporte de Libros No Disponibles";
            }
            rol = usuarioLogueado.getRol().getAuthority();
            if (libros.size() != 0) {
                bis = GenerarReportePDF.generarPDFLibros(rol, tituloPdf, libros);
                headers.add("Content-Disposition", "inline; filename=" + titulo + ".pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, el reporte solicitado no existe");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException | IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ######################## EXCEL ########################
    // GENERAR REPORTE EXCEL LIBROS UNICOS
    @ApiOperation(value = "Generación de reporte en formato xlsx de libros totales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = {"/locales/{idLocal}/libros/reportes/xlsx/libros-unicos",
            "/locales/libros/reportes/xlsx/libros-unicos"})
    public ResponseEntity<?> generarExcelLibrosUnicos(@PathVariable(value = "idLocal") Optional<Long> idLocal,
                                                      Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String rol = "";
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Libro> libros = libroService.fetchWithCategoriaWithLocal();
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            rol = usuarioLogueado.getRol().getAuthority();
            if (libros.size() != 0) {
                bis = GenerarReporteExcel.generarExcelLibros(rol, "Reporte de Libros Unicos", libros);
                headers.add("Content-Disposition", "attachment; filename=listado-libros-unicos.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL LIBROS POR CATEGORÍA
    @ApiOperation(value = "Generación de reporte en formato xlsx de libros por id categoría", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = {"/locales/{idLocal}/libros/reportes/xlsx/libros-por-categoria/{id}",
            "/locales/libros/reportes/xlsx/libros-por-categoria/{id}"})
    public ResponseEntity<?> repLibrosPorCategoria(@PathVariable(value = "idLocal") Optional<Long> idLocal,
                                                   @PathVariable("id") String id, Authentication authentication) throws Exception {
        Map<String, Object> response = new HashMap<>();
        try {
            String rol = "";
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            Categoria categoria = categoriaService.findById(Long.parseLong(id)).get();
            List<Libro> libros = libroService.findByCategoria(categoria.getNombre());
            rol = usuarioLogueado.getRol().getAuthority();
            if (libros.size() != 0) {
                in = GenerarReporteExcel.generarExcelLibros(rol, "Reporte de Libros Por Categoría", libros);
                headers.add("Content-Disposition", "attachment; filename=listado-libros-por-categoria.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL LIBROS POR ESTADO
    @ApiOperation(value = "Generación de reporte en formato xlsx de libros por su estado", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "Solo puede escoger entre disponibles y no disponibles"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El reporte no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = {"/locales/{idLocal}/libros/reportes/xlsx/libros-por-estado/{estado}",
            "/locales/libros/reportes/xlsx/libros-por-estado/{estado}"})
    public ResponseEntity<?> repLibrosPorEstado(@PathVariable(value = "idLocal") Optional<Long> idLocal,
                                                @PathVariable("estado") String estado, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Libro> libros = new ArrayList<>();
            ByteArrayInputStream in;
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            var headers = new HttpHeaders();
            String titulo = "";
            String tituloExcel = "";
            String rol = "";
            // USO UN STRING EN VEZ DE UN BOOLEAN PARA HACER SALTAR LA EXCEPCION
            if (estado.equals("true")) {
                libros = libroService.findByIsActivo(true);
                titulo = "listado-libros-disponibles";
                tituloExcel = "Reporte de Libros Disponibles";
            } else if (estado.equals("false")) {
                libros = libroService.findByIsActivo(false);
                titulo = "listado-libros-no-disponibles";
                tituloExcel = "Reporte de Libros No Disponibles";
            }
            rol = usuarioLogueado.getRol().getAuthority();
            if (libros.size() != 0) {
                in = GenerarReporteExcel.generarExcelLibros(rol, tituloExcel, libros);
                headers.add("Content-Disposition", "attachment; filename=" + titulo + ".xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, el reporte solicitado no existe");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException | IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ######################## EMPLEADOS ########################
    // ######################## PDF ########################
    // GENERAR REPORTE PDF DE EMPLEADOS TOTALES
    @ApiOperation(value = "Generación de reporte en formato pdf de empleados totales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    @GetMapping(value = "/reportes/pdf/empleados-totales", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfEmpleadosTotal(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Usuario> empleados = usuarioService.findByRoles();
            for (int i = 0; i < empleados.size(); i++) {
                // QUITAR DEL LISTADO DE EMPLEADOS LOS EMPLEADOS CON ROL PRUEBA
                empleados.removeIf(e -> e.getUsuario().equals("prueba"));
            }
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (empleados.size() != 0) {
                bis = GenerarReportePDF.generarPDFUsuarios("Reporte de empleados totales", empleados);
                headers.add("Content-Disposition", "inline; filename=listado-empleados-totales.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE PDF DE EMPLEADOS POR ESTADO
    @ApiOperation(value = "Generación de reporte en formato pdf de empleados por su estado", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "Solamente puedes escoger entre disponibles y no disponibles"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    @GetMapping(value = "/reportes/pdf/empleados-por-estado/{estado}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfLibrosPorEstado(@PathVariable("estado") String estado,
                                                       Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Usuario> empleados = usuarioService.findByRoles();
            String titulo = "";
            String tituloPdf = "";
            // USO UN STRING EN VEZ DE UN BOOLEAN PARA HACER SALTAR LA EXCEPCION
            if (estado.equals("true")) {
                // FILTRO SOLAMENTE LOS EMPLEADOS ACTIVOS Y SIN USUARIOS CON ROL PRUEBA
                for (int i = 0; i < empleados.size(); i++) {
                    // QUITAR DEL LISTADO DE EMPLEADOS LOS EMPLEADOS CON ROL PRUEBA
                    empleados.removeIf(e -> e.getUsuario().equals("prueba"));
                }
                titulo = "listado-empleados-disponibles";
                tituloPdf = "Reporte de empleados disponibles";
            } else if (estado.equals("false")) {
                for (int i = 0; i < empleados.size(); i++) {
                    empleados.removeIf(e -> e.getUsuario().equals("prueba"));
                }
                titulo = "listado-empleados-no-disponibles";
                tituloPdf = "Reporte de empleados no disponibles";
            }

            if (empleados.size() != 0) {
                bis = GenerarReportePDF.generarPDFUsuarios(tituloPdf, empleados);
                headers.add("Content-Disposition", "inline; filename=" + titulo + ".pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR PDF DE EMPLEADOS POR LOCAL
    /*@ApiOperation(value = "Generación de reporte en formato pdf de empleados por su local", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @GetMapping(value = "/reportes/pdf/empleados-por-local/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generarPdfEmpleadosPorLocal(@PathVariable("id") String id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Usuario> empleados = null;
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            empleados = usuarioService.findByLocal(Long.parseLong(id));
            for (int i = 0; i < empleados.size(); i++) {
                // QUITAR DEL LISTADO DE EMPLEADOS LOS EMPLEADOS CON ROL PRUEBA
                empleados.removeIf(e -> e.getUsuario().equals("prueba"));
            }
            if (empleados.size() != 0) {
                bis = GenerarReportePDF.generarPDFUsuarios("Reporte de empleados por local", empleados);
                headers.add("Content-Disposition", "inline; filename=empleados-por-local-reporte.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    // ######################## EXCEL ########################
    // GENERAR REPORTE EXCEL DE EMPLEADOS TOTALES
    @ApiOperation(value = "Generación de reporte en formato pdf de empleados totales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    @GetMapping(value = "/reportes/xlsx/empleados-totales")
    public ResponseEntity<?> generarExcelEmpleadosTotal(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Usuario> empleados = empleados = usuarioService.findByRoles();
            for (int i = 0; i < empleados.size(); i++) {
                // QUITAR DEL LISTADO DE EMPLEADOS LOS EMPLEADOS CON ROL PRUEBA
                empleados.removeIf(e -> e.getUsuario().equals("prueba"));
            }
            ByteArrayInputStream bis;
            var headers = new HttpHeaders();
            if (empleados.size() != 0) {
                bis = GenerarReporteExcel.generarExcelUsuarios("Reporte de empleados totales", empleados);
                headers.add("Content-Disposition", "attachment; filename=listado-empleados-totales.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(bis));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL DE EMPLEADOS POR ESTADO
    @ApiOperation(value = "Generación de reporte en formato pdf de empleados por su estado", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "Solamente puedes escoger entre disponibles y no disponibles"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde")})
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    @GetMapping(value = "/reportes/xlsx/empleados-por-estado/{estado}")
    public ResponseEntity<?> repEmpleadosPorEstado(@PathVariable("estado") String estado,
                                                   Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            ByteArrayInputStream in;
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).get();
            List<Usuario> empleados = usuarioService.findByRoles();
            var headers = new HttpHeaders();
            String titulo = "";
            String tituloExcel = "";
            // USO UN STRING EN VEZ DE UN BOOLEAN PARA HACER SALTAR LA EXCEPCION
            if (estado.equals("true")) {
                // FILTRO SOLAMENTE LOS EMPLEADOS ACTIVOS Y SIN USUARIOS CON ROL PRUEBA
                for (int i = 0; i < empleados.size(); i++) {
                    // QUITAR DEL LISTADO DE EMPLEADOS LOS EMPLEADOS CON ROL PRUEBA
                    empleados.removeIf(e -> e.getUsuario().equals("prueba"));
                }
                titulo = "listado-empleados-disponibles";
                tituloExcel = "Reporte de empleados disponibles";
            } else if (estado.equals("false")) {

                for (int i = 0; i < empleados.size(); i++) {
                    empleados.removeIf(e -> e.getUsuario().equals("prueba"));
                }
                titulo = "listado-empleados-no-disponibles";
                tituloExcel = "Reporte de empleados no disponibles";
            }

            if (empleados.size() != 0) {
                in = GenerarReporteExcel.generarExcelUsuarios(tituloExcel, empleados);
                headers.add("Content-Disposition", "attachment; filename=" + titulo + ".xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GENERAR REPORTE EXCEL DE EMPLEADOS POR LOCAL
    /*@ApiOperation(value = "Generación de reporte en formato pdf de empleados por su local", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " ", response = InputStreamResource.class),
            @ApiResponse(code = 302, message = " "),
            @ApiResponse(code = 400, message = "No tienes acceso a este recurso"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de generar el reporte. Inténtelo mas tarde") })
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @GetMapping(value = "/reportes/xlsx/empleados-por-local/{id}")
    public ResponseEntity<?> repEmpleadosPorLocal(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Usuario> empleados = null;
            ByteArrayInputStream in;
            var headers = new HttpHeaders();
            empleados = usuarioService.findByLocal(Long.parseLong(id));
            for (int i = 0; i < empleados.size(); i++) {
                // QUITAR DEL LISTADO DE EMPLEADOS LOS EMPLEADOS CON ROL PRUEBA
                empleados.removeIf(e -> e.getUsuario().equals("prueba"));
            }
            if (empleados.size() != 0) {
                in = GenerarReporteExcel.generarExcelUsuarios("Reporte de empleados por local", empleados);
                headers.add("Content-Disposition", "attachment; filename=listado-empleados-por-local.xlsx");
                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
            } else {
                response.put("message", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | NullPointerException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de generar el reporte");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
}