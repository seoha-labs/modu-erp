package com.seohalabs.moduerp.organization.employee.application;

import com.seohalabs.moduerp.organization.employee.domain.EmployeeEntity;
import com.seohalabs.moduerp.organization.employee.domain.EmployeeFactory;
import com.seohalabs.moduerp.organization.employee.infrastructure.persistence.EmployeeRepository;
import com.seohalabs.moduerp.organization.role.domain.RoleEntity;
import com.seohalabs.moduerp.organization.role.infrastructure.persistence.RoleRepository;
import com.seohalabs.moduerp.organization.shared.infrastructure.keycloak.CreatedKeycloakAccount;
import com.seohalabs.moduerp.organization.shared.infrastructure.keycloak.KeycloakAccountClient;
import com.seohalabs.moduerp.organization.shared.infrastructure.openfga.OpenFgaTupleService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterEmployeeService {

  private final EmployeeRepository employeeRepository;
  private final RoleRepository roleRepository;
  private final KeycloakAccountClient keycloakAccountClient;
  private final OpenFgaTupleService tupleService;

  public Mono<RegisterEmployeeResult> handle(RegisterEmployeeCommand command) {
    return roleRepository
        .findAllById(command.roleIds())
        .collectList()
        .flatMap(roles -> registerWithRoles(command, roles));
  }

  private Mono<RegisterEmployeeResult> registerWithRoles(
      RegisterEmployeeCommand command, List<RoleEntity> roles) {
    return createKeycloakAccount(command, roleNames(roles))
        .flatMap(account -> saveAndRegister(command, account, roles));
  }

  private Mono<CreatedKeycloakAccount> createKeycloakAccount(
      RegisterEmployeeCommand command, Set<String> roleNames) {
    return Mono.fromCallable(
            () -> keycloakAccountClient.createEmployee(command.email(), command.name(), roleNames))
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<RegisterEmployeeResult> saveAndRegister(
      RegisterEmployeeCommand command, CreatedKeycloakAccount account, List<RoleEntity> roles) {
    EmployeeEntity employee = buildEmployee(command, account.keycloakId());
    return employeeRepository
        .save(employee)
        .flatMap(saved -> writeFgaTuples(saved, roleNames(roles)))
        .map(id -> new RegisterEmployeeResult(id, account.temporaryPassword()));
  }

  private EmployeeEntity buildEmployee(RegisterEmployeeCommand command, String keycloakId) {
    EmployeeEntity emp =
        EmployeeFactory.create(
            command.name(),
            command.email(),
            command.departmentId(),
            command.positionId(),
            command.roleIds().toArray(Long[]::new));
    emp.assignKeycloakId(keycloakId);
    return emp;
  }

  private Mono<Long> writeFgaTuples(EmployeeEntity saved, Set<String> roleNames) {
    return tupleService
        .writeEmployeeRegistration(saved.getKeycloakId(), saved.getId(), roleNames)
        .thenReturn(saved.getId());
  }

  private Set<String> roleNames(List<RoleEntity> roles) {
    return roles.stream().map(RoleEntity::getName).collect(Collectors.toSet());
  }
}
