package com.seohalabs.moduerp.organization.infrastructure.initializer;

import com.seohalabs.moduerp.organization.domain.model.employee.EmployeeEntity;
import com.seohalabs.moduerp.organization.domain.model.employee.EmployeeFactory;
import com.seohalabs.moduerp.organization.infrastructure.keycloak.KeycloakAccountClient;
import com.seohalabs.moduerp.organization.infrastructure.openfga.OpenFgaTupleService;
import com.seohalabs.moduerp.organization.infrastructure.persistence.EmployeeRepository;
import com.seohalabs.moduerp.organization.infrastructure.persistence.RoleRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final AdminProperties adminProperties;
  private final KeycloakAccountClient keycloakAccountClient;
  private final EmployeeRepository employeeRepository;
  private final RoleRepository roleRepository;
  private final OpenFgaTupleService tupleService;

  @Value("${openfga.store-id:}")
  private String storeId;

  @Value("${openfga.authorization-model-id:}")
  private String modelId;

  @Override
  public void run(ApplicationArguments args) {
    if (storeId.isBlank() || modelId.isBlank()) {
      return;
    }
    ensureAdmin();
  }

  private void ensureAdmin() {
    String keycloakId = resolveKeycloakId();
    Boolean exists = employeeRepository.existsByKeycloakId(keycloakId).block();
    if (Boolean.TRUE.equals(exists)) {
      return;
    }
    createAdminEmployee(keycloakId);
  }

  private String resolveKeycloakId() {
    return keycloakAccountClient
        .findByUsername(adminProperties.username())
        .orElseGet(this::createKeycloakAdmin);
  }

  private String createKeycloakAdmin() {
    return keycloakAccountClient.createWithUsername(
        adminProperties.username(),
        adminProperties.password(),
        Set.of(DefaultRoleInitializer.ADMIN));
  }

  private void createAdminEmployee(String keycloakId) {
    Long adminRoleId = fetchAdminRoleId();
    EmployeeEntity admin = buildAdminEmployee(keycloakId, adminRoleId);
    EmployeeEntity saved = employeeRepository.save(admin).block();
    tupleService.writeEmployeeRegistration(
        keycloakId, saved.getId(), Set.of(DefaultRoleInitializer.ADMIN)).block();
  }

  private Long fetchAdminRoleId() {
    return roleRepository.findByName(DefaultRoleInitializer.ADMIN)
        .blockOptional()
        .orElseThrow(() -> new IllegalStateException("ADMIN role not found"))
        .getId();
  }

  private EmployeeEntity buildAdminEmployee(String keycloakId, Long adminRoleId) {
    EmployeeEntity admin = EmployeeFactory.create(
        adminProperties.username(),
        adminProperties.username() + "@modu-erp.local",
        null, null,
        new Long[]{adminRoleId});
    admin.assignKeycloakId(keycloakId);
    return admin;
  }
}
