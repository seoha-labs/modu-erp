package com.seohalabs.moduerp.vacation.policy.domain;

import java.util.List;

public final class TenureBonusEntityFixture {

  private TenureBonusEntityFixture() {}

  public static TenureBonusEntity threeYears() {
    return TenureBonusEntity.builder()
        .id(1L)
        .annualLeavePolicyId(1L)
        .requiredTenureYears(3)
        .bonusHours(8)
        .maxTotalHours(null)
        .build();
  }

  public static TenureBonusEntity sixYears() {
    return TenureBonusEntity.builder()
        .id(2L)
        .annualLeavePolicyId(1L)
        .requiredTenureYears(6)
        .bonusHours(8)
        .maxTotalHours(160)
        .build();
  }

  public static List<TenureBonusEntity> defaultBonuses() {
    return List.of(threeYears(), sixYears());
  }
}
