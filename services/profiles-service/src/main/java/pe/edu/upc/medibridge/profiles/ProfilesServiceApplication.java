package pe.edu.upc.medibridge.profiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients(basePackages = "pe.edu.upc.medibridge.profiles.infrastructure.acl")
@SpringBootApplication(scanBasePackages = "pe.edu.upc.medibridge")
public class ProfilesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProfilesServiceApplication.class, args);
    }
}