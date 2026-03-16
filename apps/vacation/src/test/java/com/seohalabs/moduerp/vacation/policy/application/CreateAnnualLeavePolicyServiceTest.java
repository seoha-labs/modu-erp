package com.seohalabs.moduerp.vacation.policy.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.seohalabs.moduerp.vacation.policy.domain.AnnualLeavePolicyEntityFixture;
import com.seohalabs.moduerp.vacation.policy.domain.TenureBonusEntityFixture;
import com.seohalabs.moduerp.vacation.policy.fixture.AnnualLeavePolicyFixture;
import com.seohalabs.moduerp.vacation.policy.infrastructure.persistence.AnnualLeavePolicyRepository;
import com.seohalabs.moduerp.vacation.policy.infrastructure.persistence.TenureBonusRepository;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateAnnualLeavePolicyServiceTest {

  @Mock AnnualLeavePolicyRepository policyRepository;
  @Mock TenureBonusRepository bonusRepository;
  @InjectMocks CreateAnnualLeavePolicyService service;

  @Nested
  @DisplayName("Annual leave policy creation")
  class Create {

    @DisplayName("A new annual leave policy is created with tenure bonuses")
    @Test
    void givenValidCommand_whenCreate_thenReturnsPolicyWithBonuses() {
      // given
      CreateAnnualLeavePolicyCommand command = AnnualLeavePolicyFixture.koreaCommand();
      given(policyRepository.existsByCountryCodeAndEffectiveDate(any(), any())).willReturn(false);
      given(policyRepository.save(any())).willReturn(AnnualLeavePolicyEntityFixture.korea());
      given(bonusRepository.saveAll(anyList()))
          .willReturn(TenureBonusEntityFixture.defaultBonuses());

      // when
      AnnualLeavePolicyResult result = service.handle(command);

      // then
      assertThat(result.countryCode()).isEqualTo("KR");
      assertThat(result.initialVacationHours()).isEqualTo(88);
      assertThat(result.annualVacationHours()).isEqualTo(120);
      assertThat(result.tenureBonuses()).hasSize(2);
    }

    @DisplayName("A policy without tenure bonuses can be created")
    @Test
    void givenCommandWithoutBonuses_whenCreate_thenReturnsPolicyWithEmptyBonuses() {
      // given
      CreateAnnualLeavePolicyCommand command =
          AnnualLeavePolicyFixture.koreaCommandWithoutBonuses();
      given(policyRepository.existsByCountryCodeAndEffectiveDate(any(), any())).willReturn(false);
      given(policyRepository.save(any())).willReturn(AnnualLeavePolicyEntityFixture.korea());

      // when
      AnnualLeavePolicyResult result = service.handle(command);

      // then
      assertThat(result.tenureBonuses()).isEmpty();
    }

    @DisplayName("Duplicate policy for the same country and effective date is rejected")
    @Test
    void givenDuplicatePolicy_whenCreate_thenThrowsException() {
      // given
      CreateAnnualLeavePolicyCommand command = AnnualLeavePolicyFixture.koreaCommand();
      given(policyRepository.existsByCountryCodeAndEffectiveDate("KR", command.effectiveDate()))
          .willReturn(true);

      // when
      ThrowableAssert.ThrowingCallable create = () -> service.handle(command);

      // then
      assertThatThrownBy(create).isInstanceOf(DuplicateAnnualLeavePolicyException.class);
    }
  }
}
