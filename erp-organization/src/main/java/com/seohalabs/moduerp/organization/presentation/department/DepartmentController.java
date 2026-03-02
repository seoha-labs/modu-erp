package com.seohalabs.moduerp.organization.presentation.department;

import com.seohalabs.moduerp.organization.application.command.CreateDepartmentService;
import com.seohalabs.moduerp.organization.application.query.DepartmentResult;
import com.seohalabs.moduerp.organization.application.query.FindDepartmentsQuery;
import com.seohalabs.moduerp.organization.application.query.FindDepartmentsService;
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
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

  private final CreateDepartmentService createService;
  private final FindDepartmentsService findService;

  @PreAuthorize("hasPermission('erp', 'System', 'can_create_department')")
  @PostMapping
  public Mono<ResponseEntity<Long>> create(@RequestBody CreateDepartmentRequest request) {
    return createService.handle(DepartmentMapper.INSTANCE.toCommand(request)).map(ResponseEntity::ok);
  }

  @PreAuthorize("hasPermission('erp', 'System', 'can_list_department')")
  @GetMapping
  public Flux<DepartmentResult> findAll() {
    return findService.handle(new FindDepartmentsQuery());
  }
}
