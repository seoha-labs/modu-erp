package com.seohalabs.moduerp.organization.application.query;

import com.seohalabs.moduerp.common.mapper.CommonMapper;
import com.seohalabs.moduerp.organization.domain.model.employee.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = CommonMapper.class)
public interface EmployeeDomainMapper {

  EmployeeDomainMapper INSTANCE = Mappers.getMapper(EmployeeDomainMapper.class);

  @Mapping(target = "roleIds", source = "employee.roleIds")
  EmployeeResult toResult(EmployeeEntity employee);
}
