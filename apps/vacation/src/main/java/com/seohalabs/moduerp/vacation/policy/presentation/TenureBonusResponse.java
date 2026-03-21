package com.seohalabs.moduerp.vacation.policy.presentation;

public record TenureBonusResponse(
    Long id, Integer requiredTenureYears, Integer bonusHours, Integer maxTotalHours) {}
