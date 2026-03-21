package com.seohalabs.moduerp.vacation.policy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "annual_leave_policies")
@Getter
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnualLeavePolicyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "country_code", nullable = false, length = 2)
  private String countryCode;

  @Column(name = "initial_vacation_hours", nullable = false)
  private Integer initialVacationHours;

  @Column(name = "annual_vacation_hours", nullable = false)
  private Integer annualVacationHours;

  @Column(name = "effective_date", nullable = false)
  private LocalDate effectiveDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
