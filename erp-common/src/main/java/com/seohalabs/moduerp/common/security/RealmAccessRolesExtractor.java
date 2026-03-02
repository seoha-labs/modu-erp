package com.seohalabs.moduerp.common.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class RealmAccessRolesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess == null) {
      return List.of();
    }
    return extractRoles(realmAccess);
  }

  private Collection<GrantedAuthority> extractRoles(Map<String, Object> realmAccess) {
    List<String> roles = (List<String>) realmAccess.get("roles");
    if (roles == null) {
      return List.of();
    }
    return toAuthorities(roles);
  }

  private Collection<GrantedAuthority> toAuthorities(List<String> roles) {
    return roles.stream()
        .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
        .toList();
  }
}
