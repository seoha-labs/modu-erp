package com.seohalabs.moduerp.vacation.policy.presentation;

import com.seohalabs.moduerp.vacation.policy.application.AnnualLeavePolicyNotFoundException;
import com.seohalabs.moduerp.vacation.policy.application.DuplicateAnnualLeavePolicyException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AnnualLeavePolicyExceptionHandler {

  @ExceptionHandler(AnnualLeavePolicyNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleNotFound(AnnualLeavePolicyNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(DuplicateAnnualLeavePolicyException.class)
  public ResponseEntity<Map<String, String>> handleDuplicate(
      DuplicateAnnualLeavePolicyException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
  }
}
