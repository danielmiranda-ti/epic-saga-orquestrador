package com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface EtapaSagaRepositoryPort {

    EtapaSaga salvar(EtapaSaga sagaEtapa);

    Optional<EtapaSaga> buscarPorId(String etapaId);
}
