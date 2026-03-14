package com.seohalabs.moduerp.organization.position.application;

import com.seohalabs.moduerp.organization.position.domain.PositionEntity;
import com.seohalabs.moduerp.organization.position.domain.PositionFactory;
import com.seohalabs.moduerp.organization.shared.infrastructure.openfga.OpenFgaTupleService;
import com.seohalabs.moduerp.organization.position.infrastructure.persistence.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatePositionService {

  private final PositionRepository positionRepository;
  private final OpenFgaTupleService tupleService;

  public Mono<Long> handle(CreatePositionCommand command) {
    PositionEntity position = PositionFactory.create(command.name(), command.level());
    return positionRepository.save(position).flatMap(this::registerInFga);
  }

  private Mono<Long> registerInFga(PositionEntity saved) {
    return tupleService.writePositionCreation(saved.getId()).thenReturn(saved.getId());
  }
}
