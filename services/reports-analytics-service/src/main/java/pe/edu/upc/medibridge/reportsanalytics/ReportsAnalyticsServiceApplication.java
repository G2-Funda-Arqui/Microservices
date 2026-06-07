package pe.edu.upc.medibridge.reportsanalytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients(basePackages = "pe.edu.upc.medibridge.reportsanalytics.infrastructure.acl")
@SpringBootApplication(scanBasePackages = "pe.edu.upc.medibridge")
public class ReportsAnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportsAnalyticsServiceApplication.class, args);
    }
}
