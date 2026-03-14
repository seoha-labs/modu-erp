package com.seohalabs.moduerp.organization.department.application;

import java.util.List;

public record DepartmentResult(
    Long id, String name, Long parentId, List<DepartmentResult> children) {}
