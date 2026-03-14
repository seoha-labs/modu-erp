package com.seohalabs.moduerp.organization.department.infrastructure.persistence;

import com.seohalabs.moduerp.organization.department.domain.DepartmentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DepartmentRepository extends ReactiveCrudRepository<DepartmentEntity, Long> {}
