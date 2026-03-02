package com.seohalabs.moduerp.organization.application.query;

import com.seohalabs.moduerp.common.mapper.CommonMapper;
import com.seohalabs.moduerp.organization.domain.model.position.PositionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = CommonMapper.class)
public interface PositionDomainMapper {

  PositionDomainMapper INSTANCE = Mappers.getMapper(PositionDomainMapper.class);

  PositionResult toResult(PositionEntity position);
}
