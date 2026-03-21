package com.seohalabs.moduerp.vacation.policy.application;

import com.seohalabs.moduerp.vacation.policy.domain.AnnualLeavePolicyEntity;
import com.seohalabs.moduerp.vacation.policy.domain.AnnualLeavePolicyFactory;
import com.seohalabs.moduerp.vacation.policy.domain.TenureBonusEntity;
import com.seohalabs.moduerp.vacation.policy.domain.TenureBonusFactory;
import com.seohalabs.moduerp.vacation.policy.infrastructure.persistence.AnnualLeavePolicyRepository;
import com.seohalabs.moduerp.vacation.policy.infrastructure.persistence.TenureBonusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAnnualLeavePolicyService {

  private final AnnualLeavePolicyRepository policyRepository;
  private final TenureBonusRepository bonusRepository;

  public AnnualLeavePolicyResult handle(CreateAnnualLeavePolicyCommand command) {
    validateNoDuplicate(command);
    AnnualLeavePolicyEntity saved = savePolicy(command);
    List<TenureBonusEntity> bonuses = saveBonuses(saved.getId(), command.tenureBonuses());
    return toResult(saved, bonuses);
  }

  private void validateNoDuplicate(CreateAnnualLeavePolicyCommand command) {
    if (policyRepository.existsByCountryCodeAndEffectiveDate(
        command.countryCode(), command.effectiveDate())) {
      throw new DuplicateAnnualLeavePolicyException(command.countryCode(), command.effectiveDate());
    }
  }

  private AnnualLeavePolicyEntity savePolicy(CreateAnnualLeavePolicyCommand command) {
    AnnualLeavePolicyEntity policy =
        AnnualLeavePolicyFactory.create(
            command.countryCode(),
            command.initialVacationHours(),
            command.annualVacationHours(),
            command.effectiveDate());
    return policyRepository.save(policy);
  }

  private List<TenureBonusEntity> saveBonuses(
      Long policyId, List<TenureBonusCommand> bonusCommands) {
    if (bonusCommands == null || bonusCommands.isEmpty()) {
      return List.of();
    }
    List<TenureBonusEntity> entities =
        bonusCommands.stream()
            .map(
                cmd ->
                    TenureBonusFactory.create(
                        policyId, cmd.requiredTenureYears(), cmd.bonusHours(), cmd.maxTotalHours()))
            .toList();
    return bonusRepository.saveAll(entities);
  }

  private AnnualLeavePolicyResult toResult(
      AnnualLeavePolicyEntity policy, List<TenureBonusEntity> bonuses) {
    return new AnnualLeavePolicyResult(
        policy.getId(),
        policy.getCountryCode(),
        policy.getInitialVacationHours(),
        policy.getAnnualVacationHours(),
        policy.getEffectiveDate(),
        policy.getCreatedAt(),
        AnnualLeavePolicyDomainMapper.INSTANCE.toResults(bonuses));
  }
}
