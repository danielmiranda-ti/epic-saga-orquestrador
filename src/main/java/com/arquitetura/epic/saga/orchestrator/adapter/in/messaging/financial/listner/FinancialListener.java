package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.financial.listner;

import com.arquitetura.epic.saga.orchestrator.core.port.in.financial.FinancialPort;
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

    @KafkaListener(topics = "${kafka.topic.event.register.success.financial}", groupId = "orchestrator")
    public void onFinancialEventRegisterSuccess(ConsumerRecord<String, String> record) {
        financialPort.processEventRegisterSucess(null);
    }

    @KafkaListener(topics = "${kafka.topic.event.register.failed.financial}", groupId = "orchestrator")
    public void onFinancialEventRegisterFailed(ConsumerRecord<String, String> record) {
        financialPort.processEventRegisterFailure(null);
    }

    @KafkaListener(topics = "${kafka.topic.event.compensate.success.financial}", groupId = "orchestrator")
    public void onFinancialEventCompensateSuccess(ConsumerRecord<String, String> record) {
        financialPort.processEventCompensateSucess(null);
    }

    @KafkaListener(topics = "${kafka.topic.event.compensate.failed.financial}", groupId = "orchestrator")
    public void onFinancialEventCompensateFailed(ConsumerRecord<String, String> record) {
        financialPort.processEventCompensateFailure(null);
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
