package com.seohalabs.moduerp.organization.employee.presentation;

import com.seohalabs.moduerp.organization.employee.application.GrantHrManagerCommand;
import com.seohalabs.moduerp.organization.employee.application.GrantHrManagerService;
import com.seohalabs.moduerp.organization.employee.application.RegisterEmployeeService;
import com.seohalabs.moduerp.organization.employee.application.ResignEmployeeCommand;
import com.seohalabs.moduerp.organization.employee.application.ResignEmployeeService;
import com.seohalabs.moduerp.organization.employee.application.RevokeHrManagerCommand;
import com.seohalabs.moduerp.organization.employee.application.RevokeHrManagerService;
import com.seohalabs.moduerp.organization.employee.application.EmployeeResult;
import com.seohalabs.moduerp.organization.employee.application.FindEmployeeQuery;
import com.seohalabs.moduerp.organization.employee.application.FindEmployeeService;
import com.seohalabs.moduerp.organization.employee.application.FindEmployeesQuery;
import com.seohalabs.moduerp.organization.employee.application.FindEmployeesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final RegisterEmployeeService registerService;
  private final ResignEmployeeService resignService;
  private final FindEmployeeService findOneService;
  private final FindEmployeesService findAllService;
  private final GrantHrManagerService grantHrManagerService;
  private final RevokeHrManagerService revokeHrManagerService;

  @PreAuthorize("hasPermission('erp', 'System', 'can_create_employee')")
  @PostMapping
  public Mono<ResponseEntity<Long>> register(@RequestBody RegisterEmployeeRequest request) {
    return registerService.handle(EmployeeMapper.INSTANCE.toCommand(request))
        .map(ResponseEntity::ok);
  }

  @PreAuthorize("hasPermission(#id, 'Employee', 'can_resign')")
  @PostMapping("/{id}/resign")
  public Mono<ResponseEntity<Void>> resign(@PathVariable Long id) {
    return resignService.handle(new ResignEmployeeCommand(id))
        .thenReturn(ResponseEntity.<Void>ok().build());
  }

  @PreAuthorize("hasPermission('erp', 'System', 'can_grant_hr_manager')")
  @PostMapping("/{id}/hr-manager")
  public Mono<ResponseEntity<Void>> grantHrManager(@PathVariable Long id) {
    return grantHrManagerService.handle(new GrantHrManagerCommand(id))
        .thenReturn(ResponseEntity.<Void>ok().build());
  }

  @PreAuthorize("hasPermission('erp', 'System', 'can_grant_hr_manager')")
  @DeleteMapping("/{id}/hr-manager")
  public Mono<ResponseEntity<Void>> revokeHrManager(@PathVariable Long id) {
    return revokeHrManagerService.handle(new RevokeHrManagerCommand(id))
        .thenReturn(ResponseEntity.<Void>ok().build());
  }

  @PreAuthorize("hasPermission(#id, 'Employee', 'can_view')")
  @GetMapping("/{id}")
  public Mono<EmployeeResult> findOne(@PathVariable Long id) {
    return findOneService.handle(new FindEmployeeQuery(id));
  }

  @PreAuthorize("hasPermission('erp', 'System', 'can_list_employee')")
  @GetMapping
  public Flux<EmployeeResult> findAll() {
    return findAllService.handle(new FindEmployeesQuery());
  }
}
