package com.seohalabs.moduerp.organization.presentation.position;

import com.seohalabs.moduerp.common.mapper.CommonMapper;
import com.seohalabs.moduerp.organization.application.command.CreatePositionCommand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = CommonMapper.class)
public interface PositionMapper {

  PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

  CreatePositionCommand toCommand(CreatePositionRequest request);
}
