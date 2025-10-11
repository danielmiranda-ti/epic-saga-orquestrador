package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.seller.listener;

import com.arquitetura.epic.saga.orchestrator.core.port.in.seller.SellerPort;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SellerListener {

    private static final Logger log = LoggerFactory.getLogger(SellerListener.class);

    private final SellerPort sellerPort;

    @KafkaListener(topics = "${kafka.topic.event.success.seller}", groupId = "orchestrator")
    public void onSellerEventSuccess(ConsumerRecord<String, String> record) {
        sellerPort.process();
    }

    @KafkaListener(topics = "${kafka.topic.event.failed.seller}", groupId = "orchestrator")
    public void onSellerEventFailed(ConsumerRecord<String, String> record) {

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