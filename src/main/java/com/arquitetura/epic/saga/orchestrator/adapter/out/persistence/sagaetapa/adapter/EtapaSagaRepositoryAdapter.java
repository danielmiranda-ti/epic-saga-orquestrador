package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.adapter;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga.EtapaSagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.mapper.SagaEtapaMapper;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.repository.SagaEtapaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EtapaSagaRepositoryAdapter implements EtapaSagaRepositoryPort {

    private final SagaEtapaRepository sagaEtapaRepository;
    private final SagaEtapaMapper mapper;

    @Override
    public EtapaSaga salvar(EtapaSaga sagaEtapa) {
        var sagaEtapaEntity = mapper.toEntity(sagaEtapa);
        var etapa = sagaEtapaRepository.save(sagaEtapaEntity);

        return mapper.toDomain(etapa);
    }

    public Optional<EtapaSaga> buscarPorId(String etapaId) {
        return sagaEtapaRepository.findById(UUID.fromString(etapaId))
                .map(mapper::toDomain);
    }


}
