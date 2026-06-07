package pe.edu.upc.medibridge.reportsanalytics.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.commands.GenerateClinicalReportCommand;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.entities.ReportSection;
import pe.edu.upc.medibridge.reportsanalytics.domain.model.valueobjects.ReportType;
import pe.edu.upc.medibridge.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "clinical_reports")
@NoArgsConstructor
public class ClinicalReport extends AuditableAbstractAggregateRoot<ClinicalReport> {
    @Column(nullable = false)
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportType reportType;

    @Column(nullable = false)
    private LocalDate periodStartDate;

    @Column(nullable = false)
    private LocalDate periodEndDate;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(length = 500)
    private String pdfPath;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clinical_report_id")
    private List<ReportSection> sections = new ArrayList<>();

    public ClinicalReport(GenerateClinicalReportCommand command, String summary) {
        this.patientId = command.patientId();
        this.reportType = command.reportType();
        this.periodStartDate = command.startDate();
        this.periodEndDate = command.endDate();
        this.generatedAt = LocalDateTime.now();
        this.summary = summary;
    }

    public void addSection(ReportSection section) {
        this.sections.add(section);
    }

    public void attachPdf(String pdfPath) {
        this.pdfPath = pdfPath;
    }
}
