package com.seohalabs.moduerp.vacation.policy.presentation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AnnualLeavePolicyResponse(
    Long id,
    String countryCode,
    Integer initialVacationHours,
    Integer annualVacationHours,
    LocalDate effectiveDate,
    LocalDateTime createdAt,
    List<TenureBonusResponse> tenureBonuses) {}
