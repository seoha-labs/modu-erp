package com.seohalabs.moduerp.organization.shared.infrastructure.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("keycloak")
public record KeycloakProperties(String serverUrl, String realm, Admin admin) {

  public record Admin(String clientId, String username, String password) {}
}
