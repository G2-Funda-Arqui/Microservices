package pe.edu.upc.medibridge.iam.domain.model.valueobjects;

public record UserId(Long value) {
    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
    }
}
