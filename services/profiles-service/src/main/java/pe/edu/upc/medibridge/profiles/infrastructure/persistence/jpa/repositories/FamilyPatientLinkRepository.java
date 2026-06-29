package pe.edu.upc.medibridge.profiles.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.medibridge.profiles.domain.model.aggregates.FamilyPatientLink;

import java.util.List;

@Repository
public interface FamilyPatientLinkRepository extends JpaRepository<FamilyPatientLink, Long> {
    boolean existsByFamilyMemberProfileIdAndPatientIdAndActiveTrue(Long familyMemberProfileId, Long patientId);
    List<FamilyPatientLink> findAllByPatientIdAndActiveTrue(Long patientId);

    @Query("select count(distinct link.patientId) from FamilyPatientLink link where link.familyMemberProfileId = :familyMemberProfileId and link.active = true")
    long countActivePatientsByFamilyMemberProfileId(@Param("familyMemberProfileId") Long familyMemberProfileId);
}
