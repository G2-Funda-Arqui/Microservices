package pe.edu.upc.medibridge.iam.domain.model.valueobjects;

public record RoleName(Roles value) {
    public RoleName {
        if (value == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
    }

    public String name() {
        return value.name();
    }
}
