package com.seohalabs.moduerp.organization.position.application;

import com.seohalabs.moduerp.organization.position.domain.PositionEntity;
import com.seohalabs.moduerp.organization.shared.mapping.OrganizationMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = OrganizationMapperConfig.class)
public interface PositionDomainMapper {

  PositionDomainMapper INSTANCE = Mappers.getMapper(PositionDomainMapper.class);

  PositionResult toResult(PositionEntity position);
}
