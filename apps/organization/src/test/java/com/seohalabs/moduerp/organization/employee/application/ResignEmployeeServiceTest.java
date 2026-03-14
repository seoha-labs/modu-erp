package com.seohalabs.moduerp.organization.employee.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.seohalabs.moduerp.organization.employee.domain.EmployeeEntity;
import com.seohalabs.moduerp.organization.employee.infrastructure.persistence.EmployeeRepository;
import com.seohalabs.moduerp.organization.role.domain.RoleFactory;
import com.seohalabs.moduerp.organization.role.infrastructure.persistence.RoleRepository;
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
class ResignEmployeeServiceTest {

  @Mock EmployeeRepository employeeRepository;
  @Mock RoleRepository roleRepository;
  @Mock KeycloakAccountClient keycloakAccountClient;
  @Mock OpenFgaTupleService tupleService;
  @InjectMocks ResignEmployeeService resignEmployeeService;

  @Nested
  @DisplayName("Employee resignation")
  class Handle {

    @Test
    @DisplayName("Keycloak account is disabled when an employee resigns")
    void givenActiveEmployee_whenResign_thenKeycloakAccountIsDisabled() {
      // given
      EmployeeEntity employee = mockActiveEmployee(1L, "keycloak-1", new Long[] {1L});
      given(employeeRepository.findById(1L)).willReturn(Mono.just(employee));
      given(roleRepository.findAllById(org.mockito.ArgumentMatchers.<Long>anyIterable()))
          .willReturn(Flux.just(RoleFactory.create("EMPLOYEE", "Employee role")));
      given(tupleService.deleteEmployeeResignation(anyString(), anyLong(), anySet()))
          .willReturn(Mono.empty());
      given(employeeRepository.save(any())).willReturn(Mono.just(employee));

      // when
      resignEmployeeService.handle(new ResignEmployeeCommand(1L)).block();

      // then
      then(keycloakAccountClient).should().disable("keycloak-1");
    }

    @Test
    @DisplayName("FGA tuples are deleted when an employee resigns")
    void givenActiveEmployee_whenResign_thenFgaTuplesAreDeleted() {
      // given
      EmployeeEntity employee = mockActiveEmployee(1L, "keycloak-1", new Long[] {1L});
      given(employeeRepository.findById(1L)).willReturn(Mono.just(employee));
      given(roleRepository.findAllById(org.mockito.ArgumentMatchers.<Long>anyIterable()))
          .willReturn(Flux.just(RoleFactory.create("EMPLOYEE", "Employee role")));
      given(tupleService.deleteEmployeeResignation(anyString(), anyLong(), anySet()))
          .willReturn(Mono.empty());
      given(employeeRepository.save(any())).willReturn(Mono.just(employee));

      // when
      resignEmployeeService.handle(new ResignEmployeeCommand(1L)).block();

      // then
      then(tupleService).should().deleteEmployeeResignation("keycloak-1", 1L, Set.of("EMPLOYEE"));
    }

    @Test
    @DisplayName("Employee status is persisted as RESIGNED after resignation")
    void givenActiveEmployee_whenResign_thenEmployeeIsSavedWithResignedStatus() {
      // given
      EmployeeEntity employee = mockActiveEmployee(1L, "keycloak-1", new Long[] {1L});
      given(employeeRepository.findById(1L)).willReturn(Mono.just(employee));
      given(roleRepository.findAllById(org.mockito.ArgumentMatchers.<Long>anyIterable()))
          .willReturn(Flux.just(RoleFactory.create("EMPLOYEE", "Employee role")));
      given(tupleService.deleteEmployeeResignation(anyString(), anyLong(), anySet()))
          .willReturn(Mono.empty());
      given(employeeRepository.save(any())).willReturn(Mono.just(employee));

      // when
      resignEmployeeService.handle(new ResignEmployeeCommand(1L)).block();

      // then
      then(employee).should().resign();
      then(employeeRepository).should().save(employee);
    }

    private EmployeeEntity mockActiveEmployee(Long id, String keycloakId, Long[] roleIds) {
      EmployeeEntity employee = Mockito.mock(EmployeeEntity.class);
      given(employee.getId()).willReturn(id);
      given(employee.getKeycloakId()).willReturn(keycloakId);
      given(employee.getRoleIds()).willReturn(roleIds);
      return employee;
    }
  }
}
