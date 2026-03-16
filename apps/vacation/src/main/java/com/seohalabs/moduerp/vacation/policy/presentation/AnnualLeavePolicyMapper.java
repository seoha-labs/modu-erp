package com.seohalabs.moduerp.vacation.policy.presentation;

import com.seohalabs.moduerp.vacation.policy.application.AnnualLeavePolicyResult;
import com.seohalabs.moduerp.vacation.policy.application.CreateAnnualLeavePolicyCommand;
import com.seohalabs.moduerp.vacation.policy.application.TenureBonusResult;
import com.seohalabs.moduerp.vacation.shared.mapping.VacationMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = VacationMapperConfig.class)
public interface AnnualLeavePolicyMapper {

  AnnualLeavePolicyMapper INSTANCE = Mappers.getMapper(AnnualLeavePolicyMapper.class);

  CreateAnnualLeavePolicyCommand toCommand(CreateAnnualLeavePolicyRequest request);

  AnnualLeavePolicyResponse toResponse(AnnualLeavePolicyResult result);

  List<AnnualLeavePolicyResponse> toResponses(List<AnnualLeavePolicyResult> results);

  TenureBonusResponse toResponse(TenureBonusResult result);
}
