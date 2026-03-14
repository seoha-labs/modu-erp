package com.seohalabs.moduerp.organization.position.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PositionFactory {

  public static PositionEntity create(String name, int level) {
    return PositionEntity.builder().name(name).level(level).build();
  }
}
