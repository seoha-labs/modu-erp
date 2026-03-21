package com.seohalabs.moduerp.vacation.policy.infrastructure.persistence;

import com.seohalabs.moduerp.vacation.policy.domain.TenureBonusEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenureBonusRepository extends JpaRepository<TenureBonusEntity, Long> {

  List<TenureBonusEntity> findByAnnualLeavePolicyIdOrderByRequiredTenureYearsAsc(
      Long annualLeavePolicyId);

  List<TenureBonusEntity> findByAnnualLeavePolicyIdInOrderByRequiredTenureYearsAsc(
      List<Long> annualLeavePolicyIds);
}
