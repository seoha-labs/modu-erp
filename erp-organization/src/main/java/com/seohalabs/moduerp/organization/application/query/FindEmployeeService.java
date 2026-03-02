package com.seohalabs.moduerp.organization.application.query;

import com.seohalabs.moduerp.organization.infrastructure.persistence.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindEmployeeService {

  private final EmployeeRepository employeeRepository;

  public Mono<EmployeeResult> handle(FindEmployeeQuery query) {
    return employeeRepository
        .findById(query.id())
        .map(EmployeeDomainMapper.INSTANCE::toResult)
        .switchIfEmpty(
            Mono.error(new IllegalArgumentException("Employee not found: " + query.id())));
  }
}
