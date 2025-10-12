package com.arquitetura.epic.saga.orchestrator.core.usecase.store;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.store.StorePort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreUseCase implements StorePort {

    private static final Logger log = LoggerFactory.getLogger(StoreUseCase.class);

    private final SagaService sagaService;

    @Value("${kafka.topic.command.register.financial}")
    private String topicNameStore;

    @Value("${kafka.topic.event.failed.store}")
    private String topicEventFailure;

    @Override
    public void process() {
        try {
            var regras = Map.of(
                    TipoEtapaEnum.STORE_REGISTRATION, StatusEtapaEnum.SUCESSO,
                    TipoEtapaEnum.BANK_REGISTRATION, StatusEtapaEnum.EM_ANDAMENTO
            );
            sagaService.processarEtapasSaga(
                    "",
                    "",
                    List.of(TipoEtapaEnum.STORE_REGISTRATION, TipoEtapaEnum.BANK_REGISTRATION),
                    regras,
                    TipoEtapaEnum.BANK_REGISTRATION,
                    topicNameStore,
                    s -> {}
            );

        } catch (Exception ex) {
            log.error("Erro ao processar seller: {}", ex.getMessage(), ex);
            var tipoEtapa = TipoEtapaEnum.STORE_REGISTRATION;
            sagaService.processarEtapasSaga(
                    "",
                    "",
                    List.of(tipoEtapa),
                    Map.of(tipoEtapa, StatusEtapaEnum.FALHA),
                    tipoEtapa,
                    topicEventFailure,
                    saga -> {}
            );
        }
    }
}