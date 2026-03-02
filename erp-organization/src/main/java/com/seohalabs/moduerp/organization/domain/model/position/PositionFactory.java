package com.seohalabs.moduerp.organization.domain.model.position;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PositionFactory {

    public static PositionEntity create(String name, int level) {
        return PositionEntity.builder()
                .name(name)
                .level(level)
                .build();
    }
}
