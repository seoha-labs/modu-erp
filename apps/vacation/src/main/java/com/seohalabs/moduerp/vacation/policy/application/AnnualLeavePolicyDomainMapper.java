package com.seohalabs.moduerp.vacation.policy.application;

import com.seohalabs.moduerp.vacation.policy.domain.TenureBonusEntity;
import com.seohalabs.moduerp.vacation.shared.mapping.VacationMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = VacationMapperConfig.class)
public interface AnnualLeavePolicyDomainMapper {

  AnnualLeavePolicyDomainMapper INSTANCE = Mappers.getMapper(AnnualLeavePolicyDomainMapper.class);

  TenureBonusResult toResult(TenureBonusEntity entity);

  List<TenureBonusResult> toResults(List<TenureBonusEntity> entities);
}
