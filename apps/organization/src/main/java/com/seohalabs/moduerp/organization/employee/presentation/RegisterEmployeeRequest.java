package com.seohalabs.moduerp.organization.employee.presentation;

import java.util.Set;

public record RegisterEmployeeRequest(
    String name, String email, Long departmentId, Long positionId, Set<Long> roleIds) {}
