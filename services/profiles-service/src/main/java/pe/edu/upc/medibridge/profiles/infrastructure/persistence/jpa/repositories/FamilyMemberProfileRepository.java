package pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyMemberProfile;

@Repository
public interface FamilyMemberProfileRepository extends JpaRepository<FamilyMemberProfile, Long> {
    boolean existsByUserId(Long userId);
}
