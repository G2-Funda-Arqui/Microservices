package pe.edu.upc.medibridge.healthmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication(scanBasePackages = "pe.edu.upc.medibridge")
public class HealthMonitoringServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthMonitoringServiceApplication.class, args);
    }
}
