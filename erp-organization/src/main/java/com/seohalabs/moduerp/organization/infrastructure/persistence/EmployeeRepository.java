package com.seohalabs.moduerp.organization.infrastructure.persistence;

import com.seohalabs.moduerp.organization.domain.model.employee.EmployeeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveCrudRepository<EmployeeEntity, Long> {

  Mono<Boolean> existsByKeycloakId(String keycloakId);
}
