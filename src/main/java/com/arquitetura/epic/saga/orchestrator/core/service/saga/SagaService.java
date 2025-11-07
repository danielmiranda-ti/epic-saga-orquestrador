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

import java.util.*;
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
                .status(StatusSagaEnum.IN_PROGRESS)
                .build();

        var sagaPersistida = sagaRepositoryPort.salvar(saga);

        List<EtapaInfo> etapas = List.of(
                EtapaInfo.builder()
                        .nome(TipoEtapaEnum.SELLER_REGISTRATION.name())
                        .dados(solicitacao.getDadosPessoais()).
                        status(StatusEtapaEnum.IN_PROGRESS).build(),
                EtapaInfo.builder()
                        .nome(TipoEtapaEnum.BANK_REGISTRATION.name())
                        .dados(solicitacao.getDadosBancarios())
                        .status(StatusEtapaEnum.PENDING).build(),
                EtapaInfo.builder()
                        .nome(TipoEtapaEnum.STORE_REGISTRATION.name())
                        .dados(solicitacao.getDadosJuridicos()).
                        status(StatusEtapaEnum.PENDING).build()
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
            Map<TipoEtapaEnum, StatusEtapaEnum> regrasStatus,
            TipoEtapaEnum nextStep,
            String topicName,
            Consumer<Saga> sagaUpdater
    ) {
        Optional<Saga> sagaOpt = sagaRepositoryPort.buscarPorSolicitacaoId(requestId);
        if (sagaOpt.isEmpty()) {
            log.warn("=== Não foi encontrado saga para a requisição {}", requestId);
            return;
        }

        Saga saga = sagaOpt.get();
        atualizarSaga(sagaUpdater, saga);

        List<EtapaSaga> etapas = buscarEtapasRelacionadas(saga.getId(), regrasStatus, nextStep);
        atualizarStatusDasEtapas(etapas, regrasStatus, saga);
        etapaSagaRepositoryPort.atualizarEtapas(etapas);

        enviarMensagemSeNecessario(etapas, nextStep, topicName);
    }

    private List<EtapaSaga> buscarEtapasRelacionadas(UUID sagaId,
                                                     Map<TipoEtapaEnum, StatusEtapaEnum> regrasStatus,
                                                     TipoEtapaEnum nextStep) {
        List<String> etapasABuscar = new ArrayList<>(
                regrasStatus.keySet().stream()
                        .map(TipoEtapaEnum::name)
                        .toList()
        );

        if (nextStep != null) {
            etapasABuscar.add(nextStep.name());
        }

        return etapaSagaRepositoryPort.buscarPorSagaIdETipos(sagaId, etapasABuscar);
    }

    private void atualizarStatusDasEtapas(List<EtapaSaga> etapas, Map<TipoEtapaEnum, StatusEtapaEnum> regrasStatus, Saga saga) {
        for (EtapaSaga etapa : etapas) {
            TipoEtapaEnum tipo = TipoEtapaEnum.valueOf(etapa.getNomeEtapa());
            StatusEtapaEnum novoStatus = regrasStatus.get(tipo);
            if (novoStatus != null) {
                etapa.setStatus(novoStatus);
            }
            etapa.setSaga(saga);
        }
    }

    private void enviarMensagemSeNecessario(List<EtapaSaga> etapas, TipoEtapaEnum nextStep, String topicName) {
        if (nextStep == null) {
            log.info("==== Nenhuma etapa informada para envio de mensagem.");
            return;
        }

        Optional<EtapaSaga> etapaParaMensagem = etapas.stream()
                .filter(etapa -> nextStep.name().equals(etapa.getNomeEtapa()))
                .findFirst();

        if (etapaParaMensagem.isEmpty()) {
            log.info("==== Nenhuma etapa correspondente encontrada para envio da mensagem: {}", nextStep);
            return;
        }

        if (topicName == null || topicName.isBlank()) {
            log.info("==== Tópico não informado. Mensagem não será enviada para a etapa: {}", nextStep);
            return;
        }

        produtorMensagemPort.enviaMensagem(etapaParaMensagem, topicName);
    }

    private void atualizarSaga(Consumer<Saga> sagaUpdater, Saga saga) {
        // Salva o hashCode original da saga antes de qualquer alteração
        var hashCodeOriginal = saga.hashCode();

        // Executa a função recebida, que pode modificar atributos da saga
        sagaUpdater.accept(saga);

        // Calcula o hashCode após a possível atualização
        var hashCodeAffter = saga.hashCode();

        // Se o hashCode mudou, significa que algum atributo relevante foi alterado
        if (hashCodeOriginal != hashCodeAffter) {
            // Persiste a saga atualizada no repositório
            sagaRepositoryPort.salvar(saga);
        }
    }

}
