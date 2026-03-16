package com.seohalabs.moduerp.vacation.policy.application;

public record TenureBonusResult(
    Long id, Integer requiredTenureYears, Integer bonusHours, Integer maxTotalHours) {}
