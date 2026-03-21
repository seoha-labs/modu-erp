package com.seohalabs.moduerp.vacation.policy.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

public record CreateAnnualLeavePolicyRequest(
    @NotBlank @Pattern(regexp = "^[A-Z]{2}$") String countryCode,
    @NotNull @Min(0) Integer initialVacationHours,
    @NotNull @Min(0) Integer annualVacationHours,
    @NotNull LocalDate effectiveDate,
    @Valid List<TenureBonusRequest> tenureBonuses) {}
