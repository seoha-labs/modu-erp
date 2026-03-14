package com.seohalabs.moduerp.organization.position.infrastructure.persistence;

import com.seohalabs.moduerp.organization.position.domain.PositionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PositionRepository extends ReactiveCrudRepository<PositionEntity, Long> {}
