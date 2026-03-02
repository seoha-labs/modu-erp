package com.seohalabs.moduerp.organization.presentation.employee;

import com.seohalabs.moduerp.common.mapper.CommonMapper;
import com.seohalabs.moduerp.organization.application.command.RegisterEmployeeCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = CommonMapper.class)
public interface EmployeeMapper {

  EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

  RegisterEmployeeCommand toCommand(RegisterEmployeeRequest request);
}
