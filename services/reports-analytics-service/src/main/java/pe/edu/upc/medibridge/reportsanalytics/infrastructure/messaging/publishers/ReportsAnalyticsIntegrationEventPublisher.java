package pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.publishers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.RabbitMQConfiguration;
import pe.edu.upc.medibridge.reportsanalytics.infrastructure.messaging.events.ClinicalReportGeneratedIntegrationEvent;

@Component
public class ReportsAnalyticsIntegrationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ReportsAnalyticsIntegrationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @CircuitBreaker(name = "rabbitMqPublisher", fallbackMethod = "publishClinicalReportGeneratedFallback")
    public void publishClinicalReportGenerated(Integer reportId, Long patientId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY_CLINICAL_REPORT_GENERATED,
                new ClinicalReportGeneratedIntegrationEvent(reportId, patientId));
    }

    private void publishClinicalReportGeneratedFallback(Integer reportId, Long patientId, Throwable exception) {
        throw new IllegalStateException("RabbitMQ clinical report publishing failed", exception);
    }
}
