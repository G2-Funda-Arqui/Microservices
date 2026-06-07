package pe.edu.upc.medibridge.medicationmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication(scanBasePackages = "pe.edu.upc.medibridge")
public class MedicationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicationServiceApplication.class, args);
    }
}
