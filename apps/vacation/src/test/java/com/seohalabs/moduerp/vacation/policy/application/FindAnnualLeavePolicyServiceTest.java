package com.seohalabs.moduerp.vacation.policy.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.seohalabs.moduerp.vacation.policy.domain.AnnualLeavePolicyEntityFixture;
import com.seohalabs.moduerp.vacation.policy.domain.TenureBonusEntityFixture;
import com.seohalabs.moduerp.vacation.policy.infrastructure.persistence.AnnualLeavePolicyRepository;
import com.seohalabs.moduerp.vacation.policy.infrastructure.persistence.TenureBonusRepository;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindAnnualLeavePolicyServiceTest {

  @Mock AnnualLeavePolicyRepository policyRepository;
  @Mock TenureBonusRepository bonusRepository;
  @InjectMocks FindAnnualLeavePolicyService service;

  @Nested
  @DisplayName("Find policy by id")
  class FindById {

    @DisplayName("An existing policy is returned with its tenure bonuses")
    @Test
    void givenExistingPolicy_whenFindById_thenReturnsPolicyWithBonuses() {
      // given
      given(policyRepository.findById(1L))
          .willReturn(Optional.of(AnnualLeavePolicyEntityFixture.korea()));
      given(bonusRepository.findByAnnualLeavePolicyIdOrderByRequiredTenureYearsAsc(1L))
          .willReturn(List.of(TenureBonusEntityFixture.threeYears()));

      // when
      AnnualLeavePolicyResult result = service.handleFindById(1L);

      // then
      assertThat(result.countryCode()).isEqualTo("KR");
      assertThat(result.tenureBonuses()).hasSize(1);
    }

    @DisplayName("A non-existent policy id results in not found error")
    @Test
    void givenNonExistentId_whenFindById_thenThrowsException() {
      // given
      given(policyRepository.findById(999L)).willReturn(Optional.empty());

      // when
      ThrowableAssert.ThrowingCallable find = () -> service.handleFindById(999L);

      // then
      assertThatThrownBy(find).isInstanceOf(AnnualLeavePolicyNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("Find current effective policy")
  class FindCurrent {

    @DisplayName("The most recent effective policy is returned for a country")
    @Test
    void givenActivePolicy_whenFindCurrent_thenReturnsLatestEffective() {
      // given
      FindAnnualLeavePolicyQuery query = new FindAnnualLeavePolicyQuery("KR");
      given(policyRepository.findCurrentByCountryCode(eq("KR"), any()))
          .willReturn(Optional.of(AnnualLeavePolicyEntityFixture.korea()));
      given(bonusRepository.findByAnnualLeavePolicyIdOrderByRequiredTenureYearsAsc(1L))
          .willReturn(List.of(TenureBonusEntityFixture.threeYears()));

      // when
      AnnualLeavePolicyResult result = service.handleFindCurrent(query);

      // then
      assertThat(result.countryCode()).isEqualTo("KR");
      assertThat(result.annualVacationHours()).isEqualTo(120);
    }

    @DisplayName("No policy for a country results in not found error")
    @Test
    void givenNoPolicy_whenFindCurrent_thenThrowsException() {
      // given
      FindAnnualLeavePolicyQuery query = new FindAnnualLeavePolicyQuery("JP");
      given(policyRepository.findCurrentByCountryCode(eq("JP"), any()))
          .willReturn(Optional.empty());

      // when
      ThrowableAssert.ThrowingCallable find = () -> service.handleFindCurrent(query);

      // then
      assertThatThrownBy(find).isInstanceOf(AnnualLeavePolicyNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("Find policy history")
  class FindHistory {

    @DisplayName("All policies for a country are returned with their tenure bonuses")
    @Test
    void givenMultiplePolicies_whenFindHistory_thenReturnsAllWithBonuses() {
      // given
      FindAnnualLeavePolicyQuery query = new FindAnnualLeavePolicyQuery("KR");
      given(policyRepository.findByCountryCodeOrderByEffectiveDateDesc("KR"))
          .willReturn(List.of(AnnualLeavePolicyEntityFixture.korea()));
      given(bonusRepository.findByAnnualLeavePolicyIdInOrderByRequiredTenureYearsAsc(anyList()))
          .willReturn(List.of(TenureBonusEntityFixture.threeYears()));

      // when
      List<AnnualLeavePolicyResult> results = service.handleFindHistory(query);

      // then
      assertThat(results).hasSize(1);
      assertThat(results.get(0).tenureBonuses()).hasSize(1);
    }
  }
}
