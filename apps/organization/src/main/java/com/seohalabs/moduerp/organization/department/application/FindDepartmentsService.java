package com.seohalabs.moduerp.organization.department.application;

import com.seohalabs.moduerp.organization.department.domain.DepartmentEntity;
import com.seohalabs.moduerp.organization.department.infrastructure.persistence.DepartmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindDepartmentsService {

  private final DepartmentRepository departmentRepository;

  public Flux<DepartmentResult> handle(FindDepartmentsQuery query) {
    return departmentRepository
        .findAll()
        .collectList()
        .flatMapMany(all -> Flux.fromIterable(buildTree(all, null)));
  }

  private List<DepartmentResult> buildTree(List<DepartmentEntity> all, Long parentId) {
    return all.stream()
        .filter(d -> matchesParent(d, parentId))
        .map(d -> toResult(d, buildTree(all, d.getId())))
        .toList();
  }

  private boolean matchesParent(DepartmentEntity dept, Long parentId) {
    return dept.parentId().map(pid -> pid.equals(parentId)).orElse(parentId == null);
  }

  private DepartmentResult toResult(DepartmentEntity dept, List<DepartmentResult> children) {
    Long parentId = dept.parentId().orElse(null);
    return new DepartmentResult(dept.getId(), dept.getName(), parentId, children);
  }
}
