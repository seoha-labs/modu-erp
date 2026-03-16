package com.seohalabs.moduerp.vacation.policy.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AnnualLeavePolicyResult(
    Long id,
    String countryCode,
    Integer initialVacationHours,
    Integer annualVacationHours,
    LocalDate effectiveDate,
    LocalDateTime createdAt,
    List<TenureBonusResult> tenureBonuses) {}
