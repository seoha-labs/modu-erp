package com.seohalabs.moduerp.vacation.policy.presentation;

import com.seohalabs.moduerp.vacation.policy.application.AnnualLeavePolicyResult;
import com.seohalabs.moduerp.vacation.policy.application.AnnualLeavePolicyUseCase;
import com.seohalabs.moduerp.vacation.policy.application.FindAnnualLeavePolicyQuery;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vacation/annual-leave-policies")
@RequiredArgsConstructor
public class AnnualLeavePolicyController {

  private final AnnualLeavePolicyUseCase useCase;

  @PostMapping
  public ResponseEntity<AnnualLeavePolicyResponse> create(
      @Valid @RequestBody CreateAnnualLeavePolicyRequest request) {
    AnnualLeavePolicyResult result =
        useCase.create(AnnualLeavePolicyMapper.INSTANCE.toCommand(request));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(AnnualLeavePolicyMapper.INSTANCE.toResponse(result));
  }

  @GetMapping("/current")
  public ResponseEntity<AnnualLeavePolicyResponse> findCurrent(@RequestParam String countryCode) {
    AnnualLeavePolicyResult result =
        useCase.findCurrent(new FindAnnualLeavePolicyQuery(countryCode));
    return ResponseEntity.ok(AnnualLeavePolicyMapper.INSTANCE.toResponse(result));
  }

  @GetMapping
  public ResponseEntity<List<AnnualLeavePolicyResponse>> findHistory(
      @RequestParam String countryCode) {
    List<AnnualLeavePolicyResult> results =
        useCase.findHistory(new FindAnnualLeavePolicyQuery(countryCode));
    return ResponseEntity.ok(AnnualLeavePolicyMapper.INSTANCE.toResponses(results));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AnnualLeavePolicyResponse> findById(@PathVariable Long id) {
    AnnualLeavePolicyResult result = useCase.findById(id);
    return ResponseEntity.ok(AnnualLeavePolicyMapper.INSTANCE.toResponse(result));
  }
}
