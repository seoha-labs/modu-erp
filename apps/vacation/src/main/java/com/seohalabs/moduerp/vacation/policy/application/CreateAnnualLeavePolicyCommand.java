package com.seohalabs.moduerp.vacation.policy.application;

import java.time.LocalDate;
import java.util.List;

public record CreateAnnualLeavePolicyCommand(
    String countryCode,
    Integer initialVacationHours,
    Integer annualVacationHours,
    LocalDate effectiveDate,
    List<TenureBonusCommand> tenureBonuses) {}
