package com.seohalabs.moduerp.vacation.policy.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnualLeavePolicyFactory {

  public static AnnualLeavePolicyEntity create(
      String countryCode,
      Integer initialVacationHours,
      Integer annualVacationHours,
      LocalDate effectiveDate) {
    return AnnualLeavePolicyEntity.builder()
        .countryCode(countryCode)
        .initialVacationHours(initialVacationHours)
        .annualVacationHours(annualVacationHours)
        .effectiveDate(effectiveDate)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
