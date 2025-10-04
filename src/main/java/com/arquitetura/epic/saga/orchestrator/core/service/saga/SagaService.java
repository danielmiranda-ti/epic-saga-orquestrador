package com.arquitetura.epic.saga.orchestrator.core.service.saga;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Solicitacao;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.*;
import com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga.EtapaSagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.saga.SagaRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepositoryPort sagaRepositoryPort;
    private final EtapaSagaRepositoryPort etapaSagaRepositoryPort;
    private final JsonUtil jsonUtil;

    @Transactional
    public Saga registrarSaga(Solicitacao solicitacao) {

        if (solicitacao == null || solicitacao.getSolicitacaoId() == null) {
            throw new IllegalArgumentException("Solicitação não pode ser nula.");
        }

        var saga = Saga.builder()
                .solicitacaoId(solicitacao.getSolicitacaoId())
                .status(StatusSagaEnum.EM_ANDAMENTO)
                .build();

        var sagaPersistida = sagaRepositoryPort.salvar(saga);

        List<EtapaInfo> etapas = List.of(
                EtapaInfo.builder()
                        .nome(TipoEtapaEnum.CADASTRAR_DADOS_PESSOAIS.name())
                        .dados(solicitacao.getDadosPessoais()).
                        status(StatusEtapaEnum.EM_ANDAMENTO).build(),
                EtapaInfo.builder()
                        .nome(TipoEtapaEnum.CADASTRAR_DADOS_BANCARIO.name())
                        .dados(solicitacao.getDadosBancarios())
                        .status(StatusEtapaEnum.PENDENTE).build(),
                EtapaInfo.builder()
                        .nome(TipoEtapaEnum.CADASTRAR_DADOS_JURIDICO.name())
                        .dados(solicitacao.getDadosJuridicos()).
                        status(StatusEtapaEnum.PENDENTE).build()
        );

        etapas.forEach(etapa -> {
            var etapaSaga = criarEtapa(sagaPersistida, etapa.getNome(), etapa.getDados(), etapa.getStatus());
            sagaPersistida.getEtapasSaga().add(etapaSaga);
        });

        return sagaPersistida;
    }

    private EtapaSaga criarEtapa(Saga saga, String nomeEtapa, Object payload, StatusEtapaEnum statusEtapaEnum) {
        var etapa = EtapaSaga.builder()
                .saga(saga)
                .nomeEtapa(nomeEtapa)
                .payload(jsonUtil.toJson(payload))
                .status(statusEtapaEnum)
                .build();

        return etapaSagaRepositoryPort.salvar(etapa);
    }

    public Optional<Saga> buscarSagaPorSolicitacaoId(String solicitacaoId) {
        return sagaRepositoryPort.buscarPorSolicitacaoId(solicitacaoId);
    }

}
