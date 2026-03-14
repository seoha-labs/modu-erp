package com.seohalabs.moduerp.organization.shared.infrastructure.bootstrap;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.seohalabs.moduerp.organization.role.domain.RoleFactory;
import com.seohalabs.moduerp.organization.role.infrastructure.persistence.RoleRepository;
import com.seohalabs.moduerp.organization.shared.infrastructure.keycloak.KeycloakRoleClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class DefaultRoleInitializerTest {

  @Mock RoleRepository roleRepository;
  @Mock KeycloakRoleClient keycloakRoleClient;
  @InjectMocks DefaultRoleInitializer initializer;

  @Nested
  @DisplayName("Default role initialization on startup")
  class Run {

    @Test
    @DisplayName("Keycloak role is created even when the DB role already exists")
    void givenDbRoleExistsButKeycloakRoleMissing_whenRun_thenKeycloakRoleIsCreated() {
      // given
      given(roleRepository.existsByName(DefaultRoleInitializer.ADMIN)).willReturn(Mono.just(true));
      given(roleRepository.existsByName(DefaultRoleInitializer.EMPLOYEE))
          .willReturn(Mono.just(true));
      given(keycloakRoleClient.existsByName(DefaultRoleInitializer.ADMIN)).willReturn(false);
      given(keycloakRoleClient.existsByName(DefaultRoleInitializer.EMPLOYEE)).willReturn(false);

      // when
      initializer.run(null);

      // then
      then(keycloakRoleClient).should().create(DefaultRoleInitializer.ADMIN, "ADMIN role");
      then(keycloakRoleClient).should().create(DefaultRoleInitializer.EMPLOYEE, "EMPLOYEE role");
    }

    @Test
    @DisplayName("Nothing is created when both DB and Keycloak already have the role")
    void givenBothDbAndKeycloakHaveRole_whenRun_thenNothingIsCreated() {
      // given
      given(roleRepository.existsByName(DefaultRoleInitializer.ADMIN)).willReturn(Mono.just(true));
      given(roleRepository.existsByName(DefaultRoleInitializer.EMPLOYEE))
          .willReturn(Mono.just(true));
      given(keycloakRoleClient.existsByName(DefaultRoleInitializer.ADMIN)).willReturn(true);
      given(keycloakRoleClient.existsByName(DefaultRoleInitializer.EMPLOYEE)).willReturn(true);

      // when
      initializer.run(null);

      // then
      then(roleRepository).shouldHaveNoMoreInteractions();
      then(keycloakRoleClient).should().existsByName(DefaultRoleInitializer.ADMIN);
      then(keycloakRoleClient).should().existsByName(DefaultRoleInitializer.EMPLOYEE);
    }

    @Test
    @DisplayName("Role is created in both DB and Keycloak when neither has it")
    void givenNoRoleExists_whenRun_thenRoleIsCreatedInDbAndKeycloak() {
      // given
      given(roleRepository.existsByName(DefaultRoleInitializer.ADMIN)).willReturn(Mono.just(false));
      given(roleRepository.existsByName(DefaultRoleInitializer.EMPLOYEE))
          .willReturn(Mono.just(false));
      given(roleRepository.save(org.mockito.ArgumentMatchers.any()))
          .willReturn(Mono.just(RoleFactory.create("ROLE", "desc")));
      given(keycloakRoleClient.existsByName(org.mockito.ArgumentMatchers.any())).willReturn(false);

      // when
      initializer.run(null);

      // then
      then(roleRepository)
          .should(org.mockito.Mockito.times(2))
          .save(org.mockito.ArgumentMatchers.any());
      then(keycloakRoleClient).should().create(DefaultRoleInitializer.ADMIN, "ADMIN role");
      then(keycloakRoleClient).should().create(DefaultRoleInitializer.EMPLOYEE, "EMPLOYEE role");
    }
  }
}
