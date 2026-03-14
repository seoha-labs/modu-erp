package com.seohalabs.moduerp.organization.role.application;

import com.seohalabs.moduerp.organization.role.infrastructure.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindRolesService {

  private final RoleRepository roleRepository;

  public Flux<RoleResult> handle(FindRolesQuery query) {
    return roleRepository.findAll().map(RoleDomainMapper.INSTANCE::toResult);
  }
}
