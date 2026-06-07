package pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects;

public record ReportId(Integer value) {
    public ReportId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Report id must be a positive number");
        }
    }
}
