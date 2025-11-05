package com.arquitetura.epic.saga.orchestrator.core.usecase.store;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Seller;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Store;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusSagaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.store.StorePort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class StoreUseCase implements StorePort {

    private static final Logger log = LoggerFactory.getLogger(StoreUseCase.class);

    private final SagaService sagaService;

    @Value("${kafka.topic.command.register.financial}")
    private String topicCommandRegisterFinancial;

    @Value("${kafka.topic.command.compensate.seller}")
    private String topicCommandCompensateSeller;

    @Value("${kafka.topic.command.compensate.store}")
    private String topicCommandCompensateStore;

    @Override
    @Retryable(
            recover = "handleCompensationEventRegisterSucess",
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000))
    public void processEventRegisterSucess(Store store) {
        var regras = Map.of(
                TipoEtapaEnum.STORE_REGISTRATION, StatusEtapaEnum.SUCCESS,
                TipoEtapaEnum.BANK_REGISTRATION, StatusEtapaEnum.IN_PROGRESS
        );
        sagaService.processarEtapasSaga(
                store.getRequestId(),
                regras,
                TipoEtapaEnum.BANK_REGISTRATION,
                topicCommandRegisterFinancial,
                s -> {}
        );

    }

    @Recover
    public void handleCompensationEventRegisterSucess(Exception ex, Seller seller) {
        // solicito a compensação
        var regras = Map.of(
                TipoEtapaEnum.STORE_REGISTRATION, StatusEtapaEnum.IN_COMPENSATION
        );
        sagaService.processarEtapasSaga(
                seller.getRequestId(),
                regras,
                TipoEtapaEnum.SELLER_REGISTRATION,
                topicCommandCompensateStore,
                saga -> {
                    saga.setSellerId(seller.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });
    }

    @Override
    public void processEventRegisterFailure(Store store) {
        Consumer<Saga> sagaUpdater = saga -> {
            saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
        };
        sagaService.processarEtapasSaga(
                store.getRequestId(),
                Map.of(TipoEtapaEnum.SELLER_REGISTRATION, StatusEtapaEnum.FAILURE),
                TipoEtapaEnum.STORE_REGISTRATION,
                topicCommandCompensateSeller,
                sagaUpdater
        );
    }

    @Override
    public void processEventCompensateSucess(Store store) {

        var regras = Map.of(
                TipoEtapaEnum.STORE_REGISTRATION, StatusEtapaEnum.COMPENSATED
        );

        sagaService.processarEtapasSaga(
                store.getRequestId(),
                regras,
                TipoEtapaEnum.STORE_REGISTRATION,
                topicCommandCompensateSeller,
                saga -> {
                    saga.setSellerId(store.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });


    }

    @Override
    public void processEventCompensateFailure(Store store) {

        var regras = Map.of(
                TipoEtapaEnum.STORE_REGISTRATION, StatusEtapaEnum.COMPENSATED_FAILURE
        );

        sagaService.processarEtapasSaga(
                store.getRequestId(),
                regras,
                TipoEtapaEnum.STORE_REGISTRATION,
                topicCommandCompensateSeller,
                saga -> {
                    saga.setSellerId(store.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });
    }
}