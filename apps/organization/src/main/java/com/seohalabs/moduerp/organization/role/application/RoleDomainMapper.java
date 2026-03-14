package com.seohalabs.moduerp.organization.role.application;

import com.seohalabs.moduerp.organization.role.domain.RoleEntity;
import com.seohalabs.moduerp.organization.shared.mapping.OrganizationMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = OrganizationMapperConfig.class)
public interface RoleDomainMapper {

  RoleDomainMapper INSTANCE = Mappers.getMapper(RoleDomainMapper.class);

  RoleResult toResult(RoleEntity role);
}
