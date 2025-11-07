package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.store.listener;

import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.store.dto.StoreEvent;
import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.store.mapper.StoreEventMapper;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Store;
import com.arquitetura.epic.saga.orchestrator.core.port.in.store.StorePort;
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
public class StoreListener {

    private static final Logger log = LoggerFactory.getLogger(StoreListener.class);

    private final StorePort storePort;

    private final JsonUtil jsonUtil;
    private final StoreEventMapper mapper;

    @KafkaListener(topics = "${kafka.topic.event.register.success.store}", groupId = "orchestrator")
    public void onStoreEventRegisterSuccess(ConsumerRecord<String, String> record) {
        extractedCorrelation(record);

        StoreEvent storeEvent = jsonUtil.fromJson(record.value(), StoreEvent.class);
        log.info("=== Cadastro da loja '{}' realizado com sucesso. sellerId={}, requestId={}.",
                storeEvent.getStoreId(), storeEvent.getSellerId(), storeEvent.getRequestId());

        Store store = mapper.toDomain(storeEvent);
        storePort.processEventRegisterSucess(store);

        log.info("=== Mensagem 'StoreEventRegisterSuccess' processada com sucesso. sellerId={}, requestId={}",
                storeEvent.getSellerId(), storeEvent.getRequestId());
    }

    @KafkaListener(topics = "${kafka.topic.event.register.failed.store}", groupId = "orchestrator")
    public void onStoreEventRegisterFailed(ConsumerRecord<String, String> record) {

        extractedCorrelation(record);

        StoreEvent storeEvent = jsonUtil.fromJson(record.value(), StoreEvent.class);
        log.info("=== Problema ao realizar o cadastro da loja para o seller={}, requestId={}.",
                storeEvent.getSellerId(), storeEvent.getRequestId());

        Store store = mapper.toDomain(storeEvent);
        storePort.processEventRegisterFailure(store);
        log.info("=== Mensagem 'StoreEventRegisterFailed' processada com sucesso. sellerId={}, requestId={}",
                storeEvent.getSellerId(), storeEvent.getRequestId());
    }

    @KafkaListener(topics = "${kafka.topic.event.compensate.success.store}", groupId = "orchestrator")
    public void onStoreEventCompensateSuccess(ConsumerRecord<String, String> record) {
        extractedCorrelation(record);

        StoreEvent storeEvent = jsonUtil.fromJson(record.value(), StoreEvent.class);
        log.info("=== Compensação para o cadastro da loja do seller={} realizada com sucesso. requestId={}.",
                 storeEvent.getSellerId(), storeEvent.getRequestId());

        Store store = mapper.toDomain(storeEvent);
        storePort.processEventCompensateSucess(store);

        log.info("=== Mensagem 'StoreEventCompensateSuccess' processada com sucesso. sellerId={}, requestId={}",
                storeEvent.getSellerId(), storeEvent.getRequestId());
    }

    @KafkaListener(topics = "${kafka.topic.event.compensate.failed.store}", groupId = "orchestrator")
    public void onStoreEventCompensateFailed(ConsumerRecord<String, String> record) {
        extractedCorrelation(record);

        StoreEvent storeEvent = jsonUtil.fromJson(record.value(), StoreEvent.class);
        log.info("=== Falha na compensação do cadastro da loja. sellerId={}, requestId={}.",
                 storeEvent.getSellerId(), storeEvent.getRequestId());

        Store store = mapper.toDomain(storeEvent);
        storePort.processEventCompensateFailure(store);

        log.info("=== Mensagem 'StoreEventCompensateFailed' processada com sucesso. sellerId={}, requestId={}",
                storeEvent.getSellerId(), storeEvent.getRequestId());
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
