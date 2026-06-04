package pe.edu.upc.medibridge.iam.domain.services;

public interface TokenService {
    String generateToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}
