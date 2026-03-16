package com.seohalabs.moduerp.vacation.policy.presentation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TenureBonusRequest(
    @NotNull @Min(1) Integer requiredTenureYears,
    @NotNull @Min(1) Integer bonusHours,
    @Min(0) Integer maxTotalHours) {}
