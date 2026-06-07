package pe.edu.upc.medibridge.reportsanalytics.infrastructure.pdf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions.ReportGenerationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@Component
public class PdfReportStorage {
    private final Path storageDirectory;
    private final String configuredStoragePath;

    public PdfReportStorage(@Value("${reports.pdf.storage-path:reports}") String storagePath) {
        this.configuredStoragePath = storagePath;
        this.storageDirectory = Paths.get(storagePath).toAbsolutePath().normalize();
    }

    public String savePdf(Integer reportId, byte[] content) {
        var filename = filenameFor(reportId);
        try {
            Files.createDirectories(storageDirectory);
            Files.write(storageDirectory.resolve(filename), content, CREATE, TRUNCATE_EXISTING, WRITE);
            return normalizePath(Paths.get(configuredStoragePath).resolve(filename));
        } catch (IOException exception) {
            throw new ReportGenerationException("Unable to store clinical report PDF", exception);
        }
    }

    public Optional<byte[]> readPdf(String pdfPath) {
        if (pdfPath == null || pdfPath.isBlank()) {
            return Optional.empty();
        }

        var path = Paths.get(pdfPath);
        var resolvedPath = path.isAbsolute()
                ? path.normalize()
                : Paths.get("").toAbsolutePath().resolve(path).normalize();

        if (!Files.isRegularFile(resolvedPath)) {
            return Optional.empty();
        }

        try {
            return Optional.of(Files.readAllBytes(resolvedPath));
        } catch (IOException exception) {
            throw new ReportGenerationException("Unable to read clinical report PDF", exception);
        }
    }

    private String filenameFor(Integer reportId) {
        return "clinical-report-" + reportId + ".pdf";
    }

    private String normalizePath(Path path) {
        return path.normalize().toString().replace('\\', '/');
    }
}
