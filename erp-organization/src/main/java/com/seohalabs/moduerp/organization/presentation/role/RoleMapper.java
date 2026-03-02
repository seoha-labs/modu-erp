package com.seohalabs.moduerp.organization.presentation.role;

import com.seohalabs.moduerp.common.mapper.CommonMapper;
import com.seohalabs.moduerp.organization.application.command.CreateRoleCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = CommonMapper.class)
public interface RoleMapper {

  RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

  CreateRoleCommand toCommand(CreateRoleRequest request);
}
