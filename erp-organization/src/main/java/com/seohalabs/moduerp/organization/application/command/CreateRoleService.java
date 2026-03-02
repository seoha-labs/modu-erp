package com.seohalabs.moduerp.organization.application.command;

import com.seohalabs.moduerp.organization.domain.model.role.RoleEntity;
import com.seohalabs.moduerp.organization.domain.model.role.RoleFactory;
import com.seohalabs.moduerp.organization.infrastructure.keycloak.KeycloakRoleClient;
import com.seohalabs.moduerp.organization.infrastructure.openfga.OpenFgaTupleService;
import com.seohalabs.moduerp.organization.infrastructure.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateRoleService {

  private final RoleRepository roleRepository;
  private final KeycloakRoleClient keycloakRoleClient;
  private final OpenFgaTupleService tupleService;

  public Mono<Long> handle(CreateRoleCommand command) {
    RoleEntity role = RoleFactory.create(command.name(), command.description());
    return syncToKeycloak(role)
        .then(roleRepository.save(role))
        .flatMap(this::registerInFga);
  }

  private Mono<Void> syncToKeycloak(RoleEntity role) {
    return Mono.fromRunnable(
            () -> keycloakRoleClient.create(role.getName(), role.getDescription()))
        .subscribeOn(Schedulers.boundedElastic())
        .then();
  }

  private Mono<Long> registerInFga(RoleEntity saved) {
    return tupleService.writeRoleCreation(saved.getId()).thenReturn(saved.getId());
  }
}
