package com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface EtapaSagaRepositoryPort {

    EtapaSaga salvar(EtapaSaga sagaEtapa);

    Optional<EtapaSaga> buscarPorId(String etapaId);

    List<EtapaSaga> buscarPorSolicitacaoIdETipos(String requestId, List<String> tipos);

    void atualizarEtapas(List<EtapaSaga> etapas);
}
