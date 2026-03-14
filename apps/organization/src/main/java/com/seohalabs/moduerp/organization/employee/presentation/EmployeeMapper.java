package com.seohalabs.moduerp.organization.employee.presentation;

import com.seohalabs.moduerp.organization.employee.application.RegisterEmployeeCommand;
import com.seohalabs.moduerp.organization.shared.mapping.OrganizationMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = OrganizationMapperConfig.class)
public interface EmployeeMapper {

  EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

  RegisterEmployeeCommand toCommand(RegisterEmployeeRequest request);
}
