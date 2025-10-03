package com.arquitetura.epic.saga.orchestrator.core.usecase.cadastrojuridico;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.ListenerEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.SagaTopico;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.SagaEtapa;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusSagaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.cadastrojuridico.CadastroJuridicoPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga.EtapaSagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.saga.SagaRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CadastroJuridicoUseCase implements CadastroJuridicoPort {

    private static final Logger log = LoggerFactory.getLogger(CadastroJuridicoUseCase.class);

    private final ProdutorMensagemPort produtorMensagemPort;
    private final EtapaSagaRepositoryPort etapaSagaRepositoryPort;
    private final SagaRepositoryPort sagaRepositoryPort;

    @Transactional
    public void processar(SagaTopico sagaTopico, ListenerEnum listenerEnum) {
        Optional<SagaEtapa> sagaEtapa = etapaSagaRepositoryPort.buscarPorId(sagaTopico.getEtapaId());

        if (listenerEnum == null) {
            log.warn("ListenerEnum nulo não tratado");
            return;
        }

        switch(listenerEnum) {
            case SUCESSO -> sagaEtapa.ifPresent(etapa -> {
                produtorMensagemPort.enviaMensagem(sagaEtapa, "verificacao-financeira-start");
                atualizarStatusEtapa(etapa, StatusEtapaEnum.SUCESSO);
            });
            case FALHA -> {
                sagaEtapa.ifPresent(etapa -> {
                    atualizarStatusEtapa(etapa, StatusEtapaEnum.FALHA);
                    log.warn("Etapa {} marcada como FALHA", etapa.getNomeEtapa());
                });

                sagaRepositoryPort.buscarPorId(sagaTopico.getSagaId())
                        .ifPresent(saga -> {
                            saga.setStatus(StatusSagaEnum.FALHA);
                            sagaRepositoryPort.salvar(saga);
                            log.warn("Saga {} marcada como FALHA", saga.getId());
                        });
            }
            default -> log.warn("ListenerEnum {} não tratado", listenerEnum);
        }
    }

    private void atualizarStatusEtapa(SagaEtapa etapa, StatusEtapaEnum status) {
        etapa.setStatus(status);
        etapaSagaRepositoryPort.salvar(etapa);
    }
}
