package com.seohalabs.moduerp.organization.infrastructure.keycloak;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakRoleClient {

  private final Keycloak keycloak;
  private final KeycloakProperties properties;

  public void create(String name, String description) {
    RoleRepresentation role = new RoleRepresentation();
    role.setName(name);
    role.setDescription(description);
    keycloak.realm(properties.realm()).roles().create(role);
  }

  public boolean existsByName(String name) {
    try {
      keycloak.realm(properties.realm()).roles().get(name).toRepresentation();
      return true;
    } catch (NotFoundException e) {
      return false;
    }
  }
}
