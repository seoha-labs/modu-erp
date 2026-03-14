package com.seohalabs.moduerp.organization.employee.application;

import com.seohalabs.moduerp.organization.employee.domain.EmployeeEntity;
import com.seohalabs.moduerp.organization.employee.infrastructure.persistence.EmployeeRepository;
import com.seohalabs.moduerp.organization.shared.infrastructure.openfga.OpenFgaTupleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class GrantHrManagerService {

  private final EmployeeRepository employeeRepository;
  private final OpenFgaTupleService tupleService;

  public Mono<Void> handle(GrantHrManagerCommand command) {
    return findEmployee(command.employeeId())
        .flatMap(employee -> tupleService.grantHrManager(employee.getKeycloakId()));
  }

  private Mono<EmployeeEntity> findEmployee(Long id) {
    return employeeRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Employee not found: " + id)));
  }
}
