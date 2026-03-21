package com.seohalabs.moduerp.vacation.policy.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.seohalabs.moduerp.vacation.policy.application.AnnualLeavePolicyNotFoundException;
import com.seohalabs.moduerp.vacation.policy.application.AnnualLeavePolicyUseCase;
import com.seohalabs.moduerp.vacation.policy.application.DuplicateAnnualLeavePolicyException;
import com.seohalabs.moduerp.vacation.policy.fixture.AnnualLeavePolicyFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AnnualLeavePolicyController.class)
@Import(com.seohalabs.moduerp.vacation.shared.infrastructure.security.VacationSecurityConfig.class)
class AnnualLeavePolicyControllerTest {

  @Autowired MockMvc mockMvc;
  @MockitoBean AnnualLeavePolicyUseCase useCase;

  @Nested
  @DisplayName("Create annual leave policy")
  class Create {

    @DisplayName("A valid request creates a policy and returns 201")
    @Test
    @WithMockUser
    void givenValidRequest_whenPost_thenReturns201() throws Exception {
      // given
      given(useCase.create(any())).willReturn(AnnualLeavePolicyFixture.koreaResult());

      // when & then
      mockMvc
          .perform(
              post("/api/vacation/annual-leave-policies")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(AnnualLeavePolicyFixture.koreaRequestJson()))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.countryCode").value("KR"))
          .andExpect(jsonPath("$.initialVacationHours").value(88))
          .andExpect(jsonPath("$.annualVacationHours").value(120))
          .andExpect(jsonPath("$.tenureBonuses").isArray())
          .andExpect(jsonPath("$.tenureBonuses[0].requiredTenureYears").value(3));
    }

    @DisplayName("A duplicate policy returns 409 Conflict")
    @Test
    @WithMockUser
    void givenDuplicatePolicy_whenPost_thenReturns409() throws Exception {
      // given
      given(useCase.create(any()))
          .willThrow(new DuplicateAnnualLeavePolicyException("KR", LocalDate.of(2026, 1, 1)));

      // when & then
      mockMvc
          .perform(
              post("/api/vacation/annual-leave-policies")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(AnnualLeavePolicyFixture.koreaRequestJson()))
          .andExpect(status().isConflict());
    }

    @DisplayName("An unauthenticated request returns 401")
    @Test
    void givenNoAuth_whenPost_thenReturns401() throws Exception {
      // when & then
      mockMvc
          .perform(
              post("/api/vacation/annual-leave-policies")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(AnnualLeavePolicyFixture.koreaRequestJson()))
          .andExpect(status().isUnauthorized());
    }
  }

  @Nested
  @DisplayName("Find current policy")
  class FindCurrent {

    @DisplayName("The current policy for a country is returned")
    @Test
    @WithMockUser
    void givenExistingPolicy_whenGetCurrent_thenReturns200() throws Exception {
      // given
      given(useCase.findCurrent(any())).willReturn(AnnualLeavePolicyFixture.koreaResult());

      // when & then
      mockMvc
          .perform(get("/api/vacation/annual-leave-policies/current").param("countryCode", "KR"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.countryCode").value("KR"));
    }

    @DisplayName("No policy for a country returns 404")
    @Test
    @WithMockUser
    void givenNoPolicy_whenGetCurrent_thenReturns404() throws Exception {
      // given
      given(useCase.findCurrent(any())).willThrow(new AnnualLeavePolicyNotFoundException("JP"));

      // when & then
      mockMvc
          .perform(get("/api/vacation/annual-leave-policies/current").param("countryCode", "JP"))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("Find policy by id")
  class FindById {

    @DisplayName("An existing policy is returned by id")
    @Test
    @WithMockUser
    void givenExistingId_whenGet_thenReturns200() throws Exception {
      // given
      given(useCase.findById(1L)).willReturn(AnnualLeavePolicyFixture.koreaResult());

      // when & then
      mockMvc
          .perform(get("/api/vacation/annual-leave-policies/1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(1));
    }
  }

  @Nested
  @DisplayName("Find policy history")
  class FindHistory {

    @DisplayName("All policies for a country are returned")
    @Test
    @WithMockUser
    void givenPolicies_whenGetHistory_thenReturnsList() throws Exception {
      // given
      given(useCase.findHistory(any())).willReturn(List.of(AnnualLeavePolicyFixture.koreaResult()));

      // when & then
      mockMvc
          .perform(get("/api/vacation/annual-leave-policies").param("countryCode", "KR"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].countryCode").value("KR"));
    }
  }
}
