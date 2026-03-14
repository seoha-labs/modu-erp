package com.seohalabs.moduerp.organization.employee.application;

import com.seohalabs.moduerp.organization.employee.infrastructure.persistence.EmployeeRepository;
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
