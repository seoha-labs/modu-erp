package com.seohalabs.moduerp.organization.application.command;

import com.seohalabs.moduerp.organization.domain.model.employee.EmployeeEntity;
import com.seohalabs.moduerp.organization.domain.model.role.RoleEntity;
import com.seohalabs.moduerp.organization.infrastructure.keycloak.KeycloakAccountClient;
import com.seohalabs.moduerp.organization.infrastructure.openfga.OpenFgaTupleService;
import com.seohalabs.moduerp.organization.infrastructure.persistence.EmployeeRepository;
import com.seohalabs.moduerp.organization.infrastructure.persistence.RoleRepository;
import java.util.Arrays;
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
public class ResignEmployeeService {

  private final EmployeeRepository employeeRepository;
  private final RoleRepository roleRepository;
  private final KeycloakAccountClient keycloakAccountClient;
  private final OpenFgaTupleService tupleService;

  public Mono<Void> handle(ResignEmployeeCommand command) {
    return findEmployee(command.employeeId()).flatMap(this::resignEmployee);
  }

  private Mono<EmployeeEntity> findEmployee(Long id) {
    return employeeRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Employee not found: " + id)));
  }

  private Mono<Void> resignEmployee(EmployeeEntity employee) {
    employee.resign();
    return disableKeycloakAccount(employee)
        .then(lookupRolesAndDeleteTuples(employee))
        .then(employeeRepository.save(employee))
        .then();
  }

  private Mono<Void> disableKeycloakAccount(EmployeeEntity employee) {
    return Mono.fromRunnable(() -> keycloakAccountClient.disable(employee.getKeycloakId()))
        .subscribeOn(Schedulers.boundedElastic())
        .then();
  }

  private Mono<Void> lookupRolesAndDeleteTuples(EmployeeEntity employee) {
    return roleRepository
        .findAllById(Arrays.asList(employee.getRoleIds()))
        .collectList()
        .flatMap(roles -> deleteFgaTuples(employee, roles));
  }

  private Mono<Void> deleteFgaTuples(EmployeeEntity employee, List<RoleEntity> roles) {
    return tupleService.deleteEmployeeResignation(
        employee.getKeycloakId(), employee.getId(), roleNames(roles));
  }

  private Set<String> roleNames(List<RoleEntity> roles) {
    return roles.stream().map(RoleEntity::getName).collect(Collectors.toSet());
  }
}
