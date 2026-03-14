package com.seohalabs.moduerp.organization.shared.infrastructure.openfga;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientTupleKeyWithoutCondition;
import dev.openfga.sdk.api.client.model.ClientWriteResponse;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenFgaTupleServiceTest {

  @Mock OpenFgaClient fgaClient;
  @InjectMocks OpenFgaTupleService tupleService;

  @Nested
  @DisplayName("Employee resignation tuple management")
  class DeleteEmployeeResignation {

    @Test
    @DisplayName("System parent tuple is preserved so admin can still view the resigned employee")
    void givenResignedEmployee_whenDeleteEmployeeResignation_thenSystemParentTupleIsNotDeleted()
        throws Exception {
      // given
      given(fgaClient.deleteTuples(any()))
          .willReturn(CompletableFuture.completedFuture(Mockito.mock(ClientWriteResponse.class)));

      // when
      tupleService.deleteEmployeeResignation("keycloak-1", 1L, Set.of("EMPLOYEE")).block();

      // then
      ArgumentCaptor<List<ClientTupleKeyWithoutCondition>> captor =
          ArgumentCaptor.forClass((Class) List.class);
      Mockito.verify(fgaClient).deleteTuples(captor.capture());
      assertThat(captor.getValue())
          .noneMatch(t -> "system:erp".equals(t.getUser()) && "parent".equals(t.getRelation()));
    }

    @Test
    @DisplayName("Member and owner tuples are deleted on resignation")
    void givenResignedEmployee_whenDeleteEmployeeResignation_thenMemberAndOwnerTuplesAreDeleted()
        throws Exception {
      // given
      given(fgaClient.deleteTuples(any()))
          .willReturn(CompletableFuture.completedFuture(Mockito.mock(ClientWriteResponse.class)));

      // when
      tupleService.deleteEmployeeResignation("keycloak-1", 1L, Set.of("EMPLOYEE")).block();

      // then
      ArgumentCaptor<List<ClientTupleKeyWithoutCondition>> captor =
          ArgumentCaptor.forClass((Class) List.class);
      Mockito.verify(fgaClient).deleteTuples(captor.capture());
      assertThat(captor.getValue())
          .anyMatch(t -> "user:keycloak-1".equals(t.getUser()) && "member".equals(t.getRelation()))
          .anyMatch(t -> "user:keycloak-1".equals(t.getUser()) && "owner".equals(t.getRelation()));
    }
  }
}
