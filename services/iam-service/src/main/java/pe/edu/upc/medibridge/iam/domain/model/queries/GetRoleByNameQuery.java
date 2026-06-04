package pe.edu.upc.medibridge.iam.domain.model.queries;

import pe.edu.upc.medibridge.iam.domain.model.valueobjects.Roles;

public record GetRoleByNameQuery(Roles name) {
}
