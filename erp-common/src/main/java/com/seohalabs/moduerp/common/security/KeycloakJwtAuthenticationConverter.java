package com.seohalabs.moduerp.common.security;

import java.util.Collection;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class KeycloakJwtAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

  private final RealmAccessRolesExtractor rolesExtractor = new RealmAccessRolesExtractor();

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    return new JwtAuthenticationToken(jwt, authorities(jwt), jwt.getSubject());
  }

  private Collection<GrantedAuthority> authorities(Jwt jwt) {
    Collection<GrantedAuthority> result = rolesExtractor.convert(jwt);
    return result != null ? result : List.of();
  }
}
