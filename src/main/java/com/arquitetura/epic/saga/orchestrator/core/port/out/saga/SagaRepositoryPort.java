package com.arquitetura.epic.saga.orchestrator.core.port.out.saga;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface SagaRepositoryPort {

    Saga salvar(Saga saga);

    Optional<Saga> buscarPorId(String sagaId);

}
