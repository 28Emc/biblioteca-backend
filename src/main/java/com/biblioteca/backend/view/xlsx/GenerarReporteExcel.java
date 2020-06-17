package com.biblioteca.backend.view.xlsx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.biblioteca.backend.model.Libro;
import com.biblioteca.backend.model.Prestamo;
import com.biblioteca.backend.model.Usuario;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GenerarReporteExcel {

    // ################ PRESTAMOS
    public static ByteArrayInputStream generarExcelPrestamos(String titulo, List<Prestamo> prestamos)
            throws IOException, NullPointerException {
        // int columns = datos.size();//para generar total de columnas por ID
        // encontrados
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            // CREANDO PLANTILLA DE EXCEL
            Sheet sheet = workbook.createSheet(titulo);
            int rowNum = 0;
            int cellNum = 0;
            // CREANDO FUENTE
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setFontName("Serif");
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            font.setItalic(false);
            // CREANDO ESTILO
            CellStyle style = workbook.createCellStyle();
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.index);
            style.setAlignment(HorizontalAlignment.CENTER);
            // SETEANDO LA FUENTE AL ESTILO
            style.setFont(font);
            // ##################### TABLA
            // CREANDO CABECERA
            ArrayList<String> listCabecera = new ArrayList<>();
            listCabecera.add("Id");
            listCabecera.add("Titulo");
            listCabecera.add("Autor");
            listCabecera.add("Categoría");
            listCabecera.add("Empleado");
            listCabecera.add("Usuario");
            listCabecera.add("Fecha Despacho");
            listCabecera.add("Fecha Devolución");
            listCabecera.add("Estado");
            // ITERANDO LISTA DE CABECERA PARA CREAR CELDAS Y APLICAR ESTILOS
            Row cabecera = sheet.createRow(rowNum);
            for (String cabeceraItem : listCabecera) {
                Cell cell = cabecera.createCell(cellNum++);
                cell.setCellValue(cabeceraItem);
                // CONDICONAL IF OPCIONAL, PERO LA DEJO PARA AGREGAR MAS ELEMENTOS ANTES
                // DE ESTA FILA EN UN FUTURO
                if (rowNum == 0) {
                    cell.setCellStyle(style);
                }
            }
            // CUERPO
            rowNum = 1; // NRO. DE FILA QUE VA JUSTO DEBAJO DE LA CABECERA
            // RECORRO MI LISTA DE PRESTAMOS PARA RELLENAR CADA CELDA DE LA FILA
            for (Prestamo prestamoItem : prestamos) {
                Row fila = sheet.createRow(rowNum++);
                fila.createCell(0).setCellValue(prestamoItem.getId());
                fila.createCell(1).setCellValue(prestamoItem.getLibro().getTitulo());
                fila.createCell(2).setCellValue(prestamoItem.getLibro().getAutor());
                fila.createCell(3).setCellValue(prestamoItem.getLibro().getCategoria().getNombre());
                if (prestamoItem.getEmpleado().getUsuario().equals("prueba")) {
                    fila.createCell(4).setCellValue("NO DEFINIDO");
                } else {
                    fila.createCell(4)
                            .setCellValue(prestamoItem.getEmpleado().getNombres() + ", "
                                    + prestamoItem.getEmpleado().getApellidoPaterno() + " "
                                    + prestamoItem.getEmpleado().getApellidoMaterno());
                }
                fila.createCell(5)
                        .setCellValue(prestamoItem.getUsuario().getNombres() + ", "
                                + prestamoItem.getUsuario().getApellidoPaterno() + " "
                                + prestamoItem.getUsuario().getApellidoMaterno());
                fila.createCell(6).setCellValue(prestamoItem.getFechaDespacho().toString());
                fila.createCell(7).setCellValue(prestamoItem.getFechaDevolucion().toString());
                if (prestamoItem.isActivo()) {
                    fila.createCell(8).setCellValue("Préstamo completado (" + prestamoItem.isActivo() + ")");
                } else {
                    fila.createCell(8).setCellValue("Libro sin devolver (" + prestamoItem.isActivo() + ")");
                }
            }
            // AJUSTO EL ANCHO DE CADA CELDA PARA MOSTRAR MEJOR LA DATA
            // NOTA: ESTA ITERACIÓN PUEDE CAUSAR BAJO RENDIMIENTO A LA HORA DE ABRIR EL
            // ARCHIVO
            for (int i = 0; i < listCabecera.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // ################ USUARIOS
    public static ByteArrayInputStream generarExcelUsuarios(String titulo, List<Usuario> usuarios) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(titulo);
            int rowNum = 0;
            int cellNum = 0;
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setFontName("Serif");
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            font.setItalic(false);
            CellStyle style = workbook.createCellStyle();
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.index);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFont(font);
            ArrayList<String> listCabecera = new ArrayList<>();
            listCabecera.add("Id");
            listCabecera.add("Nombres");
            listCabecera.add("Apellido Paterno");
            listCabecera.add("Apellido Materno");
            listCabecera.add("DNI");
            listCabecera.add("Dirección");
            listCabecera.add("Email");
            listCabecera.add("Celular");
            listCabecera.add("F. Registro");
            listCabecera.add("Username");
            listCabecera.add("Estado");
            Row cabecera = sheet.createRow(rowNum);
            for (String cabeceraItem : listCabecera) {
                Cell cell = cabecera.createCell(cellNum++);
                cell.setCellValue(cabeceraItem);
                if (rowNum == 0) {
                    cell.setCellStyle(style);
                }
            }
            rowNum = 1;
            for (Usuario usuarioItem : usuarios) {
                Row fila = sheet.createRow(rowNum++);
                fila.createCell(0).setCellValue(usuarioItem.getId());
                fila.createCell(1).setCellValue(usuarioItem.getNombres());
                fila.createCell(2).setCellValue(usuarioItem.getApellidoPaterno());
                fila.createCell(3).setCellValue(usuarioItem.getApellidoMaterno());
                fila.createCell(4).setCellValue(usuarioItem.getNroDocumento());
                fila.createCell(5).setCellValue(usuarioItem.getDireccion());
                fila.createCell(6).setCellValue(usuarioItem.getEmail());
                fila.createCell(7).setCellValue(usuarioItem.getCelular());
                fila.createCell(8).setCellValue(usuarioItem.getFechaRegistro().toString());
                fila.createCell(9).setCellValue(usuarioItem.getUsuario());
                if (usuarioItem.isActivo()) {
                    fila.createCell(10).setCellValue("Activo");
                } else {
                    fila.createCell(10).setCellValue("Inactivo");
                }
            }

            for (int i = 0; i < listCabecera.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // ################ LIBROS
    public static ByteArrayInputStream generarExcelLibros(String rol, String titulo, List<Libro> libros)
            throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(titulo);
            int rowNum = 0;
            int cellNum = 0;
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setFontName("Serif");
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            font.setItalic(false);
            CellStyle style = workbook.createCellStyle();
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.index);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFont(font);
            ArrayList<String> listCabecera = new ArrayList<>();
            listCabecera.add("Id");
            listCabecera.add("Titulo");
            listCabecera.add("Autor");
            listCabecera.add("Categoría");
            if (rol.equals("ROLE_SYSADMIN")) {
                listCabecera.add("Local");
            } else {
                listCabecera.add("Fecha Publicación");
                listCabecera.add("Fecha Registro");
            }
            listCabecera.add("Stock");
            listCabecera.add("Estado");
            Row cabecera = sheet.createRow(rowNum);
            for (String cabeceraItem : listCabecera) {
                Cell cell = cabecera.createCell(cellNum++);
                cell.setCellValue(cabeceraItem);
                if (rowNum == 0) {
                    cell.setCellStyle(style);
                }
            }
            rowNum = 1;
            for (Libro libroItem : libros) {
                Row fila = sheet.createRow(rowNum++);
                fila.createCell(0).setCellValue(libroItem.getId());
                fila.createCell(1).setCellValue(libroItem.getTitulo());
                fila.createCell(2).setCellValue(libroItem.getAutor());
                fila.createCell(3).setCellValue(libroItem.getCategoria().getNombre());
                if (rol.equals("ROLE_SYSADMIN")) {
                    fila.createCell(4).setCellValue(libroItem.getLocal().getDireccion());
                    fila.createCell(5).setCellValue(libroItem.getStock());
                    if (libroItem.isActivo()) {
                        fila.createCell(6).setCellValue("Activo");
                    } else {
                        fila.createCell(6).setCellValue("Inactivo");
                    }
                } else {
                    fila.createCell(4).setCellValue(libroItem.getFechaPublicacion().toString());
                    fila.createCell(5).setCellValue(libroItem.getFechaRegistro().toString());
                    fila.createCell(6).setCellValue(libroItem.getStock());
                    if (libroItem.isActivo()) {
                        fila.createCell(7).setCellValue("Activo");
                    } else {
                        fila.createCell(7).setCellValue("Inactivo");
                    }
                }
            }
            for (int i = 0; i < listCabecera.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}