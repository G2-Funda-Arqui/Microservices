package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

import java.time.LocalDate;

public record ExpirationDate(LocalDate value) {
    public ExpirationDate {
        if (value == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }
    }

    public boolean isExpired() {
        return value.isBefore(LocalDate.now());
    }
}
