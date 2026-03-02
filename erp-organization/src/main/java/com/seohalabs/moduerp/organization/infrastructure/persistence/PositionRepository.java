package com.seohalabs.moduerp.organization.infrastructure.persistence;

import com.seohalabs.moduerp.organization.domain.model.position.PositionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PositionRepository extends ReactiveCrudRepository<PositionEntity, Long> {}
