package pe.edu.upc.medibridge.medicationmanagement.domain.model.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Integer medicationId) {
        super("Insufficient stock for medication: " + medicationId);
    }
}
