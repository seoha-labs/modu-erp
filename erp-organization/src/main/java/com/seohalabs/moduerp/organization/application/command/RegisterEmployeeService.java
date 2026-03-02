package com.seohalabs.moduerp.organization.application.command;

import com.seohalabs.moduerp.organization.domain.model.employee.EmployeeEntity;
import com.seohalabs.moduerp.organization.domain.model.employee.EmployeeFactory;
import com.seohalabs.moduerp.organization.domain.model.role.RoleEntity;
import com.seohalabs.moduerp.organization.infrastructure.keycloak.KeycloakAccountClient;
import com.seohalabs.moduerp.organization.infrastructure.openfga.OpenFgaTupleService;
import com.seohalabs.moduerp.organization.infrastructure.persistence.EmployeeRepository;
import com.seohalabs.moduerp.organization.infrastructure.persistence.RoleRepository;
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

  public Mono<Long> handle(RegisterEmployeeCommand command) {
    return roleRepository.findAllById(command.roleIds())
        .collectList()
        .flatMap(roles -> registerWithRoles(command, roles));
  }

  private Mono<Long> registerWithRoles(RegisterEmployeeCommand command, List<RoleEntity> roles) {
    return createKeycloakAccount(command, roleNames(roles))
        .flatMap(keycloakId -> saveAndRegister(command, keycloakId, roles));
  }

  private Mono<String> createKeycloakAccount(
      RegisterEmployeeCommand command, Set<String> roleNames) {
    return Mono.fromCallable(
            () -> keycloakAccountClient.create(command.email(), command.name(), roleNames))
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<Long> saveAndRegister(
      RegisterEmployeeCommand command, String keycloakId, List<RoleEntity> roles) {
    EmployeeEntity employee = buildEmployee(command, keycloakId);
    return employeeRepository.save(employee)
        .flatMap(saved -> writeFgaTuples(saved, roleNames(roles)));
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
