package pe.edu.upc.medibridge.payments.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.upc.medibridge.payments.domain.model.commands.AddPaymentMethodCommand;
import pe.edu.upc.medibridge.shared.domain.model.entities.AuditableModel;

@Getter
@Entity
@Table(name = "payment_methods")
@NoArgsConstructor
public class PaymentMethod extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 30)
    private String brand;

    @Column(nullable = false, length = 4)
    private String lastFourDigits;

    @Column(nullable = false, unique = true, length = 120)
    private String stripePaymentMethodId;

    @Column(nullable = false)
    private boolean active;

    public PaymentMethod(AddPaymentMethodCommand command) {
        this.userId = command.userId();
        this.brand = command.brand();
        this.lastFourDigits = command.lastFourDigits();
        this.stripePaymentMethodId = command.stripePaymentMethodId();
        this.active = true;
    }
}
