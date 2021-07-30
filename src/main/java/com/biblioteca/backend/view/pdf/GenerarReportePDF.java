package com.biblioteca.backend.view.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.svg.converter.SvgConverter;

public class GenerarReportePDF {

    // ################ PRESTAMOS
    public static ByteArrayInputStream generarPDFPrestamos(String titulo, List<Prestamo> prestamos)
            throws IOException, NullPointerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // PdfWriter
        PdfWriter writer = new PdfWriter(out);
        // PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        // Agregando nueva página al documento pdf
        pdfDoc.addNewPage();
        // Document plantilla que va a rellenarse en pdf
        Document document = new Document(pdfDoc);
        document.setMargins(0f, 0f, 0f, 0f);
        // Obtener svg de archivo y convertirlo en imagen
        URL svgUrl = new File("src/main/resources/static/img/LogoReporteVectorizado.svg").toURI().toURL();
        Image image = SvgConverter.convertToImage(svgUrl.openStream(), pdfDoc).setAutoScale(true);
        // Tabla cabecera y estilos
        Table tablaCabecera = new Table(new float[] { 150, 450 });
        Text textCabecera = new Text(titulo);
        Style styleCabecera = new Style().setFontSize(22).setMarginLeft(20).setFontColor(new DeviceRgb(255, 255, 255));
        textCabecera.addStyle(styleCabecera);
        Paragraph paragraphCabecera = new Paragraph().add(textCabecera);
        Cell cellCabeceraImagen = new Cell().add(image).setBorder(Border.NO_BORDER).setPadding(15f);
        Cell cellCabeceraTitulo = new Cell().add(paragraphCabecera).setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceRgb(0, 102, 153)).setVerticalAlignment(VerticalAlignment.MIDDLE);
        tablaCabecera.addCell(cellCabeceraImagen);
        tablaCabecera.addCell(cellCabeceraTitulo);
        // Agrego la cabecera al documento plantilla
        document.add(tablaCabecera);
        // Tabla cuerpo y estilos
        Table tablaCuerpo = new Table(new float[] { 1, 2.8f, 3, 3, 2.5f, 2.5f, 2.5f });
        tablaCuerpo.setMargins(10f, 10f, 10f, 10f);
        Style styleCuerpo = new Style().setFontColor(new DeviceRgb(255, 255, 255));
        Style styleCuerpo2 = new Style().setFontColor(new DeviceRgb(0, 0, 0));
        Cell cellCuerpo = null;
        // Cabecera
        ArrayList<String> listCabecera = new ArrayList<>();
        listCabecera.add("Id");
        listCabecera.add("Libro");
        listCabecera.add("Empleado");
        listCabecera.add("Usuario");
        listCabecera.add("F. Despacho");
        listCabecera.add("F. Devolución");
        listCabecera.add("Estado");
        // Por cada elemento de mi cabecera, creo una celda y la agrego a la tabla
        for (String cabeceraItem : listCabecera) {
            Text textCuerpo = new Text(cabeceraItem);
            textCuerpo.addStyle(styleCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(textCuerpo));
            cellCuerpo.setBackgroundColor(new DeviceRgb(52, 58, 64));
            cellCuerpo.setPadding(8f);
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setVerticalAlignment(VerticalAlignment.MIDDLE);
            // Agrego la tabla al documento plantilla
            tablaCuerpo.addCell(cellCuerpo);
        }
        // Iteración sobre el data source
        for (Prestamo prestamoItem : prestamos) {
            // Por cada elemento del data source, creo una celda y la agrego a la tabla con
            // su estilo
            // NOTA: REVISAR LOS CAMPOS A AGREGAR AL REPORTE
            cellCuerpo = new Cell()
                    .add(new Paragraph().add(new Text(prestamoItem.getId().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell()
                    .add(new Paragraph().add(new Text(prestamoItem.getLibro().getTitulo()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            /* TODO: REVISAR
            if (prestamoItem.getEmpleado().getUsuario().equals("prueba")) {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("No definido").addStyle(styleCuerpo2)));
            } else {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text(
                        prestamoItem.getEmpleado().getNombres() + ", " + prestamoItem.getEmpleado().getApellidoMaterno()
                                + " " + prestamoItem.getEmpleado().getApellidoPaterno()).addStyle(styleCuerpo2)));
            }
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(
                    prestamoItem.getUsuario().getNombres() + ", " + prestamoItem.getUsuario().getApellidoMaterno() + " "
                            + prestamoItem.getUsuario().getApellidoPaterno() + " ("
                            + prestamoItem.getUsuario().getDni() + ")").addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(
                    new Paragraph().add(new Text(prestamoItem.getFechaDespacho().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(
                    new Paragraph().add(new Text(prestamoItem.getFechaDevolucion().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            if (prestamoItem.isActivo()) {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("Terminado").addStyle(styleCuerpo2)));
            } else {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("No devuelto").addStyle(styleCuerpo2)));
            }*/
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
        }
        // Agrego la tabla al documento plantilla
        document.add(tablaCuerpo);
        // Cierro el documento para terminar de crear la plantilla
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // ################ LIBROS
    public static ByteArrayInputStream generarPDFLibros(String rol, String titulo, List<Libro> libros)
            throws IOException, NullPointerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // PdfWriter
        PdfWriter writer = new PdfWriter(out);
        // PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        // Agregando nueva página al documento pdf
        pdfDoc.addNewPage();
        // Document plantilla que va a rellenarse en pdf
        Document document = new Document(pdfDoc);
        document.setMargins(0f, 0f, 0f, 0f);
        // Obtener svg de archivo y convertirlo en imagen
        URL svgUrl = new File("src/main/resources/static/img/LogoReporteVectorizado.svg").toURI().toURL();
        Image image = SvgConverter.convertToImage(svgUrl.openStream(), pdfDoc).setAutoScale(true);
        // Tabla cabecera y estilos
        Table tablaCabecera = new Table(new float[] { 150, 450 });
        Text textCabecera = new Text(titulo);
        Style styleCabecera = new Style().setFontSize(22).setMarginLeft(20).setFontColor(new DeviceRgb(255, 255, 255));
        textCabecera.addStyle(styleCabecera);
        Paragraph paragraphCabecera = new Paragraph().add(textCabecera);
        Cell cellCabeceraImagen = new Cell().add(image).setBorder(Border.NO_BORDER).setPadding(15f);
        Cell cellCabeceraTitulo = new Cell().add(paragraphCabecera).setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceRgb(0, 102, 153)).setVerticalAlignment(VerticalAlignment.MIDDLE);
        tablaCabecera.addCell(cellCabeceraImagen);
        tablaCabecera.addCell(cellCabeceraTitulo);
        // Agrego la cabecera al documento plantilla
        document.add(tablaCabecera);
        // Tabla cuerpo y estilos
        Table tablaCuerpo = null;
        if (titulo.contains("stock") || rol.equals("ROLE_SYSADMIN")) {
            tablaCuerpo = new Table(new float[] { 1, 2.5f, 2.3f, 2.3f, 3f, 1.3f, 1.8f });
        } else {
            tablaCuerpo = new Table(new float[] { 1, 2.5f, 2.3f, 2.3f, 2.5f, 2.3f, 1.3f, 1.8f });
        }
        tablaCuerpo.setMargins(10f, 10f, 10f, 10f);
        Style styleCuerpo = new Style().setFontColor(new DeviceRgb(255, 255, 255));
        Style styleCuerpo2 = new Style().setFontColor(new DeviceRgb(0, 0, 0));
        Cell cellCuerpo = null;
        // Cabecera
        ArrayList<String> listCabecera = new ArrayList<>();
        listCabecera.add("Id");
        listCabecera.add("Titulo");
        listCabecera.add("Autor");
        listCabecera.add("Categoría");
        if (titulo.contains("stock") || rol.equals("ROLE_SYSADMIN")) {
            listCabecera.add("Local");
        } else {
            listCabecera.add("F. Publicación");
            listCabecera.add("F. Registro");
        }
        listCabecera.add("Stock");
        listCabecera.add("Estado");
        // Por cada elemento de mi cabecera, creo una celda y la agrego a la tabla
        for (String cabeceraItem : listCabecera) {
            Text textCuerpo = new Text(cabeceraItem);
            textCuerpo.addStyle(styleCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(textCuerpo));
            cellCuerpo.setBackgroundColor(new DeviceRgb(52, 58, 64));
            cellCuerpo.setPadding(8f);
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setVerticalAlignment(VerticalAlignment.MIDDLE);
            // Agrego la tabla al documento plantilla
            tablaCuerpo.addCell(cellCuerpo);
        }
        // Iteración sobre el data source
        for (Libro libroItem : libros) {
            // Por cada elemento del data source, creo una celda y la agrego a la tabla con
            // su estilo
            // NOTA: REVISAR LOS CAMPOS A AGREGAR AL REPORTE
            cellCuerpo = new Cell()
                    .add(new Paragraph().add(new Text(libroItem.getId().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(libroItem.getTitulo()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(libroItem.getAutor()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell()
                    .add(new Paragraph().add(new Text(libroItem.getCategoria().getNombre()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            if (titulo.contains("stock") || rol.equals("ROLE_SYSADMIN")) {
                cellCuerpo = new Cell().add(new Paragraph()
                        .add(new Text(libroItem.getLocal().getDireccion().toString()).addStyle(styleCuerpo2)));
                cellCuerpo.setBorder(Border.NO_BORDER);
                cellCuerpo.setPadding(10f);
                tablaCuerpo.addCell(cellCuerpo);
            } else {
                cellCuerpo = new Cell().add(new Paragraph()
                        .add(new Text(libroItem.getFechaPublicacion().toString()).addStyle(styleCuerpo2)));
                cellCuerpo.setBorder(Border.NO_BORDER);
                cellCuerpo.setPadding(10f);
                tablaCuerpo.addCell(cellCuerpo);
                cellCuerpo = new Cell().add(
                        new Paragraph().add(new Text(libroItem.getFechaRegistro().toString()).addStyle(styleCuerpo2)));
                cellCuerpo.setBorder(Border.NO_BORDER);
                cellCuerpo.setPadding(10f);
                tablaCuerpo.addCell(cellCuerpo);
            }
            cellCuerpo = new Cell()
                    .add(new Paragraph().add(new Text(libroItem.getStock().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            if (libroItem.isActivo()) {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("Activo").addStyle(styleCuerpo2)));
            } else {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("Inactivo").addStyle(styleCuerpo2)));
            }
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
        }
        // Agrego la tabla al documento plantilla
        document.add(tablaCuerpo);
        // Cierro el documento para terminar de crear la plantilla
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // ################ USUARIOS
    public static ByteArrayInputStream generarPDFUsuarios(String titulo, List<Usuario> usuarios)
            throws IOException, NullPointerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // PdfWriter
        PdfWriter writer = new PdfWriter(out);
        // PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        // Agregando nueva página al documento pdf
        pdfDoc.addNewPage();
        // Document plantilla que va a rellenarse en pdf
        Document document = new Document(pdfDoc);
        document.setMargins(0f, 0f, 0f, 0f);
        // Obtener svg de archivo y convertirlo en imagen
        URL svgUrl = new File("src/main/resources/static/img/LogoReporteVectorizado.svg").toURI().toURL();
        Image image = SvgConverter.convertToImage(svgUrl.openStream(), pdfDoc).setAutoScale(true);
        // Tabla cabecera y estilos
        Table tablaCabecera = new Table(new float[] { 110, 490 });
        Text textCabecera = new Text(titulo);
        Style styleCabecera = new Style().setFontSize(22).setMarginLeft(20).setFontColor(new DeviceRgb(255, 255, 255));
        textCabecera.addStyle(styleCabecera);
        Paragraph paragraphCabecera = new Paragraph().add(textCabecera);
        Cell cellCabeceraImagen = new Cell().add(image).setBorder(Border.NO_BORDER).setPadding(15f);
        Cell cellCabeceraTitulo = new Cell().add(paragraphCabecera).setBorder(Border.NO_BORDER)
                .setBackgroundColor(new DeviceRgb(0, 102, 153)).setVerticalAlignment(VerticalAlignment.MIDDLE);
        tablaCabecera.addCell(cellCabeceraImagen);
        tablaCabecera.addCell(cellCabeceraTitulo);
        // Agrego la cabecera al documento plantilla
        document.add(tablaCabecera);
        // Tabla cuerpo y estilos
        Table tablaCuerpo = new Table(new float[] { 2, 5.5f, 2, 2.5f, 2.5f, 2f });
        tablaCuerpo.setMargins(10f, 10f, 10f, 10f);
        Style styleCuerpo = new Style().setFontColor(new DeviceRgb(255, 255, 255));
        Style styleCuerpo2 = new Style().setFontColor(new DeviceRgb(0, 0, 0));
        Cell cellCuerpo = null;
        // Cabecera
        ArrayList<String> listCabecera = new ArrayList<>();
        listCabecera.add("Id");
        listCabecera.add("Nombre Completo");
        listCabecera.add("DNI");
        listCabecera.add("F. Registro");
        listCabecera.add("Username");
        listCabecera.add("Estado");
        // Por cada elemento de mi cabecera, creo una celda y la agrego a la tabla
        /* TODO: REVISAR
        for (String cabeceraItem : listCabecera) {
            Text textCuerpo = new Text(cabeceraItem);
            textCuerpo.addStyle(styleCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(textCuerpo));
            cellCuerpo.setBackgroundColor(new DeviceRgb(52, 58, 64));
            cellCuerpo.setPadding(8f);
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setVerticalAlignment(VerticalAlignment.MIDDLE);
            // Agrego la tabla al documento plantilla
            tablaCuerpo.addCell(cellCuerpo);
        }
        for (Usuario usuarioItem : usuarios) {
            cellCuerpo = new Cell()
                    .add(new Paragraph().add(new Text(usuarioItem.getId().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell()
                    .add(new Paragraph().add(new Text(usuarioItem.getNombres() + ", " + usuarioItem.getApellidoMaterno()
                            + " " + usuarioItem.getApellidoPaterno()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell()
                    .add(new Paragraph().add(new Text(usuarioItem.getDni()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(
                    new Paragraph().add(new Text(usuarioItem.getFechaRegistro().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(usuarioItem.getUsuario()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            if (usuarioItem.isActivo()) {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("Activo").addStyle(styleCuerpo2)));
            } else {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("Inactivo").addStyle(styleCuerpo2)));
            }
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
        }*/
        // Agrego la tabla al documento plantilla
        document.add(tablaCuerpo);
        // Cierro el documento para terminar de crear la plantilla
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}