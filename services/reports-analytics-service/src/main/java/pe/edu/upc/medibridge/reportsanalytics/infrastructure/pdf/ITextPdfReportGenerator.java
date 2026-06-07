package pe.edu.upc.medibridge.reportsanalytics.infrastructure.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates.ClinicalReport;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.entities.ReportSection;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions.ReportGenerationException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Component
public class ITextPdfReportGenerator {
    private static final Color PRIMARY_COLOR = new Color(28, 55, 84);
    private static final Color SECONDARY_COLOR = new Color(233, 239, 245);
    private static final Color BORDER_COLOR = new Color(180, 190, 200);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generate(ClinicalReport report) {
        try {
            var outputStream = new ByteArrayOutputStream();
            var document = new Document(PageSize.A4, 42, 42, 42, 42);
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addHeader(document);
            addReportMetadata(document, report);
            addSummary(document, report);
            addSections(document, report);
            addClosingNotes(document);
            document.close();

            return outputStream.toByteArray();
        } catch (DocumentException exception) {
            throw new ReportGenerationException("Unable to generate clinical report PDF", exception);
        } catch (RuntimeException exception) {
            throw new ReportGenerationException("Unable to generate clinical report PDF", exception);
        }
    }

    private void addHeader(Document document) throws DocumentException {
        Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Font documentTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Color.BLACK);

        var brand = new Paragraph("MediBridge", brandFont);
        brand.setAlignment(Element.ALIGN_CENTER);
        brand.setSpacingAfter(2);
        document.add(brand);

        var subtitle = new Paragraph("Integrated care management platform", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(14);
        document.add(subtitle);

        var title = new Paragraph("Formal Clinical Report", documentTitleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(18);
        document.add(title);
    }

    private void addReportMetadata(Document document, ClinicalReport report) throws DocumentException {
        addSectionTitle(document, "Report Identification");

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        var table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{28, 72});
        table.setSpacingAfter(16);

        addMetadataRow(table, "Report code", valueOrPending(report.getId()), headerFont, bodyFont);
        addMetadataRow(table, "Patient reference", valueOrPending(report.getPatientId()), headerFont, bodyFont);
        addMetadataRow(table, "Report type", report.getReportType().name(), headerFont, bodyFont);
        addMetadataRow(table, "Evaluation period", formatDate(report.getPeriodStartDate()) + " to " + formatDate(report.getPeriodEndDate()), headerFont, bodyFont);
        addMetadataRow(table, "Generated at", formatDateTime(report.getGeneratedAt()), headerFont, bodyFont);
        addMetadataRow(table, "PDF registry path", valueOrPending(report.getPdfPath()), headerFont, bodyFont);

        document.add(table);
    }

    private void addMetadataRow(PdfPTable table, String label, String value, Font headerFont, Font bodyFont) {
        var labelCell = new PdfPCell(new Phrase(label, headerFont));
        labelCell.setBackgroundColor(PRIMARY_COLOR);
        labelCell.setPadding(8);
        labelCell.setBorderColor(BORDER_COLOR);
        table.addCell(labelCell);

        var valueCell = new PdfPCell(new Phrase(value, bodyFont));
        valueCell.setPadding(8);
        valueCell.setBorderColor(BORDER_COLOR);
        table.addCell(valueCell);
    }

    private void addSummary(Document document, ClinicalReport report) throws DocumentException {
        addSectionTitle(document, "Executive Summary");

        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        var summary = new Paragraph(report.getSummary(), bodyFont);
        summary.setSpacingAfter(14);
        summary.setLeading(14);
        document.add(summary);
    }

    private void addSections(Document document, ClinicalReport report) throws DocumentException {
        addSectionTitle(document, "Clinical Detail");

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        var table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{8, 24, 68});
        table.setSpacingAfter(16);

        addTableHeader(table, "Order", headerFont);
        addTableHeader(table, "Section", headerFont);
        addTableHeader(table, "Detail", headerFont);

        report.getSections().stream()
                .sorted(Comparator.comparing(ReportSection::getDisplayOrder))
                .forEach(section -> {
                    addTableBodyCell(table, String.valueOf(section.getDisplayOrder()), bodyFont);
                    addTableBodyCell(table, section.getTitle(), bodyFont);
                    addTableBodyCell(table, section.getContent(), bodyFont);
                });

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String label, Font font) {
        var cell = new PdfPCell(new Phrase(label, font));
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }

    private void addTableBodyCell(PdfPTable table, String value, Font font) {
        var cell = new PdfPCell(new Phrase(value, font));
        cell.setPadding(8);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR);
        var paragraph = new Paragraph(title, sectionTitleFont);
        paragraph.setSpacingBefore(8);
        paragraph.setSpacingAfter(8);
        document.add(paragraph);
    }

    private void addClosingNotes(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, PRIMARY_COLOR);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);

        var table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(8);

        var titleCell = new PdfPCell(new Phrase("Clinical Notes", titleFont));
        titleCell.setBackgroundColor(SECONDARY_COLOR);
        titleCell.setPadding(8);
        titleCell.setBorderColor(BORDER_COLOR);
        table.addCell(titleCell);

        var note = "This report consolidates information available in MediBridge at the generation date. "
                + "It should be reviewed by authorized healthcare staff before clinical decision-making.";
        var noteCell = new PdfPCell(new Phrase(note, bodyFont));
        noteCell.setPadding(8);
        noteCell.setBorderColor(BORDER_COLOR);
        table.addCell(noteCell);

        document.add(table);
    }

    private String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return "Not registered";
        }
        return date.format(DATE_FORMAT);
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Not registered";
        }
        return dateTime.format(DATE_TIME_FORMAT);
    }

    private String valueOrPending(Object value) {
        if (value == null) {
            return "Not registered";
        }
        var text = String.valueOf(value);
        return text.isBlank() ? "Not registered" : text;
    }
}
