package com.seohalabs.moduerp.organization.employee.application;

import com.seohalabs.moduerp.organization.employee.domain.EmployeeEntity;
import com.seohalabs.moduerp.organization.shared.infrastructure.openfga.OpenFgaTupleService;
import com.seohalabs.moduerp.organization.employee.infrastructure.persistence.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class RevokeHrManagerService {

  private final EmployeeRepository employeeRepository;
  private final OpenFgaTupleService tupleService;

  public Mono<Void> handle(RevokeHrManagerCommand command) {
    return findEmployee(command.employeeId())
        .flatMap(employee -> tupleService.revokeHrManager(employee.getKeycloakId()));
  }

  private Mono<EmployeeEntity> findEmployee(Long id) {
    return employeeRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Employee not found: " + id)));
  }
}
