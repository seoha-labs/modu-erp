package com.seohalabs.moduerp.vacation.policy.application;

public record TenureBonusCommand(
    Integer requiredTenureYears, Integer bonusHours, Integer maxTotalHours) {}
