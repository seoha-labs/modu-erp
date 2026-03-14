package com.seohalabs.moduerp.organization.shared.infrastructure.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakConfig {

  @Bean
  public Keycloak keycloak(KeycloakProperties properties) {
    return KeycloakBuilder.builder()
        .serverUrl(properties.serverUrl())
        .realm("master")
        .clientId(properties.admin().clientId())
        .username(properties.admin().username())
        .password(properties.admin().password())
        .build();
  }
}
