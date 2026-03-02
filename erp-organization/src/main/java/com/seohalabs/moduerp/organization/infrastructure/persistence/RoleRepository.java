package com.seohalabs.moduerp.organization.infrastructure.persistence;

import com.seohalabs.moduerp.organization.domain.model.role.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveCrudRepository<RoleEntity, Long> {

  Mono<Boolean> existsByName(String name);

  Mono<RoleEntity> findByName(String name);
}
