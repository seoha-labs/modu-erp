package com.seohalabs.moduerp.organization.employee.infrastructure.persistence;

import com.seohalabs.moduerp.organization.employee.domain.EmployeeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveCrudRepository<EmployeeEntity, Long> {

  Mono<Boolean> existsByKeycloakId(String keycloakId);
}
