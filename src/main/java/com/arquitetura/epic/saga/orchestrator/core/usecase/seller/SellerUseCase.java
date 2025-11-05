package com.arquitetura.epic.saga.orchestrator.core.usecase.seller;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Seller;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusSagaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.seller.SellerPort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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

//    @Value("${kafka.topic.command.compensate.seller}")
//    private String topicCommandCompensateSeller;


    @Retryable(
            recover = "handleCompensationEventRegisterSucess",
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000))
    public void processEventRegisterSucess(Seller seller) {

        RetryContext context = org.springframework.retry.support.RetrySynchronizationManager.getContext();
        if (context != null) {
            int attempt = context.getRetryCount() + 1;
            log.info("Tentativa {} de processamento do seller {}", attempt, seller.getSellerId());
        }

        var regras = Map.of(
                TipoEtapaEnum.SELLER_REGISTRATION, StatusEtapaEnum.SUCCESS,
                TipoEtapaEnum.STORE_REGISTRATION, StatusEtapaEnum.IN_PROGRESS
        );
        sagaService.processarEtapasSaga(
                seller.getRequestId(),
                regras,
                TipoEtapaEnum.STORE_REGISTRATION,
                topicNameStore,
                saga -> saga.setSellerId(seller.getSellerId()));
    }

    @Recover
    public void handleCompensationEventRegisterSucess(Exception ex, Seller seller) {
        // solicito a compensação
        var regras = Map.of(
                TipoEtapaEnum.SELLER_REGISTRATION, StatusEtapaEnum.IN_COMPENSATION
        );
        sagaService.processarEtapasSaga(
                seller.getRequestId(),
                regras,
                TipoEtapaEnum.SELLER_REGISTRATION,
                null,
                saga -> {
                    saga.setSellerId(seller.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });
    }

    @Override
    public void processEventRegisterFailure(Seller seller) {
        Consumer<Saga> sagaUpdater = saga -> {
            saga.setSellerId(seller.getSellerId());
            saga.setStatus(StatusSagaEnum.FAILURE);
        };
        sagaService.processarEtapasSaga(
                seller.getRequestId(),
//                List.of(TipoEtapaEnum.SELLER_REGISTRATION),
                Map.of(TipoEtapaEnum.SELLER_REGISTRATION, StatusEtapaEnum.FAILURE),
                TipoEtapaEnum.STORE_REGISTRATION,
                null,
                sagaUpdater
        );
    }

    @Override
    public void processEventCompensateSucess(Seller seller) {
        Consumer<Saga> sagaUpdater = saga -> {
            saga.setSellerId(seller.getSellerId());
            saga.setStatus(StatusSagaEnum.COMPENSATED);
        };
        sagaService.processarEtapasSaga(
                seller.getRequestId(),
                Map.of(TipoEtapaEnum.SELLER_REGISTRATION, StatusEtapaEnum.COMPENSATED),
                TipoEtapaEnum.STORE_REGISTRATION,
                null,
                sagaUpdater
        );
    }

    @Override
    public void processEventCompensateFailure(Seller seller) {
        var regras = Map.of(
                TipoEtapaEnum.SELLER_REGISTRATION, StatusEtapaEnum.COMPENSATED_FAILURE
        );

        sagaService.processarEtapasSaga(
                seller.getRequestId(),
                regras,
                TipoEtapaEnum.SELLER_REGISTRATION,
                null,
                saga -> {
                    saga.setSellerId(seller.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });
    }
}