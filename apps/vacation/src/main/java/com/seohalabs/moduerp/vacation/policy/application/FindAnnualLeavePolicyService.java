package com.seohalabs.moduerp.vacation.policy.application;

import com.seohalabs.moduerp.vacation.policy.domain.AnnualLeavePolicyEntity;
import com.seohalabs.moduerp.vacation.policy.domain.TenureBonusEntity;
import com.seohalabs.moduerp.vacation.policy.infrastructure.persistence.AnnualLeavePolicyRepository;
import com.seohalabs.moduerp.vacation.policy.infrastructure.persistence.TenureBonusRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindAnnualLeavePolicyService {

  private final AnnualLeavePolicyRepository policyRepository;
  private final TenureBonusRepository bonusRepository;

  public AnnualLeavePolicyResult handleFindById(Long id) {
    AnnualLeavePolicyEntity policy =
        policyRepository.findById(id).orElseThrow(() -> new AnnualLeavePolicyNotFoundException(id));
    List<TenureBonusEntity> bonuses = findBonuses(policy.getId());
    return toResult(policy, bonuses);
  }

  public AnnualLeavePolicyResult handleFindCurrent(FindAnnualLeavePolicyQuery query) {
    AnnualLeavePolicyEntity policy =
        policyRepository
            .findCurrentByCountryCode(query.countryCode(), LocalDate.now())
            .orElseThrow(() -> new AnnualLeavePolicyNotFoundException(query.countryCode()));
    List<TenureBonusEntity> bonuses = findBonuses(policy.getId());
    return toResult(policy, bonuses);
  }

  public List<AnnualLeavePolicyResult> handleFindHistory(FindAnnualLeavePolicyQuery query) {
    List<AnnualLeavePolicyEntity> policies =
        policyRepository.findByCountryCodeOrderByEffectiveDateDesc(query.countryCode());
    List<Long> policyIds = policies.stream().map(AnnualLeavePolicyEntity::getId).toList();
    Map<Long, List<TenureBonusEntity>> bonusMap = groupBonusesByPolicyId(policyIds);
    return policies.stream()
        .map(p -> toResult(p, bonusMap.getOrDefault(p.getId(), List.of())))
        .toList();
  }

  private List<TenureBonusEntity> findBonuses(Long policyId) {
    return bonusRepository.findByAnnualLeavePolicyIdOrderByRequiredTenureYearsAsc(policyId);
  }

  private Map<Long, List<TenureBonusEntity>> groupBonusesByPolicyId(List<Long> policyIds) {
    if (policyIds.isEmpty()) {
      return Map.of();
    }
    return bonusRepository
        .findByAnnualLeavePolicyIdInOrderByRequiredTenureYearsAsc(policyIds)
        .stream()
        .collect(Collectors.groupingBy(TenureBonusEntity::getAnnualLeavePolicyId));
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
