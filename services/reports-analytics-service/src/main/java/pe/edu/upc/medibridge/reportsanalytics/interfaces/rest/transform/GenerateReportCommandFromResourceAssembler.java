package pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.transform;

import pe.edu.upc.medibridge.reportsanalytics.domain.model.commands.GenerateClinicalReportCommand;
import pe.edu.upc.medibridge.reportsanalytics.interfaces.rest.resources.GenerateReportRequest;

public class GenerateReportCommandFromResourceAssembler {
    public static GenerateClinicalReportCommand toCommandFromResource(GenerateReportRequest resource, Long requestedByUserId) {
        return new GenerateClinicalReportCommand(
                resource.patientId(),
                resource.reportType(),
                resource.startDate(),
                resource.endDate(),
                requestedByUserId);
    }
}
