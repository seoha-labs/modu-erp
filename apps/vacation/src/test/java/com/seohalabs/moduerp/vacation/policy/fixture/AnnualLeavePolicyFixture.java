package com.seohalabs.moduerp.vacation.policy.fixture;

import com.seohalabs.moduerp.vacation.policy.application.AnnualLeavePolicyResult;
import com.seohalabs.moduerp.vacation.policy.application.CreateAnnualLeavePolicyCommand;
import com.seohalabs.moduerp.vacation.policy.application.TenureBonusCommand;
import com.seohalabs.moduerp.vacation.policy.application.TenureBonusResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class AnnualLeavePolicyFixture {

  private AnnualLeavePolicyFixture() {}

  public static CreateAnnualLeavePolicyCommand koreaCommand() {
    return new CreateAnnualLeavePolicyCommand(
        "KR",
        88,
        120,
        LocalDate.of(2026, 1, 1),
        List.of(new TenureBonusCommand(3, 8, null), new TenureBonusCommand(6, 8, 160)));
  }

  public static CreateAnnualLeavePolicyCommand koreaCommandWithoutBonuses() {
    return new CreateAnnualLeavePolicyCommand("KR", 88, 120, LocalDate.of(2026, 1, 1), List.of());
  }

  public static AnnualLeavePolicyResult koreaResult() {
    return new AnnualLeavePolicyResult(
        1L,
        "KR",
        88,
        120,
        LocalDate.of(2026, 1, 1),
        LocalDateTime.of(2026, 3, 16, 10, 0),
        List.of(new TenureBonusResult(1L, 3, 8, null), new TenureBonusResult(2L, 6, 8, 160)));
  }

  public static String koreaRequestJson() {
    return
    """
        {
          "countryCode": "KR",
          "initialVacationHours": 88,
          "annualVacationHours": 120,
          "effectiveDate": "2026-01-01",
          "tenureBonuses": [
            {
              "requiredTenureYears": 3,
              "bonusHours": 8,
              "maxTotalHours": null
            },
            {
              "requiredTenureYears": 6,
              "bonusHours": 8,
              "maxTotalHours": 160
            }
          ]
        }
        """;
  }
}
