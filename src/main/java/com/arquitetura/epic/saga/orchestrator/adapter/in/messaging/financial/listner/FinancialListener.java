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

    @KafkaListener(topics = "${kafka.topic.event.success.financial}", groupId = "orchestrator")
    public void onFinancialEventSuccess(ConsumerRecord<String, String> record) {
        financialPort.process();
    }

    @KafkaListener(topics = "${kafka.topic.event.failed.financial}", groupId = "orchestrator")
    public void onFinancialEventFailed(ConsumerRecord<String, String> record) {

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
