package com.seohalabs.moduerp.organization.shared.infrastructure.keycloak;

import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakAccountClient {

  private final Keycloak keycloak;
  private final KeycloakProperties properties;

  public CreatedKeycloakAccount createEmployee(String email, String name, Set<String> roleNames) {
    String userId = create(email, name, roleNames);
    String password = generateTemporaryPassword();
    setTemporaryPassword(userId, password);
    return new CreatedKeycloakAccount(userId, password);
  }

  private String create(String email, String name, Set<String> roleNames) {
    Response response = realm().users().create(buildEmailUser(email, name));
    String userId = CreatedResponseUtil.getCreatedId(response);
    assignRoles(userId, roleNames);
    return userId;
  }

  private void setTemporaryPassword(String userId, String password) {
    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(password);
    credential.setTemporary(true);
    realm().users().get(userId).resetPassword(credential);
  }

  private String generateTemporaryPassword() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
  }

  public String createWithUsername(String username, String password, Set<String> roleNames) {
    Response response = realm().users().create(buildUsernameUser(username));
    String userId = CreatedResponseUtil.getCreatedId(response);
    setPassword(userId, password);
    assignRoles(userId, roleNames);
    return userId;
  }

  public void setPassword(String keycloakId, String password) {
    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(password);
    credential.setTemporary(false);
    realm().users().get(keycloakId).resetPassword(credential);
  }

  public Optional<String> findByUsername(String username) {
    List<UserRepresentation> users = realm().users().searchByUsername(username, true);
    return users.stream().map(UserRepresentation::getId).findFirst();
  }

  public void disable(String keycloakId) {
    UserRepresentation user = new UserRepresentation();
    user.setEnabled(false);
    realm().users().get(keycloakId).update(user);
  }

  private RealmResource realm() {
    return keycloak.realm(properties.realm());
  }

  private UserRepresentation buildEmailUser(String email, String name) {
    UserRepresentation user = new UserRepresentation();
    user.setEmail(email);
    user.setUsername(email);
    user.setFirstName(name);
    user.setLastName(name);
    user.setEnabled(true);
    return user;
  }

  private UserRepresentation buildUsernameUser(String username) {
    UserRepresentation user = new UserRepresentation();
    user.setUsername(username);
    user.setEmail(username + "@modu-erp.local");
    user.setFirstName(username);
    user.setLastName(username);
    user.setEnabled(true);
    return user;
  }

  private void assignRoles(String userId, Set<String> roleNames) {
    List<RoleRepresentation> roles =
        roleNames.stream().map(n -> realm().roles().get(n).toRepresentation()).toList();
    realm().users().get(userId).roles().realmLevel().add(roles);
  }
}
