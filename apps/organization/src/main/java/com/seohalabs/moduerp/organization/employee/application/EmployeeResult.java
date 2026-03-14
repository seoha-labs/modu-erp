package com.seohalabs.moduerp.organization.employee.application;

import java.util.Set;

public record EmployeeResult(
    Long id,
    String name,
    String email,
    Long departmentId,
    Long positionId,
    Set<Long> roleIds,
    String status) {}
