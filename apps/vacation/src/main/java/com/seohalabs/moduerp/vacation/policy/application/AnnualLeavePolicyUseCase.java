package com.seohalabs.moduerp.vacation.policy.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnnualLeavePolicyUseCase {

  private final CreateAnnualLeavePolicyService createService;
  private final FindAnnualLeavePolicyService findService;

  public AnnualLeavePolicyResult create(CreateAnnualLeavePolicyCommand command) {
    return createService.handle(command);
  }

  public AnnualLeavePolicyResult findById(Long id) {
    return findService.handleFindById(id);
  }

  public AnnualLeavePolicyResult findCurrent(FindAnnualLeavePolicyQuery query) {
    return findService.handleFindCurrent(query);
  }

  public List<AnnualLeavePolicyResult> findHistory(FindAnnualLeavePolicyQuery query) {
    return findService.handleFindHistory(query);
  }
}
