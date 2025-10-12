package com.arquitetura.epic.saga.orchestrator.core.service.saga;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Solicitacao;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.*;
import com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga.EtapaSagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.saga.SagaRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class SagaService {

    private static final Logger log = LoggerFactory.getLogger(SagaService.class);

    private final ProdutorMensagemPort produtorMensagemPort;
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
                        .nome(TipoEtapaEnum.SELLER_REGISTRATION.name())
                        .dados(solicitacao.getDadosPessoais()).
                        status(StatusEtapaEnum.EM_ANDAMENTO).build(),
                EtapaInfo.builder()
                        .nome(TipoEtapaEnum.BANK_REGISTRATION.name())
                        .dados(solicitacao.getDadosBancarios())
                        .status(StatusEtapaEnum.PENDENTE).build(),
                EtapaInfo.builder()
                        .nome(TipoEtapaEnum.STORE_REGISTRATION.name())
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

    @Transactional
    public void processarEtapasSaga(
            String requestId,
            String sellerId,
            List<TipoEtapaEnum> tiposEtapa,
            Map<TipoEtapaEnum, StatusEtapaEnum> regrasStatus,
            TipoEtapaEnum etapaParaMensagem,
            String topicName,
            Consumer<Saga> sagaUpdater
    ) {
        sagaRepositoryPort.buscarPorId(requestId)
                .ifPresentOrElse(saga -> {
                    atualizarSaga(sagaUpdater, saga);

                    List<EtapaSaga> etapas = etapaSagaRepositoryPort.buscarPorSolicitacaoIdETipos(
                            requestId, tiposEtapa.stream().map(TipoEtapaEnum::name).toList()
                    );

                    etapas.forEach(etapa -> {
                        TipoEtapaEnum tipo = TipoEtapaEnum.valueOf(etapa.getNomeEtapa());
                        StatusEtapaEnum novoStatus = regrasStatus.get(tipo);
                        if (novoStatus != null) etapa.setStatus(novoStatus);
                    });

                    etapaSagaRepositoryPort.atualizarEtapas(etapas);

                    Optional.ofNullable(etapaParaMensagem)
                            .flatMap(tipo -> etapas.stream()
                                    .filter(etapa -> tipo.name().equals(etapa.getNomeEtapa()))
                                    .findFirst())
                            .ifPresentOrElse(
                                    etapa -> produtorMensagemPort.enviaMensagem(Optional.of(etapa), topicName),
                                    () -> log.info("Nenhuma etapa correspondente encontrada para etapaParaMensagem: {}", etapaParaMensagem)
                            );
                    },
                        () -> log.warn("não foi encontrado saga para a requisição {}", requestId)
                );
    }

    private void atualizarSaga(Consumer<Saga> sagaUpdater, Saga saga) {
        var hashCodeOriginal = saga.hashCode();

        sagaUpdater.accept(saga);

        var hashCodeAffter = saga.hashCode();

        if(hashCodeOriginal != hashCodeAffter) {
            sagaRepositoryPort.salvar(saga);
        }
    }

}
