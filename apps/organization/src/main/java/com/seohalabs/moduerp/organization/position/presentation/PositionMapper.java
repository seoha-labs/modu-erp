package com.seohalabs.moduerp.organization.position.presentation;

import com.seohalabs.moduerp.organization.shared.mapping.OrganizationMapperConfig;
import com.seohalabs.moduerp.organization.position.application.CreatePositionCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = OrganizationMapperConfig.class)
public interface PositionMapper {

  PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

  CreatePositionCommand toCommand(CreatePositionRequest request);
}
