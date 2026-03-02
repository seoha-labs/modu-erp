package com.seohalabs.moduerp.organization.application.command;

import java.util.Set;

public record RegisterEmployeeCommand(
    String name, String email, Long departmentId, Long positionId, Set<Long> roleIds) {}
