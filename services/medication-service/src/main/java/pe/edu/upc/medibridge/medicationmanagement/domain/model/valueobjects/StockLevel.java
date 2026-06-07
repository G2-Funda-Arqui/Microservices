package pe.edu.upc.medibridge.medicationmanagement.domain.model.valueobjects;

public record StockLevel(Integer quantity, Integer lowStockThreshold) {
    public StockLevel {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        if (lowStockThreshold == null || lowStockThreshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
    }

    public boolean isLow() {
        return quantity <= lowStockThreshold;
    }
}
