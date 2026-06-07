package pe.edu.upc.medibridge.medicationmanagement.application.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.commands.TriggerReplenishmentAlertCommand;
import pe.edu.upc.medibridge.medicationmanagement.domain.model.entities.ReplenishmentAlert;
import pe.edu.upc.medibridge.medicationmanagement.infrastructure.persistence.jpa.repositories.ReplenishmentAlertRepository;

@Service
public class ReplenishmentAlertCommandServiceImpl {
    private final ReplenishmentAlertRepository replenishmentAlertRepository;

    public ReplenishmentAlertCommandServiceImpl(ReplenishmentAlertRepository replenishmentAlertRepository) {
        this.replenishmentAlertRepository = replenishmentAlertRepository;
    }

    public ReplenishmentAlert handle(TriggerReplenishmentAlertCommand command) {
        return replenishmentAlertRepository.save(new ReplenishmentAlert(
                command.medicationId(),
                command.patientId(),
                command.currentStock()));
    }
}
