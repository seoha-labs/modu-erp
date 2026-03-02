package com.seohalabs.moduerp.organization.application.query;

import com.seohalabs.moduerp.organization.infrastructure.persistence.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindPositionsService {

  private final PositionRepository positionRepository;

  public Flux<PositionResult> handle(FindPositionsQuery query) {
    return positionRepository.findAll().map(PositionDomainMapper.INSTANCE::toResult);
  }
}
