package com.arquitetura.epic.saga.orchestrator.core.usecase.financial;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Financial;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Seller;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusSagaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.financial.FinancialPort;
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

@Service
@RequiredArgsConstructor
public class FinancialUseCase implements FinancialPort {

    private static final Logger log = LoggerFactory.getLogger(FinancialUseCase.class);

    private final SagaService sagaService;

    @Value("${kafka.topic.command.compensate.store}")
    private String topicCommandCompensateStore;

    @Value("${kafka.topic.command.compensate.financial}")
    private String topicCommandCompensateFinancial;


    @Override
    @Retryable(
            recover = "handleCompensationEventRegisterSucess",
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000))
    public void processEventRegisterSucess(Financial financial) {
        sagaService.processarEtapasSaga(
                financial.getRequestId(),
                Map.of(TipoEtapaEnum.BANK_REGISTRATION, StatusEtapaEnum.SUCCESS),
                null,
                null,
                saga -> {
                    saga.setStatus(StatusSagaEnum.SUCCESS);
                }
        );
    }

    @Recover
    public void handleCompensationEventRegisterSucess(Exception ex, Financial financial) {
        // solicito a compensação
        var regras = Map.of(
                TipoEtapaEnum.BANK_REGISTRATION, StatusEtapaEnum.IN_COMPENSATION
        );
        sagaService.processarEtapasSaga(
                financial.getRequestId(),
                regras,
                TipoEtapaEnum.STORE_REGISTRATION,
                topicCommandCompensateFinancial,
                saga -> {
                    saga.setSellerId(financial.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });
    }

    @Override
    public void processEventRegisterFailure(Financial financial) {
        var regras = Map.of(
                TipoEtapaEnum.BANK_REGISTRATION, StatusEtapaEnum.FAILURE
        );
        sagaService.processarEtapasSaga(
                financial.getRequestId(),
                regras,
                TipoEtapaEnum.STORE_REGISTRATION,
                topicCommandCompensateStore,
                saga -> {
                    saga.setSellerId(financial.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });
    }

    @Override
    public void processEventCompensateSucess(Financial financial) {
        // solicitar a compensação da store
        var regras = Map.of(
                TipoEtapaEnum.BANK_REGISTRATION, StatusEtapaEnum.COMPENSATED
        );

        sagaService.processarEtapasSaga(
                financial.getRequestId(),
                regras,
                TipoEtapaEnum.STORE_REGISTRATION,
                topicCommandCompensateStore,
                saga -> {
                    saga.setSellerId(financial.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });
    }

    @Override
    public void processEventCompensateFailure(Financial financial) {
        var regras = Map.of(
                TipoEtapaEnum.BANK_REGISTRATION, StatusEtapaEnum.COMPENSATED_FAILURE
        );

        sagaService.processarEtapasSaga(
                financial.getRequestId(),
                regras,
                TipoEtapaEnum.STORE_REGISTRATION,
                topicCommandCompensateStore,
                saga -> {
                    saga.setSellerId(financial.getSellerId());
                    saga.setStatus(StatusSagaEnum.IN_COMPENSATION);
                });
    }
}