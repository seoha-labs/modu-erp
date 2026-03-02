package com.seohalabs.moduerp.organization.presentation.department;

import com.seohalabs.moduerp.common.mapper.CommonMapper;
import com.seohalabs.moduerp.organization.application.command.CreateDepartmentCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = CommonMapper.class)
public interface DepartmentMapper {

  DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

  CreateDepartmentCommand toCommand(CreateDepartmentRequest request);
}
