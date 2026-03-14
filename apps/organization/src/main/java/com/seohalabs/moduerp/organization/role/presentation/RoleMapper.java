package com.seohalabs.moduerp.organization.role.presentation;

import com.seohalabs.moduerp.organization.shared.mapping.OrganizationMapperConfig;
import com.seohalabs.moduerp.organization.role.application.CreateRoleCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = OrganizationMapperConfig.class)
public interface RoleMapper {

  RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

  CreateRoleCommand toCommand(CreateRoleRequest request);
}
