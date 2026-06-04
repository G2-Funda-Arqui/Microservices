package pe.edu.upc.medibridge.iam.domain.model.valueobjects;

public record JwtToken(String value) {
    public JwtToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("JWT token cannot be blank");
        }
    }
}
