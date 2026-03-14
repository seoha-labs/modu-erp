package com.seohalabs.moduerp.organization.employee.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmployeeFactory {

  public static EmployeeEntity create(
      String name, String email, Long departmentId, Long positionId, Long[] roleIds) {
    return EmployeeEntity.builder()
        .name(name)
        .email(email)
        .departmentId(departmentId)
        .positionId(positionId)
        .roleIds(roleIds)
        .build();
  }
}
