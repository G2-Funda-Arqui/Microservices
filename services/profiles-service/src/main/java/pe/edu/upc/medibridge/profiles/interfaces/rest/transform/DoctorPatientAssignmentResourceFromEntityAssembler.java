package pe.edu.upc.medibridge.profiles.interfaces.rest.transform;

import pe.edu.upc.medibridge.profiles.domain.model.aggregates.DoctorPatientAssignment;
import pe.edu.upc.medibridge.profiles.interfaces.rest.resources.DoctorPatientAssignmentResource;

public class DoctorPatientAssignmentResourceFromEntityAssembler {
    public static DoctorPatientAssignmentResource toResourceFromEntity(DoctorPatientAssignment entity) {
        return new DoctorPatientAssignmentResource(
                entity.getId(),
                entity.getDoctorProfileId(),
                entity.getPatientId(),
                entity.isActive());
    }
}
