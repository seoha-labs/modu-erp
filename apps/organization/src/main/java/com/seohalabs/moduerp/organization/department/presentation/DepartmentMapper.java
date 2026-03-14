package com.seohalabs.moduerp.organization.department.presentation;

import com.seohalabs.moduerp.organization.shared.mapping.OrganizationMapperConfig;
import com.seohalabs.moduerp.organization.department.application.CreateDepartmentCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = OrganizationMapperConfig.class)
public interface DepartmentMapper {

  DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

  CreateDepartmentCommand toCommand(CreateDepartmentRequest request);
}
