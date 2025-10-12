package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.adapter;

import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity.EtapaSagaEntity;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga.EtapaSagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.mapper.SagaEtapaMapper;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.repository.SagaEtapaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<EtapaSaga> buscarPorSolicitacaoIdETipos(String requestId, List<String> tipos) {
        UUID solicitacaoUUID = UUID.fromString(requestId);
        return sagaEtapaRepository.findBySaga_SolicitacaoIdAndNomeEtapaIn(solicitacaoUUID, tipos)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public void atualizarEtapas(List<EtapaSaga> etapas) {
        List<EtapaSagaEntity> entidades = etapas.stream()
                .map(mapper::toEntity) // Certifique-se que o status correto está em cada entidade
                .toList();
        sagaEtapaRepository.saveAll(entidades);
    }


}
