package pe.edu.upc.medibridge.iam.domain.services;

public interface HashingService {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
