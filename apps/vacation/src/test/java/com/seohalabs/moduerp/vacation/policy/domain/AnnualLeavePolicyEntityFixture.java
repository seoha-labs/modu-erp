package com.seohalabs.moduerp.vacation.policy.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AnnualLeavePolicyEntityFixture {

  private AnnualLeavePolicyEntityFixture() {}

  public static AnnualLeavePolicyEntity korea() {
    return AnnualLeavePolicyEntity.builder()
        .id(1L)
        .countryCode("KR")
        .initialVacationHours(88)
        .annualVacationHours(120)
        .effectiveDate(LocalDate.of(2026, 1, 1))
        .createdAt(LocalDateTime.of(2026, 3, 16, 10, 0))
        .build();
  }
}
