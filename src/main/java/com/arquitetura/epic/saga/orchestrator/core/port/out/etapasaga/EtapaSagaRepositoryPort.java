package com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.SagaEtapa;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface EtapaSagaRepositoryPort {

    SagaEtapa salvar(SagaEtapa sagaEtapa);

    Optional<SagaEtapa> buscarPorId(String etapaId);
}
