package com.seohalabs.moduerp.organization.department.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DepartmentFactory {

    public static DepartmentEntity create(String name, Long parentId) {
        return DepartmentEntity.builder()
                .name(name)
                .parentId(parentId)
                .build();
    }
}
