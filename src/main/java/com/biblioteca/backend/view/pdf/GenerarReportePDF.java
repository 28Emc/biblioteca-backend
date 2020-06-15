package com.biblioteca.backend.view.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.StyleConstants.FontConstants;
import com.biblioteca.backend.model.Libro;
import com.biblioteca.backend.model.Prestamo;
import com.biblioteca.backend.model.Usuario;
import com.itextpdf.io.font.constants.*;
import com.itextpdf.kernel.colors.Color; 
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.svg.converter.SvgConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerarReportePDF {

    public String svg = "<svg id='Logo'xmlns='http://www.w3.org/2000/svg'width='119.91'height='85.65'viewBox='0 0 2100 1500'><defs><style>.cls-1{fill:#ffd300;}.cls-1,.cls-2,.cls-3,.cls-4,.cls-5{fill-rule:evenodd;}.cls-2{fill:#751ebb;}.cls-3{fill:#008e2c;}.cls-4{fill:#006cb7;}.cls-5{fill:#ff451a;}.cls-6{font-size:214.873px;font-family:'Segoe UI Emoji';}</style></defs><g id='LibroAmarillo'><path id='LibroAmarillo_-_interior'data-name='LibroAmarillo - interior'class='cls-1'd='M668,1146s-0.628,7,3,7h7s2-.39,2-6,2-175,2-175-0.128-4-3-4h-7s-3.628-.394-4,2S668,1146,668,1146Zm-43,0s-0.628,7,3,7h7s2-.39,2-6,2-175,2-175-0.128-4-3-4h-7s-3.628-.394-4,2S625,1146,625,1146Zm20,0s-0.628,7,3,7h7s2-.39,2-6,2-175,2-175-0.128-4-3-4h-7s-3.628-.394-4,2S645,1146,645,1146Z'/><path id='LibroAmarillo_-_exterior'data-name='LibroAmarillo - exterior'class='cls-1'd='M609,1154s-0.128,7-3,7H587s-6-.89-6-7V952s-0.128-40,41-40,62-2,62-2,40,1.106,40,40,1,204,1,204-0.628,8-5,8H701a7.185,7.185,0,0,1-5-7V964s-0.628-26-22-26H631s-22,.606-22,22v194Z'/></g><g id='LibroVioleta'><path id='LibroVioleta_-_interior'data-name='LibroVioleta - interior'class='cls-2'd='M321,1027s-2.378-3.62,0-6S450,892,450,892s1.622-3.378,5,0l7,7s2.122,2.878,0,5L332,1034s-2.378,1.62-5-1S321,1027,321,1027Zm1-8c2.378-2.38,129-129,129-129M322,1019c2.378-2.38,129-129,129-129M336,1044s-2.378-3.62,0-6S465,909,465,909s1.622-3.378,5,0l7,7s2.122,2.878,0,5L347,1051s-2.378,1.62-5-1S336,1044,336,1044Zm17,17s-2.378-3.62,0-6S482,926,482,926s1.622-3.378,5,0l7,7s2.122,2.878,0,5L364,1068s-2.378,1.62-5-1S353,1061,353,1061Z'/><path id='LibroVioleta_-_exterior'data-name='LibroVioleta - exterior'class='cls-2'd='M282,994s-1.771,7.02,3,12c5.628,5.88,12,13,12,13s6.122,3.88,12-2S446,882,446,882s21.356-17.644,39,0,23,21,23,21,17.644,18.356,1,35-138,137-138,137-5.644,6.36-1,11,13,13,13,13,5.644,5.36,11,0S544,951,544,951s24.622-29.378,0-54-51-50-51-50-32.856-31.144-63-1Z'/></g><g id='LibroVerde'><path id='LibroVerde_-_exterior'data-name='LibroVerde - exterior'class='cls-3'd='M232,705c-6.219,0-6-6-6-6V680c0-3.568,6-4,6-4l210,2s36,0.932,36,35,1,70,1,70-0.219,37-37,37H232s-6-1.568-6-7V797s-0.219-5,5-5,192-1,192-1,26-1.568,26-25V727s-1.719-22-28-22H232Z'/><path id='LibroVerde_-_interior'data-name='LibroVerde - interior'class='cls-3'd='M251,719H415s6,0.432,6,4v6s-2.719,4-3,4c-6.281,0-178-1-178-1s-5,.432-5-3v-7s0.281-3,5-3h11Zm0,44H415s6,0.432,6,4v6s-2.719,4-3,4c-6.281,0-178-1-178-1s-5,.432-5-3v-7s0.281-3,5-3h11Zm0-21H415s6,0.432,6,4v6s-2.719,4-3,4c-6.281,0-178-1-178-1s-5,.432-5-3v-7s0.281-3,5-3h11Z'/></g><g id='LibroAzul'><path id='LibroAzul_-_exterior'data-name='LibroAzul - exterior'class='cls-4'd='M369,423s-5.062-4.938,0-10l11-11s6.843-5.157,11-1S542,550,542,550s23.438,29.562,1,52-51,53-51,53-37.157,26.843-64,0S279,504,279,504s-4.343-6.657,1-12,12-13,12-13,5.843-4.157,12,2L441,618s21.157,19.843,37,4l30-30s15.438-12.562-2-30S369,423,369,423Z'/><path id='LibroAzul_-_interior'data-name='LibroAzul - interior'class='cls-4'd='M317,481s-3.062-3.938,0-7a74.866,74.866,0,0,0,6-7s2.438-1.562,5,1S458,596,458,596s2.438,2.562,0,5l-8,8s-3.657,2.343-6,0S317,481,317,481Zm35-34s-3.062-3.938,0-7a74.866,74.866,0,0,0,6-7s2.438-1.562,5,1S493,562,493,562s2.438,2.562,0,5l-8,8s-3.657,2.343-6,0S352,447,352,447Zm-17,17s-3.062-3.938,0-7a74.866,74.866,0,0,0,6-7s2.438-1.562,5,1S476,579,476,579s2.438,2.562,0,5l-8,8s-3.657,2.343-6,0S335,464,335,464Z'/></g><g id='LibroNaranja'><path id='LibroNaranja_-_exterior'data-name='LibroNaranja - exterior'class='cls-5'd='M694,346s-0.9-8,7-8h16s5,0.519,5,8,1,203,1,203-1.237,41-40,41H619s-40-.814-40-41-1-204-1-204,0.1-6,7-6h16s6,0.186,6,4,1,195,1,195,0.43,23,21,23h42s24-.481,24-22S694,346,694,346Z'/><path id='LibroNaranja_-_interior'data-name='LibroNaranja - interior'class='cls-5'd='M622,351s0.483-5,4-5h6s3,0.193,3,3,1,179,1,179-0.183,4-2,4h-9s-3-1.474-3-4V351Zm21,0s0.483-5,4-5h6s3,0.193,3,3,1,179,1,179-0.183,4-2,4h-9s-3-1.474-3-4V351Zm21,0s0.483-5,4-5h6s3,0.193,3,3,1,179,1,179-0.183,4-2,4h-9s-3-1.474-3-4V351Z'/></g><text id='Biblioteca'class='cls-6'x='725'y='823'>Biblioteca</text></svg>";

    // ################ PRESTAMOS
    public static ByteArrayInputStream generarPDFPrestamos(String titulo, List<Prestamo> prestamos) throws Exception {
        // INICIO A ARMAR MI DOCUMENTO PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Creating a PdfWriter
		//String dest = "C:/Users/Edi/Desktop/pdf/sample.pdf";
		PdfWriter writer = new PdfWriter(out);
		// Creating a PdfDocument
		PdfDocument pdfDoc = new PdfDocument(writer);
		// Adding a new page
		pdfDoc.addNewPage();
		// Creating a Document
		Document document = new Document(pdfDoc);
		document.setMargins(0f, 0f, 0f, 0f);
		// Obtener svg de archivo y convertirlo en imagen apta para iText
		URL svgUrl = new File("src/main/resources/static/img/LogoVectorizado.svg").toURI().toURL();
		Image image = SvgConverter.convertToImage(svgUrl.openStream(), pdfDoc).setAutoScale(true);
		// Tabla cabecera
		Table tablaCabecera = new Table(new float[] { 150, 450 });
		Text textCabecera = new Text("Reporte mensual de préstamos");
		Style styleCabecera = new Style().setFontSize(22).setMarginLeft(20).setFontColor(new DeviceRgb(255, 255, 255));
		textCabecera.addStyle(styleCabecera);
		Paragraph paragraphCabecera = new Paragraph().add(textCabecera);
		Cell cellCabeceraImagen = new Cell().add(image).setBorder(Border.NO_BORDER);
		Cell cellCabeceraTitulo = new Cell().add(paragraphCabecera).setBorder(Border.NO_BORDER)
				.setBackgroundColor(new DeviceRgb(0, 102, 153)).setVerticalAlignment(VerticalAlignment.MIDDLE);
		tablaCabecera.addCell(cellCabeceraImagen);
		tablaCabecera.addCell(cellCabeceraTitulo);
		document.add(tablaCabecera);
		// Tabla cuerpo
		Table tablaCuerpo = new Table(new float[] { 1, 2.8f, 3, 3, 2.5f, 2.5f, 2.5f });
		tablaCuerpo.setMargins(10f, 10f, 10f, 10f);
        Style styleCuerpo = new Style().setFontColor(new DeviceRgb(255, 255, 255));
        Style styleCuerpo2 = new Style().setFontColor(new DeviceRgb(0, 0, 0));
		Cell cellCuerpo = null;
		// CREANDO CABECERA
		ArrayList<String> listCabecera = new ArrayList<>();
		listCabecera.add("Id");
		listCabecera.add("Libro");
		listCabecera.add("Empleado");
		listCabecera.add("Usuario");
		listCabecera.add("F. Despacho");
		listCabecera.add("F. Devolución");
		listCabecera.add("Estado");
		for (String cabeceraItem : listCabecera) {
			Text textCuerpo = new Text(cabeceraItem);
			textCuerpo.addStyle(styleCuerpo);
			cellCuerpo = new Cell().add(new Paragraph().add(textCuerpo));
			cellCuerpo.setBackgroundColor(new DeviceRgb(52, 58, 64));
			cellCuerpo.setPadding(8f);
			cellCuerpo.setBorder(Border.NO_BORDER);
			cellCuerpo.setVerticalAlignment(VerticalAlignment.MIDDLE);
			tablaCuerpo.addCell(cellCuerpo);
        }
        // RECORRO MI LISTA PARA OBTENER LOS VARIOS PRESTAMOS
        for (Prestamo prestamoItem : prestamos) {
            // AGREGO LA DATA COMO TAL EN CELDAS
            // NOTA 3: REVISAR LOS CAMPOS A AGREGAR AL REPORTE
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(prestamoItem.getId().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(prestamoItem.getLibro().getTitulo()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
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
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(prestamoItem.getUsuario().getNombres() + ", " + prestamoItem.getUsuario().getApellidoMaterno() + " "
            + prestamoItem.getUsuario().getApellidoPaterno() + " ("
            + prestamoItem.getUsuario().getNroDocumento() + ")").addStyle(styleCuerpo2)));            
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(prestamoItem.getFechaDespacho().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            cellCuerpo = new Cell().add(new Paragraph().add(new Text(prestamoItem.getFechaDevolucion().toString()).addStyle(styleCuerpo2)));
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
            if (prestamoItem.isActivo()) {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("Terminado").addStyle(styleCuerpo2)));
            } else {
                cellCuerpo = new Cell().add(new Paragraph().add(new Text("No devuelto").addStyle(styleCuerpo2)));
            }
            cellCuerpo.setBorder(Border.NO_BORDER);
            cellCuerpo.setPadding(10f);
            tablaCuerpo.addCell(cellCuerpo);
        }
		document.add(tablaCuerpo);
		// Closing the document
		document.close();
          
        return new ByteArrayInputStream(out.toByteArray());
    }
/*
    // ################ LIBROS
    public static ByteArrayInputStream generarPDFLibros(String titulo, List<Libro> libros) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.setMargins(39f, 39f, 0f, 0f);
        document.open();
        document.addTitle("Biblioteca2020 || " + titulo);

        PdfPTable tablaCabecera = new PdfPTable(2);
        tablaCabecera.setWidthPercentage(120);
        tablaCabecera.setSpacingAfter(20f);
        PdfPCell cellCabecera = null;
        Font fontTitulo = new Font(new Font(Font.BOLD, 22, Font.NORMAL, new Color(255, 255, 255)));
        cellCabecera = new PdfPCell(new Phrase(titulo, fontTitulo));
        cellCabecera.setBorderWidth(0);
        cellCabecera.setNoWrap(true);
        cellCabecera.setPaddingTop(35f);
        cellCabecera.setPaddingLeft(25f);
        cellCabecera.setPaddingBottom(30f);
        cellCabecera.setBackgroundColor(new Color(0, 102, 153));
        tablaCabecera.addCell(cellCabecera);
        Image image = Image.getInstance("src/main/resources/static/img/LogoVectorizado.png");
        cellCabecera = new PdfPCell(image);
        cellCabecera.setBorderWidth(0);
        cellCabecera.setNoWrap(true);
        cellCabecera.setPaddingTop(10f);
        cellCabecera.setPaddingLeft(163f);
        cellCabecera.setPaddingBottom(20f);
        cellCabecera.setBackgroundColor(new Color(0, 102, 153));
        tablaCabecera.addCell(cellCabecera);
        document.add(tablaCabecera);

        PdfPTable tabla;
        //String role = (String) model.getAttribute("role");
        if (titulo.contains("stock") /*|| role.equals("[ROLE_SYSADMIN]")*//*) {
            tabla = new PdfPTable(7);
            tabla.setWidths(new float[] { 1, 2.5f, 2.3f, 2.3f, 3f, 1.3f, 1.8f });
        } else {
            tabla = new PdfPTable(8);
            tabla.setWidths(new float[] { 1, 2.5f, 2.3f, 2.3f, 2.5f, 2.3f, 1.3f, 1.8f });
        }
        tabla.setWidthPercentage(110);
        PdfPCell cell = null;
        Font fontCabeceraTabla = new Font(new Font(Font.BOLD, 11, Font.NORMAL, new Color(255, 255, 255)));
        Font fontCuerpoTabla = new Font(new Font(Font.BOLD, 11, Font.NORMAL, new Color(0, 0, 0)));
        ArrayList<String> listCabecera = new ArrayList<>();
        listCabecera.add("Id");
        listCabecera.add("Titulo");
        listCabecera.add("Autor");
        listCabecera.add("Categoría");
        if (titulo.contains("stock") /*|| role.equals("[ROLE_SYSADMIN]")*//*) {
            listCabecera.add("Local");
        } else {
            listCabecera.add("F. Publicación");
            listCabecera.add("F. Registro");
        }
        listCabecera.add("Stock");
        listCabecera.add("Estado");
        for (String cabeceraItem : listCabecera) {
            cell = new PdfPCell(new Phrase(cabeceraItem, fontCabeceraTabla));
            cell.setBackgroundColor(new Color(52, 58, 64));
            cell.setPadding(8f);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderWidth(0);
            tabla.addCell(cell);
        }
        for (Libro libroItem : libros) {
            cell = new PdfPCell(new Phrase(libroItem.getId().toString(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase(libroItem.getTitulo(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase(libroItem.getAutor(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase(libroItem.getCategoria().getNombre(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            if (titulo.contains("stock") /*|| role.equals("[ROLE_SYSADMIN]")*//*) {
                cell = new PdfPCell(new Phrase(libroItem.getLocal().getDireccion().toString(), fontCuerpoTabla));
                cell.setBorderWidth(0);
                cell.setPadding(10f);
                tabla.addCell(cell);
            } else {
                cell = new PdfPCell(new Phrase(libroItem.getFechaPublicacion().toString(), fontCuerpoTabla));
                cell.setBorderWidth(0);
                cell.setPadding(10f);
                tabla.addCell(cell);
                cell = new PdfPCell(new Phrase(libroItem.getFechaRegistro().toString(), fontCuerpoTabla));
                cell.setBorderWidth(0);
                cell.setPadding(10f);
                tabla.addCell(cell);
            }
            cell = new PdfPCell(new Phrase(libroItem.getStock().toString(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            if (libroItem.isActivo()) {
                cell = new PdfPCell(new Phrase("Activo", fontCuerpoTabla));
            } else {
                cell = new PdfPCell(new Phrase("Inactivo", fontCuerpoTabla));
            }
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
        }
        document.add(tabla);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
*/
    // ################ USUARIOS
    /*public static ByteArrayInputStream generarPDFUsuarios(String titulo, List<Usuario> usuarios) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.setMargins(39f, 39f, 0f, 0f);
        document.open();
        document.addTitle("Biblioteca2020 || " + titulo);

        PdfPTable tablaCabecera = new PdfPTable(2);
        tablaCabecera.setWidthPercentage(120);
        tablaCabecera.setSpacingAfter(20f);
        PdfPCell cellCabecera = null;
        Font fontTitulo = new Font(new Font(Font.BOLD, 25, Font.NORMAL, new Color(255, 255, 255)));
        cellCabecera = new PdfPCell(new Phrase(titulo, fontTitulo));
        cellCabecera.setBorderWidth(0);
        cellCabecera.setNoWrap(true);
        cellCabecera.setPaddingTop(35f);
        cellCabecera.setPaddingLeft(25f);
        cellCabecera.setPaddingBottom(30f);
        cellCabecera.setBackgroundColor(new Color(0, 102, 153));
        tablaCabecera.addCell(cellCabecera);
        Image image = Image.getInstance("src/main/resources/static/img/LogoVectorizado.png");
        cellCabecera = new PdfPCell(image);
        cellCabecera.setBorderWidth(0);
        cellCabecera.setNoWrap(true);
        cellCabecera.setPaddingTop(10f);
        cellCabecera.setPaddingLeft(163f);
        cellCabecera.setPaddingBottom(20f);
        cellCabecera.setBackgroundColor(new Color(0, 102, 153));
        tablaCabecera.addCell(cellCabecera);
        document.add(tablaCabecera);

        PdfPTable tabla = new PdfPTable(7);
        tabla.setWidths(new float[] { 1, 2.5F, 2.5F, 2, 2.5F, 2, 1.5F });
        tabla.setWidthPercentage(110);
        PdfPCell cell = null;
        Font fontCabeceraTabla = new Font(new Font(Font.BOLD, 11, Font.NORMAL, new Color(255, 255, 255)));
        Font fontCuerpoTabla = new Font(new Font(Font.BOLD, 11, Font.NORMAL, new Color(0, 0, 0)));
        ArrayList<String> listCabecera = new ArrayList<>();
        listCabecera.add("Id");
        listCabecera.add("Nombres");
        listCabecera.add("Apellidos");
        listCabecera.add("DNI");
        listCabecera.add("F. Registro");
        listCabecera.add("Username");
        listCabecera.add("Estado");
        for (String cabeceraItem : listCabecera) {
            cell = new PdfPCell(new Phrase(cabeceraItem, fontCabeceraTabla));
            cell.setBackgroundColor(new Color(52, 58, 64));
            cell.setPadding(8f);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderWidth(0);
            tabla.addCell(cell);
        }
        for (Usuario usuarioItem : usuarios) {
            cell = new PdfPCell(new Phrase(usuarioItem.getId().toString(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase(usuarioItem.getNombres(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase(usuarioItem.getApellidoMaterno() + " " + usuarioItem.getApellidoPaterno(),
                    fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase(usuarioItem.getNroDocumento(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase(usuarioItem.getFechaRegistro().toString(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase(usuarioItem.getUsuario(), fontCuerpoTabla));
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
            if (usuarioItem.isActivo()) {
                cell = new PdfPCell(new Phrase("Activo", fontCuerpoTabla));
            } else {
                cell = new PdfPCell(new Phrase("Inactivo", fontCuerpoTabla));
            }
            cell.setBorderWidth(0);
            cell.setPadding(10f);
            tabla.addCell(cell);
        }
        document.add(tabla);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
*/
}