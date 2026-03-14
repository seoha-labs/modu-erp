package com.seohalabs.moduerp.organization.employee.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.seohalabs.moduerp.organization.employee.domain.EmployeeEntity;
import com.seohalabs.moduerp.organization.employee.infrastructure.persistence.EmployeeRepository;
import com.seohalabs.moduerp.organization.role.domain.RoleFactory;
import com.seohalabs.moduerp.organization.role.infrastructure.persistence.RoleRepository;
import com.seohalabs.moduerp.organization.shared.infrastructure.keycloak.CreatedKeycloakAccount;
import com.seohalabs.moduerp.organization.shared.infrastructure.keycloak.KeycloakAccountClient;
import com.seohalabs.moduerp.organization.shared.infrastructure.openfga.OpenFgaTupleService;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RegisterEmployeeServiceTest {

  @Mock EmployeeRepository employeeRepository;
  @Mock RoleRepository roleRepository;
  @Mock KeycloakAccountClient keycloakAccountClient;
  @Mock OpenFgaTupleService tupleService;
  @InjectMocks RegisterEmployeeService registerEmployeeService;

  @Nested
  @DisplayName("Employee registration")
  class Handle {

    @Test
    @DisplayName("Registration result includes a temporary password for the new employee")
    void givenValidCommand_whenHandle_thenResultContainsTemporaryPassword() {
      // given
      RegisterEmployeeCommand command =
          new RegisterEmployeeCommand("John", "john@example.com", null, null, Set.of(1L));
      given(roleRepository.findAllById(org.mockito.ArgumentMatchers.<Long>anyIterable()))
          .willReturn(Flux.just(RoleFactory.create("EMPLOYEE", "Employee role")));
      given(keycloakAccountClient.createEmployee(anyString(), anyString(), anySet()))
          .willReturn(new CreatedKeycloakAccount("keycloak-1", "tmpPass123"));
      EmployeeEntity savedEmployee = mockSavedEmployee(10L);
      given(employeeRepository.save(any())).willReturn(Mono.just(savedEmployee));
      given(tupleService.writeEmployeeRegistration(anyString(), any(), anySet()))
          .willReturn(Mono.empty());

      // when
      RegisterEmployeeResult result = registerEmployeeService.handle(command).block();

      // then
      assertThat(result).isNotNull();
      assertThat(result.id()).isEqualTo(10L);
      assertThat(result.temporaryPassword()).isEqualTo("tmpPass123");
    }

    @Test
    @DisplayName("Keycloak account is provisioned using createEmployee (not create)")
    void givenValidCommand_whenHandle_thenCreateEmployeeIsCalledOnKeycloak() {
      // given
      RegisterEmployeeCommand command =
          new RegisterEmployeeCommand("Jane", "jane@example.com", null, null, Set.of(1L));
      given(roleRepository.findAllById(org.mockito.ArgumentMatchers.<Long>anyIterable()))
          .willReturn(Flux.just(RoleFactory.create("EMPLOYEE", "Employee role")));
      given(keycloakAccountClient.createEmployee(anyString(), anyString(), anySet()))
          .willReturn(new CreatedKeycloakAccount("keycloak-2", "pass456"));
      given(employeeRepository.save(any())).willReturn(Mono.just(mockSavedEmployee(20L)));
      given(tupleService.writeEmployeeRegistration(anyString(), any(), anySet()))
          .willReturn(Mono.empty());

      // when
      registerEmployeeService.handle(command).block();

      // then
      then(keycloakAccountClient).should().createEmployee("jane@example.com", "Jane", anySet());
    }

    @Test
    @DisplayName("Registration fails when Keycloak account creation throws an exception")
    void givenKeycloakFailure_whenHandle_thenExceptionIsPropagated() {
      // given
      RegisterEmployeeCommand command =
          new RegisterEmployeeCommand("Fail", "fail@example.com", null, null, Set.of(1L));
      given(roleRepository.findAllById(org.mockito.ArgumentMatchers.<Long>anyIterable()))
          .willReturn(Flux.just(RoleFactory.create("EMPLOYEE", "Employee role")));
      given(keycloakAccountClient.createEmployee(anyString(), anyString(), anySet()))
          .willThrow(new RuntimeException("Keycloak unavailable"));

      // when & then
      assertThatThrownBy(() -> registerEmployeeService.handle(command).block())
          .hasMessageContaining("Keycloak unavailable");
    }

    private EmployeeEntity mockSavedEmployee(Long id) {
      EmployeeEntity employee = Mockito.mock(EmployeeEntity.class);
      given(employee.getId()).willReturn(id);
      given(employee.getKeycloakId()).willReturn("keycloak-" + id);
      return employee;
    }
  }
}
