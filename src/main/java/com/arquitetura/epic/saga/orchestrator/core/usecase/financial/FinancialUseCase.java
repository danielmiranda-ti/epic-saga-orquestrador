package com.arquitetura.epic.saga.orchestrator.core.usecase.financial;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusSagaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.financial.FinancialPort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinancialUseCase implements FinancialPort {

    private static final Logger log = LoggerFactory.getLogger(FinancialUseCase.class);

    private final SagaService sagaService;

    @Value("${kafka.topic.event.failed.financial}")
    private String topicEventFailure;

    @Override
    public void process() {
        try {
            sagaService.processarEtapasSaga(
                    "",
                    "",
                    List.of(TipoEtapaEnum.BANK_REGISTRATION),
                    Map.of(TipoEtapaEnum.BANK_REGISTRATION, StatusEtapaEnum.SUCESSO),
                    null,
                    null,
                    saga -> {
                        saga.setStatus(StatusSagaEnum.SUCESSO);
                    }
            );
        } catch (Exception ex) {
            log.error("Erro ao processar seller: {}", ex.getMessage(), ex);
            var tipoEtapa = TipoEtapaEnum.BANK_REGISTRATION;

            sagaService.processarEtapasSaga(
                    "",
                    "",
                    List.of(tipoEtapa),
                    Map.of(tipoEtapa, StatusEtapaEnum.FALHA),
                    TipoEtapaEnum.BANK_REGISTRATION,
                    topicEventFailure,
                    saga -> saga.setStatus(StatusSagaEnum.FALHA)
            );
        }
    }
}