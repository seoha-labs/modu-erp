package com.seohalabs.moduerp.organization.infrastructure.openfga;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientTupleKeyWithoutCondition;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OpenFgaTupleService {

  private final OpenFgaClient fgaClient;

  public Mono<Void> writeEmployeeRegistration(String keycloakId, Long employeeId, Set<String> roleNames) {
    return writeTuples(allEmployeeTuples(keycloakId, employeeId, roleNames));
  }

  public Mono<Void> deleteEmployeeResignation(String keycloakId, Long employeeId, Set<String> roleNames) {
    return deleteTuples(allDeleteTuples(keycloakId, employeeId, roleNames));
  }

  public Mono<Void> writeDepartmentCreation(Long departmentId) {
    return writeTuples(List.of(writeKey("system:erp", "parent", "department:" + departmentId)));
  }

  public Mono<Void> writePositionCreation(Long positionId) {
    return writeTuples(List.of(writeKey("system:erp", "parent", "position:" + positionId)));
  }

  public Mono<Void> writeRoleCreation(Long roleId) {
    return writeTuples(List.of(writeKey("system:erp", "parent", "role_resource:" + roleId)));
  }

  public Mono<Void> grantHrManager(String keycloakId) {
    return writeTuples(List.of(writeKey("user:" + keycloakId, "hr_manager", "system:erp")));
  }

  public Mono<Void> revokeHrManager(String keycloakId) {
    return deleteTuples(List.of(deleteKey("user:" + keycloakId, "hr_manager", "system:erp")));
  }

  private List<ClientTupleKey> allEmployeeTuples(
      String keycloakId, Long employeeId, Set<String> roleNames) {
    return Stream.concat(
            baseTuples(keycloakId, employeeId).stream(), roleTuples(keycloakId, roleNames).stream())
        .toList();
  }

  private List<ClientTupleKeyWithoutCondition> allDeleteTuples(
      String keycloakId, Long employeeId, Set<String> roleNames) {
    return Stream.concat(
            baseDeleteTuples(keycloakId, employeeId).stream(),
            roleDeleteTuples(keycloakId, roleNames).stream())
        .toList();
  }

  private List<ClientTupleKey> baseTuples(String keycloakId, Long employeeId) {
    return List.of(
        writeKey("user:" + keycloakId, "member", "system:erp"),
        writeKey("user:" + keycloakId, "owner", "employee:" + employeeId),
        writeKey("system:erp", "parent", "employee:" + employeeId));
  }

  private List<ClientTupleKeyWithoutCondition> baseDeleteTuples(
      String keycloakId, Long employeeId) {
    return List.of(
        deleteKey("user:" + keycloakId, "member", "system:erp"),
        deleteKey("user:" + keycloakId, "owner", "employee:" + employeeId),
        deleteKey("system:erp", "parent", "employee:" + employeeId));
  }

  private List<ClientTupleKey> roleTuples(String keycloakId, Set<String> roleNames) {
    return roleNames.stream()
        .filter(r -> r.equals("ADMIN"))
        .map(r -> writeKey("user:" + keycloakId, "admin", "system:erp"))
        .toList();
  }

  private List<ClientTupleKeyWithoutCondition> roleDeleteTuples(
      String keycloakId, Set<String> roleNames) {
    return roleNames.stream()
        .filter(r -> r.equals("ADMIN"))
        .map(r -> deleteKey("user:" + keycloakId, "admin", "system:erp"))
        .toList();
  }

  private Mono<Void> writeTuples(List<ClientTupleKey> tuples) {
    return Mono.fromCallable(() -> fgaClient.writeTuples(tuples))
        .flatMap(Mono::fromFuture)
        .then();
  }

  private Mono<Void> deleteTuples(List<ClientTupleKeyWithoutCondition> tuples) {
    return Mono.fromCallable(() -> fgaClient.deleteTuples(tuples))
        .flatMap(Mono::fromFuture)
        .then();
  }

  private ClientTupleKey writeKey(String user, String relation, String object) {
    return new ClientTupleKey().user(user).relation(relation)._object(object);
  }

  private ClientTupleKeyWithoutCondition deleteKey(String user, String relation, String object) {
    return new ClientTupleKeyWithoutCondition().user(user).relation(relation)._object(object);
  }
}
