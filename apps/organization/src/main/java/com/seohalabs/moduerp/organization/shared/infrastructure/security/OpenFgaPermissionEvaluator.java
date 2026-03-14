package com.seohalabs.moduerp.organization.shared.infrastructure.security;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientCheckRequest;
import dev.openfga.sdk.api.client.model.ClientCheckResponse;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenFgaPermissionEvaluator implements PermissionEvaluator {

  private static final Map<String, String> FGA_TYPE =
      Map.of(
          "System", "system",
          "Employee", "employee",
          "Department", "department",
          "Position", "position",
          "Role", "role_resource");

  private final OpenFgaClient fgaClient;

  @Override
  public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
    return false;
  }

  @Override
  public boolean hasPermission(
      Authentication auth, Serializable targetId, String targetType, Object permission) {
    String userId = resolveUserId(auth);
    if (userId == null) {
      return false;
    }
    return checkPermission(userId, permission.toString(), fgaObject(targetId, targetType));
  }

  private String resolveUserId(Authentication auth) {
    if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
      log.warn(
          "Cannot resolve user identity: not a JwtAuthenticationToken, got {}",
          auth.getClass().getSimpleName());
      return null;
    }
    return resolveFromJwt(jwtAuth.getToken());
  }

  private String resolveFromJwt(Jwt jwt) {
    String sub = extractSub(jwt);
    return sub != null ? sub : extractPreferredUsername(jwt);
  }

  private String extractSub(Jwt jwt) {
    String sub = jwt.getSubject();
    if (sub == null || sub.isBlank()) {
      return null;
    }
    return "user:" + sub;
  }

  private String extractPreferredUsername(Jwt jwt) {
    String username = jwt.getClaimAsString("preferred_username");
    if (username == null || username.isBlank()) {
      log.warn("JWT has no usable identity claim (sub or preferred_username)");
      return null;
    }
    return "user:" + username;
  }

  private String fgaObject(Serializable targetId, String targetType) {
    return FGA_TYPE.getOrDefault(targetType, targetType.toLowerCase()) + ":" + targetId;
  }

  private boolean checkPermission(String user, String relation, String object) {
    try {
      return allowed(user, relation, object);
    } catch (FgaInvalidParameterException | ExecutionException | InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  private boolean allowed(String user, String relation, String object)
      throws FgaInvalidParameterException, ExecutionException, InterruptedException {
    ClientCheckResponse response = fgaClient.check(buildRequest(user, relation, object)).get();
    return Boolean.TRUE.equals(response.getAllowed());
  }

  private ClientCheckRequest buildRequest(String user, String relation, String object) {
    return new ClientCheckRequest().user(user).relation(relation)._object(object);
  }
}
