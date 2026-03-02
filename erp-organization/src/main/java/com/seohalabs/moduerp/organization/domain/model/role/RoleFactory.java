package com.seohalabs.moduerp.organization.domain.model.role;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleFactory {

    public static RoleEntity create(String name, String description) {
        return RoleEntity.builder()
                .name(name)
                .description(description)
                .build();
    }
}
