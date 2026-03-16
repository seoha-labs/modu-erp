package com.seohalabs.moduerp.vacation.policy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tenure_bonuses")
@Getter
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TenureBonusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "annual_leave_policy_id", nullable = false)
  private Long annualLeavePolicyId;

  @Column(name = "required_tenure_years", nullable = false)
  private Integer requiredTenureYears;

  @Column(name = "bonus_hours", nullable = false)
  private Integer bonusHours;

  @Column(name = "max_total_hours")
  private Integer maxTotalHours;
}
