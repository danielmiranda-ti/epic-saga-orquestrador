package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.financial.listner;

import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.financial.dto.FinancialEvent;
import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.financial.mapper.FinancialEventMapper;
import com.arquitetura.epic.saga.orchestrator.core.port.in.financial.FinancialPort;
import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinancialListener {

    private static final Logger log = LoggerFactory.getLogger(FinancialListener.class);

    private final FinancialPort financialPort;

    private final JsonUtil jsonUtil;

    private final FinancialEventMapper mapper;

    @KafkaListener(topics = "${kafka.topic.event.register.success.financial}", groupId = "orchestrator")
    public void onFinancialEventRegisterSuccess(ConsumerRecord<String, String> record) {
        FinancialEvent financialEvent = x(record);
        log.info("=== Cadastro dos dados financeiros realizado com sucesso. sellerId={}, requestId={}.",
                 financialEvent.getSellerId(), financialEvent.getRequestId());

        var financial = mapper.toDomain(financialEvent);

        financialPort.processEventRegisterSucess(financial);
    }

    private FinancialEvent x(ConsumerRecord<String, String> record) {
        extractedCorrelation(record);

        return jsonUtil.fromJson(record.value(), FinancialEvent.class);
    }

    @KafkaListener(topics = "${kafka.topic.event.register.failed.financial}", groupId = "orchestrator")
    public void onFinancialEventRegisterFailed(ConsumerRecord<String, String> record) {
        FinancialEvent financialEvent = x(record);
        log.info("=== Falha ao cadastrar os dados financeiros. sellerId={}, requestId={}.",
                financialEvent.getSellerId(), financialEvent.getRequestId());

        var financial = mapper.toDomain(financialEvent);
        financialPort.processEventRegisterFailure(financial);
    }

    @KafkaListener(topics = "${kafka.topic.event.compensate.success.financial}", groupId = "orchestrator")
    public void onFinancialEventCompensateSuccess(ConsumerRecord<String, String> record) {
        FinancialEvent financialEvent = x(record);
        log.info("=== Sucesso na compensação do cadastro dos dados financeiros. sellerId={}, requestId={}.",
                financialEvent.getSellerId(), financialEvent.getRequestId());

        var financial = mapper.toDomain(financialEvent);
        financialPort.processEventCompensateSucess(financial);
    }

    @KafkaListener(topics = "${kafka.topic.event.compensate.failed.financial}", groupId = "orchestrator")
    public void onFinancialEventCompensateFailed(ConsumerRecord<String, String> record) {
        FinancialEvent financialEvent = x(record);
        log.info("=== Falha ao realizar a compensação do cadastro dos dados financeiros. sellerId={}, requestId={}.",
                financialEvent.getSellerId(), financialEvent.getRequestId());

        var financial = mapper.toDomain(financialEvent);
        financialPort.processEventCompensateFailure(financial);
    }

    private void extractedCorrelation(ConsumerRecord<String, String> record) {
        var correlationHeader = record.headers().lastHeader("X-Correlation-Id");
        if (correlationHeader != null) {
            String correlationId = new String(correlationHeader.value());
            if (!correlationId.isBlank()) {
                MDC.put("correlationId", correlationId);
            }
        }
    }
}
