package com.seohalabs.moduerp.organization.application.query;

import com.seohalabs.moduerp.organization.infrastructure.persistence.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindEmployeesService {

  private final EmployeeRepository employeeRepository;

  public Flux<EmployeeResult> handle(FindEmployeesQuery query) {
    return employeeRepository.findAll().map(EmployeeDomainMapper.INSTANCE::toResult);
  }
}
