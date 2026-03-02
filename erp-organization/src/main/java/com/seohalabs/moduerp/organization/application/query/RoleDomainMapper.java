package com.seohalabs.moduerp.organization.application.query;

import com.seohalabs.moduerp.common.mapper.CommonMapper;
import com.seohalabs.moduerp.organization.domain.model.role.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = CommonMapper.class)
public interface RoleDomainMapper {

  RoleDomainMapper INSTANCE = Mappers.getMapper(RoleDomainMapper.class);

  RoleResult toResult(RoleEntity role);
}
