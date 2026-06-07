package pe.edu.upc.medibridge.payments.domain.model.valueobjects;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        Currency.getInstance(currency);
    }
}
