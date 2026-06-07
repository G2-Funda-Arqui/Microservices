package pe.edu.upc.medibridge.reportsanalytics.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

@Getter
@Entity
@Table(name = "report_sections")
@NoArgsConstructor
public class ReportSection extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Integer displayOrder;

    public ReportSection(String title, String content, Integer displayOrder) {
        this.title = title;
        this.content = content;
        this.displayOrder = displayOrder;
    }
}
