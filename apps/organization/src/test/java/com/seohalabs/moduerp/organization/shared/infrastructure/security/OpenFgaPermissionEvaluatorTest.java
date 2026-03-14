package com.seohalabs.moduerp.organization.shared.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientCheckResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class OpenFgaPermissionEvaluatorTest {

  @Mock OpenFgaClient fgaClient;
  @InjectMocks OpenFgaPermissionEvaluator evaluator;

  @Nested
  @DisplayName("User identity resolution from JWT")
  class UserIdentityResolution {

    @Test
    @DisplayName("Sub claim is used when present in the JWT")
    void givenJwtWithSub_whenHasPermission_thenSubIsUsedForFgaCheck() throws Exception {
      // given
      Jwt jwt = buildJwt("user-sub-123", null);
      JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, List.of(), "user-sub-123");
      ClientCheckResponse response = allowedResponse();
      given(fgaClient.check(any())).willReturn(CompletableFuture.completedFuture(response));

      // when
      boolean result = evaluator.hasPermission(auth, "erp", "System", "can_view");

      // then
      assertThat(result).isTrue();
      then(fgaClient)
          .should()
          .check(
              org.mockito.ArgumentMatchers.argThat(
                  req -> "user:user-sub-123".equals(req.getUser())));
    }

    @Test
    @DisplayName("Preferred username is used as fallback when sub is absent")
    void givenJwtWithoutSubButWithPreferredUsername_whenHasPermission_thenPreferredUsernameIsUsed()
        throws Exception {
      // given
      Jwt jwt = buildJwt(null, "john.doe");
      JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, List.of(), null);
      ClientCheckResponse response = allowedResponse();
      given(fgaClient.check(any())).willReturn(CompletableFuture.completedFuture(response));

      // when
      boolean result = evaluator.hasPermission(auth, "erp", "System", "can_view");

      // then
      assertThat(result).isTrue();
      then(fgaClient)
          .should()
          .check(
              org.mockito.ArgumentMatchers.argThat(req -> "user:john.doe".equals(req.getUser())));
    }

    @Test
    @DisplayName("Permission is denied and no FGA check is made when JWT has no usable identity")
    void givenJwtWithNoIdentityClaim_whenHasPermission_thenReturnsFalseWithoutFgaCall() {
      // given
      Jwt jwt = buildJwt(null, null);
      JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, List.of(), null);

      // when
      boolean result = evaluator.hasPermission(auth, "erp", "System", "can_view");

      // then
      assertThat(result).isFalse();
      then(fgaClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Permission is denied when authentication is not a JWT token")
    void givenNonJwtAuthentication_whenHasPermission_thenReturnsFalseWithoutFgaCall() {
      // given
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken("user", "pass");

      // when
      boolean result = evaluator.hasPermission(auth, "erp", "System", "can_view");

      // then
      assertThat(result).isFalse();
      then(fgaClient).shouldHaveNoInteractions();
    }

    private Jwt buildJwt(String sub, String preferredUsername) {
      Jwt.Builder builder = Jwt.withTokenValue("token").header("alg", "RS256").subject(sub);
      if (preferredUsername != null) {
        builder.claim("preferred_username", preferredUsername);
      }
      return builder.build();
    }

    private ClientCheckResponse allowedResponse() {
      ClientCheckResponse response = Mockito.mock(ClientCheckResponse.class);
      given(response.getAllowed()).willReturn(true);
      return response;
    }
  }
}
