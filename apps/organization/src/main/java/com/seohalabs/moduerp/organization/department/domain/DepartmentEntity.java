package com.seohalabs.moduerp.organization.department.domain;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("departments")
@Getter
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentEntity {

  @Id
  private Long id;

  private String name;
  private Long parentId;

  public Optional<Long> parentId() {
    return Optional.ofNullable(parentId);
  }
}
