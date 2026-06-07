package pe.edu.upc.medibridge.appointments.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.appointments.domain.model.entities.FamilyPatientRelation;

@Repository
public interface FamilyPatientRelationRepository extends JpaRepository<FamilyPatientRelation, Long> {
    boolean existsByLinkId(Long linkId);
    boolean existsByFamilyMemberProfileIdAndPatientIdAndActiveTrue(Long familyMemberProfileId, Long patientId);
}
