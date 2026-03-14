package com.seohalabs.moduerp.organization.shared.infrastructure.bootstrap;

import com.seohalabs.moduerp.organization.role.domain.RoleFactory;
import com.seohalabs.moduerp.organization.role.infrastructure.persistence.RoleRepository;
import com.seohalabs.moduerp.organization.shared.infrastructure.keycloak.KeycloakRoleClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@RequiredArgsConstructor
public class DefaultRoleInitializer implements ApplicationRunner {

  static final String ADMIN = "ADMIN";
  static final String EMPLOYEE = "EMPLOYEE";

  private static final List<String> DEFAULT_ROLES = List.of(ADMIN, EMPLOYEE);

  private final RoleRepository roleRepository;
  private final KeycloakRoleClient keycloakRoleClient;

  @Override
  public void run(ApplicationArguments args) {
    DEFAULT_ROLES.forEach(this::createIfAbsent);
  }

  private void createIfAbsent(String roleName) {
    Boolean exists = roleRepository.existsByName(roleName).block();
    if (Boolean.TRUE.equals(exists)) {
      ensureKeycloakRole(roleName);
      return;
    }
    createRole(roleName);
  }

  private void createRole(String roleName) {
    roleRepository.save(RoleFactory.create(roleName, roleName + " role")).block();
    ensureKeycloakRole(roleName);
  }

  private void ensureKeycloakRole(String roleName) {
    if (keycloakRoleClient.existsByName(roleName)) {
      return;
    }
    keycloakRoleClient.create(roleName, roleName + " role");
  }
}
