package com.seohalabs.moduerp.organization.employee.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("employees")
@Getter
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeEntity {

  @Id
  private Long id;

  private String name;
  private String email;
  private Long departmentId;
  private Long positionId;
  private EmploymentStatusType status;
  private String keycloakId;
  private Long[] roleIds;

  public void resign() {
    this.status = EmploymentStatusType.RESIGNED;
  }

  public void assignKeycloakId(String keycloakId) {
    this.keycloakId = keycloakId;
  }
}
