package pe.edu.upc.medibridge.payments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.payments.domain.model.entities.UserReference;

@Repository
public interface UserReferenceRepository extends JpaRepository<UserReference, Long> {
    boolean existsByUserId(Long userId);
}
