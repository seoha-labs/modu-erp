package com.seohalabs.moduerp.organization.presentation.position;

import com.seohalabs.moduerp.organization.application.command.CreatePositionService;
import com.seohalabs.moduerp.organization.application.query.FindPositionsQuery;
import com.seohalabs.moduerp.organization.application.query.FindPositionsService;
import com.seohalabs.moduerp.organization.application.query.PositionResult;
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
@RequestMapping("/positions")
@RequiredArgsConstructor
public class PositionController {

  private final CreatePositionService createService;
  private final FindPositionsService findService;

  @PreAuthorize("hasPermission('erp', 'System', 'can_create_position')")
  @PostMapping
  public Mono<ResponseEntity<Long>> create(@RequestBody CreatePositionRequest request) {
    return createService.handle(PositionMapper.INSTANCE.toCommand(request)).map(ResponseEntity::ok);
  }

  @PreAuthorize("hasPermission('erp', 'System', 'can_list_position')")
  @GetMapping
  public Flux<PositionResult> findAll() {
    return findService.handle(new FindPositionsQuery());
  }
}
