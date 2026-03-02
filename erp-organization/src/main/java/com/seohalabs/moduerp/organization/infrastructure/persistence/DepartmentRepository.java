package com.seohalabs.moduerp.organization.infrastructure.persistence;

import com.seohalabs.moduerp.organization.domain.model.department.DepartmentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DepartmentRepository extends ReactiveCrudRepository<DepartmentEntity, Long> {}
