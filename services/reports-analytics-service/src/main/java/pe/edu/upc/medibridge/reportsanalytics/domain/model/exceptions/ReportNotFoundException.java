package pe.edu.upc.medibridge.reportsanalytics.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(Integer reportId) {
        super("Clinical report not found with id: " + reportId);
    }
}
