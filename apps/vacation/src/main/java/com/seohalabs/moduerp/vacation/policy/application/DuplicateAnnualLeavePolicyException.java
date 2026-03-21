package com.seohalabs.moduerp.vacation.policy.application;

import java.time.LocalDate;

public class DuplicateAnnualLeavePolicyException extends RuntimeException {

  public DuplicateAnnualLeavePolicyException(String countryCode, LocalDate effectiveDate) {
    super(
        "Annual leave policy already exists for country '%s' with effective date '%s'"
            .formatted(countryCode, effectiveDate));
  }
}
