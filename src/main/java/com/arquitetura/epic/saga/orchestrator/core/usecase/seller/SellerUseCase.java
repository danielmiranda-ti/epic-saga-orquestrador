package com.arquitetura.epic.saga.orchestrator.core.usecase.seller;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusSagaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.seller.SellerPort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class SellerUseCase implements SellerPort {

    private static final Logger log = LoggerFactory.getLogger(SellerUseCase.class);

    private final SagaService sagaService;

    @Value("${kafka.topic.command.register.store}")
    private String topicNameStore;

    @Value("${kafka.topic.event.failed.seller}")
    private String topicEventFailure;


    @Override
    @Transactional
    public void process() {
        try {
            var tiposEtapas = List.of(TipoEtapaEnum.SELLER_REGISTRATION, TipoEtapaEnum.STORE_REGISTRATION);
            var regras = Map.of(
                    TipoEtapaEnum.SELLER_REGISTRATION, StatusEtapaEnum.SUCESSO,
                    TipoEtapaEnum.STORE_REGISTRATION, StatusEtapaEnum.EM_ANDAMENTO
            );
            sagaService.processarEtapasSaga("", "",
                    tiposEtapas,
                    regras,
                    TipoEtapaEnum.STORE_REGISTRATION,
                    topicNameStore,
                    saga -> saga.setSellerId("sellerId"));
        } catch (Exception ex) {
            log.error("Erro ao processar seller: {}", ex.getMessage(), ex);
            Consumer<Saga> sagaUpdater = saga -> {
                saga.setSellerId("sellerId");
                saga.setStatus(StatusSagaEnum.FALHA);
            };
            sagaService.processarEtapasSaga(
                    "",
                    "",
                    List.of(TipoEtapaEnum.SELLER_REGISTRATION),
                    Map.of(TipoEtapaEnum.SELLER_REGISTRATION, StatusEtapaEnum.FALHA),
                    TipoEtapaEnum.STORE_REGISTRATION,
                    topicNameStore,
                    sagaUpdater
            );
        }
    }
}