package com.seohalabs.moduerp.organization.role.presentation;

import com.seohalabs.moduerp.organization.role.application.CreateRoleService;
import com.seohalabs.moduerp.organization.role.application.FindRolesQuery;
import com.seohalabs.moduerp.organization.role.application.FindRolesService;
import com.seohalabs.moduerp.organization.role.application.RoleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

  private final CreateRoleService createService;
  private final FindRolesService findService;

  @PreAuthorize("hasPermission('erp', 'System', 'can_create_role')")
  @PostMapping
  public Mono<ResponseEntity<Long>> create(@RequestBody CreateRoleRequest request) {
    return createService.handle(RoleMapper.INSTANCE.toCommand(request)).map(ResponseEntity::ok);
  }

  @PreAuthorize("hasPermission('erp', 'System', 'can_list_role')")
  @GetMapping
  public Flux<RoleResult> findAll() {
    return findService.handle(new FindRolesQuery());
  }
}
