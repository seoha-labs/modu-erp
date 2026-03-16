package com.seohalabs.moduerp.vacation.policy.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TenureBonusFactory {

  public static TenureBonusEntity create(
      Long annualLeavePolicyId,
      Integer requiredTenureYears,
      Integer bonusHours,
      Integer maxTotalHours) {
    return TenureBonusEntity.builder()
        .annualLeavePolicyId(annualLeavePolicyId)
        .requiredTenureYears(requiredTenureYears)
        .bonusHours(bonusHours)
        .maxTotalHours(maxTotalHours)
        .build();
  }
}
