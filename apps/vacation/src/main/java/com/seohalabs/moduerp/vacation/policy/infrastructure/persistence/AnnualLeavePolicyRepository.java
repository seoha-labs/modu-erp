package com.seohalabs.moduerp.vacation.policy.infrastructure.persistence;

import com.seohalabs.moduerp.vacation.policy.domain.AnnualLeavePolicyEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnnualLeavePolicyRepository extends JpaRepository<AnnualLeavePolicyEntity, Long> {

  List<AnnualLeavePolicyEntity> findByCountryCodeOrderByEffectiveDateDesc(String countryCode);

  boolean existsByCountryCodeAndEffectiveDate(String countryCode, LocalDate effectiveDate);

  @Query(
      "SELECT p FROM AnnualLeavePolicyEntity p"
          + " WHERE p.countryCode = :countryCode"
          + " AND p.effectiveDate <= :date"
          + " ORDER BY p.effectiveDate DESC"
          + " LIMIT 1")
  Optional<AnnualLeavePolicyEntity> findCurrentByCountryCode(String countryCode, LocalDate date);
}
