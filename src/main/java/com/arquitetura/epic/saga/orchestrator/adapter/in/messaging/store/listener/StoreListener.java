package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.store.listener;

import com.arquitetura.epic.saga.orchestrator.core.port.in.store.StorePort;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreListener {

    private static final Logger log = LoggerFactory.getLogger(StoreListener.class);

    private final StorePort storePort;

    @KafkaListener(topics = "${kafka.topic.event.success.store}", groupId = "orchestrator")
    public void onStoreEventSuccess(ConsumerRecord<String, String> record) {
        storePort.process();
    }

    @KafkaListener(topics = "${kafka.topic.event.failed.store}", groupId = "orchestrator")
    public void onStoreEventFailed(ConsumerRecord<String, String> record) {

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
