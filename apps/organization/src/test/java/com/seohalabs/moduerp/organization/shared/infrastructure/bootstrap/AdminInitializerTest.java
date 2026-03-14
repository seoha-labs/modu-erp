package com.seohalabs.moduerp.organization.shared.infrastructure.bootstrap;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.seohalabs.moduerp.organization.employee.domain.EmployeeEntity;
import com.seohalabs.moduerp.organization.employee.infrastructure.persistence.EmployeeRepository;
import com.seohalabs.moduerp.organization.role.infrastructure.persistence.RoleRepository;
import com.seohalabs.moduerp.organization.shared.infrastructure.keycloak.KeycloakAccountClient;
import com.seohalabs.moduerp.organization.shared.infrastructure.openfga.OpenFgaTupleService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

  @Mock AdminProperties adminProperties;
  @Mock KeycloakAccountClient keycloakAccountClient;
  @Mock EmployeeRepository employeeRepository;
  @Mock RoleRepository roleRepository;
  @Mock OpenFgaTupleService tupleService;
  @InjectMocks AdminInitializer adminInitializer;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(adminInitializer, "storeId", "store-123");
    ReflectionTestUtils.setField(adminInitializer, "modelId", "model-123");
    given(adminProperties.username()).willReturn("admin");
  }

  @Nested
  @DisplayName("Admin initialization on startup")
  class Run {

    @Test
    @DisplayName("FGA registration tuples are re-written when admin employee already exists in DB")
    void givenAdminEmployeeExists_whenRun_thenFgaTuplesAreRecovered() {
      // given
      EmployeeEntity existingAdmin = mockAdminEmployee();
      given(keycloakAccountClient.findByUsername("admin")).willReturn(Optional.of("keycloak-1"));
      given(employeeRepository.findByKeycloakId("keycloak-1")).willReturn(Mono.just(existingAdmin));
      given(tupleService.writeEmployeeRegistration(anyString(), anyLong(), anySet()))
          .willReturn(Mono.empty());

      // when
      adminInitializer.run(null);

      // then
      then(tupleService).should().writeEmployeeRegistration(anyString(), anyLong(), anySet());
    }

    @Test
    @DisplayName("Full admin setup is skipped when OpenFGA is not configured")
    void givenOpenFgaNotConfigured_whenRun_thenNothingIsExecuted() {
      // given
      ReflectionTestUtils.setField(adminInitializer, "storeId", "");
      ReflectionTestUtils.setField(adminInitializer, "modelId", "");

      // when
      adminInitializer.run(null);

      // then
      then(keycloakAccountClient).shouldHaveNoInteractions();
      then(employeeRepository).shouldHaveNoInteractions();
    }

    private EmployeeEntity mockAdminEmployee() {
      EmployeeEntity employee = org.mockito.Mockito.mock(EmployeeEntity.class);
      given(employee.getId()).willReturn(42L);
      return employee;
    }
  }
}
