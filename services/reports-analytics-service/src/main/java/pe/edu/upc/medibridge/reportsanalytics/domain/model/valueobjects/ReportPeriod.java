package pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects;

import java.time.LocalDate;

public record ReportPeriod(LocalDate startDate, LocalDate endDate) {
    public ReportPeriod {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Report period dates are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Report period end date cannot be before start date");
        }
    }
}
