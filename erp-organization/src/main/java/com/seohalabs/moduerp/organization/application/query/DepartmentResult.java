package com.seohalabs.moduerp.organization.application.query;

import java.util.List;

public record DepartmentResult(Long id, String name, Long parentId, List<DepartmentResult> children) {}
