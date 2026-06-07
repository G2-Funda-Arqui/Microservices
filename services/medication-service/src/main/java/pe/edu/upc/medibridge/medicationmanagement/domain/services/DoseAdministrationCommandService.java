package pe.edu.upc.medibridge.medicationmanagement.domain.services;

import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.RecordDoseAdministrationCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.SkipDoseCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.DoseAdministration;

import java.util.Optional;

public interface DoseAdministrationCommandService {
    Optional<DoseAdministration> handle(RecordDoseAdministrationCommand command);
    Optional<DoseAdministration> handle(SkipDoseCommand command);
}
