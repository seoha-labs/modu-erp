package com.seohalabs.moduerp.organization.employee.application;

import com.seohalabs.moduerp.organization.employee.domain.EmployeeEntity;
import com.seohalabs.moduerp.organization.shared.mapping.OrganizationMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = OrganizationMapperConfig.class)
public interface EmployeeDomainMapper {

  EmployeeDomainMapper INSTANCE = Mappers.getMapper(EmployeeDomainMapper.class);

  @Mapping(target = "roleIds", source = "employee.roleIds")
  EmployeeResult toResult(EmployeeEntity employee);
}
