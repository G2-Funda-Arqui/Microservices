package pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.payments.domain.model.aggregates.Subscription;
import pe.edu.upc.medibridge.payments.domain.model.valueobjects.SubscriptionStatus;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);
}
