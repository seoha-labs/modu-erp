package com.seohalabs.moduerp.vacation.policy.application;

public class AnnualLeavePolicyNotFoundException extends RuntimeException {

  public AnnualLeavePolicyNotFoundException(Long id) {
    super("Annual leave policy not found with id '%d'".formatted(id));
  }

  public AnnualLeavePolicyNotFoundException(String countryCode) {
    super("No active annual leave policy found for country '%s'".formatted(countryCode));
  }
}
