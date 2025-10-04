package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.adapter;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.core.port.out.saga.SagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.entity.SagaEntity;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.mapper.SagaMapper;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.repository.SagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SagaRepositoryAdapter implements SagaRepositoryPort {

    private final SagaRepository sagaRepository;
    private final SagaMapper sagaMapper;

    public Saga salvar(Saga saga) {
        SagaEntity sagaEntity = sagaMapper.toEntity(saga);
        var sagaPersite = sagaRepository.save(sagaEntity);
        return sagaMapper.toDomain(sagaPersite);
    }

    public Optional<Saga> buscarPorId(String sagaId) {
        return sagaRepository.findById(UUID.fromString(sagaId)).map(sagaMapper::toDomain);
    }

    public Optional<Saga> buscarPorSolicitacaoId(String solicitacaoId) {
        return sagaRepository.findBySolicitacaoId(UUID.fromString(solicitacaoId)).map(sagaMapper::toDomain);
    }
}
