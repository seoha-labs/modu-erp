package com.seohalabs.moduerp.organization.department.application;

import com.seohalabs.moduerp.organization.department.domain.DepartmentEntity;
import com.seohalabs.moduerp.organization.department.domain.DepartmentFactory;
import com.seohalabs.moduerp.organization.department.infrastructure.persistence.DepartmentRepository;
import com.seohalabs.moduerp.organization.shared.infrastructure.openfga.OpenFgaTupleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateDepartmentService {

  private final DepartmentRepository departmentRepository;
  private final OpenFgaTupleService tupleService;

  public Mono<Long> handle(CreateDepartmentCommand command) {
    DepartmentEntity department = DepartmentFactory.create(command.name(), command.parentId());
    return departmentRepository.save(department).flatMap(this::registerInFga);
  }

  private Mono<Long> registerInFga(DepartmentEntity saved) {
    return tupleService.writeDepartmentCreation(saved.getId()).thenReturn(saved.getId());
  }
}
