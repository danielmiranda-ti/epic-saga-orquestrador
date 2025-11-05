package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.seller.listener;

import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.seller.dto.SellerEvent;
import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.seller.mapper.SellerEventMapper;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Seller;
import com.arquitetura.epic.saga.orchestrator.core.port.in.seller.SellerPort;
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
public class SellerListener {

    private static final Logger log = LoggerFactory.getLogger(SellerListener.class);

    private final SellerPort sellerPort;

    private final JsonUtil jsonUtil;
    private final SellerEventMapper mapper;

    @KafkaListener(topics = "${kafka.topic.event.register.success.seller}", groupId = "orchestrator")
    public void onSellerEventRegisterSuccess(ConsumerRecord<String, String> record) {
        extractedCorrelation(record);

        SellerEvent sellerEvent = jsonUtil.fromJson(record.value(), SellerEvent.class);
        log.info("=== Cadastro de vendedor realizado com sucesso. sellerId={}, requestId={}.",
                sellerEvent.getSellerId(), sellerEvent.getRequestId());

        Seller seller = mapper.toDomain(sellerEvent);

        sellerPort.processEventRegisterSucess(seller);

        log.info("=== Mensagem postada com sucesso. sellerId={}, requestId={}",
                sellerEvent.getSellerId(), sellerEvent.getRequestId());
    }

    @KafkaListener(topics = "${kafka.topic.event.register.failed.seller}", groupId = "orchestrator")
    public void onSellerEventRegisterFailed(ConsumerRecord<String, String> record) {
        extractedCorrelation(record);

        SellerEvent sellerEvent = jsonUtil.fromJson(record.value(), SellerEvent.class);
        log.warn("=== Falha no cadastro do vendedor. sellerId={}, requestId={}",
                sellerEvent.getSellerId(), sellerEvent.getRequestId());
        Seller seller = mapper.toDomain(sellerEvent);

        sellerPort.processEventRegisterFailure(seller);

        log.info("=== Processamento da falha, realizada com sucesso. sellerId={}, requestId={}, reasonFailure={}",
                sellerEvent.getSellerId(), sellerEvent.getRequestId(), sellerEvent.getReasonFailure());
    }

    @KafkaListener(topics = "${kafka.topic.event.compensate.success.seller}", groupId = "orchestrator")
    public void onSellerEventCompensateSuccess(ConsumerRecord<String, String> record) {
        extractedCorrelation(record);

        SellerEvent sellerEvent = jsonUtil.fromJson(record.value(), SellerEvent.class);
        log.warn("=== Solicitação na compensação do cadastro do vendedor realizada com sucesso. sellerId={}, requestId={}",
                sellerEvent.getSellerId(), sellerEvent.getRequestId());
        Seller seller = mapper.toDomain(sellerEvent);

        sellerPort.processEventCompensateSucess(seller);

        log.info("=== Processamento da compensação, realizada com sucesso. sellerId={}, requestId={}",
                sellerEvent.getSellerId(), sellerEvent.getRequestId());
    }

    @KafkaListener(topics = "${kafka.topic.event.compensate.failed.seller}", groupId = "orchestrator")
    public void onSellerEventCompensateFailed(ConsumerRecord<String, String> record) {
        extractedCorrelation(record);

        SellerEvent sellerEvent = jsonUtil.fromJson(record.value(), SellerEvent.class);
        log.warn("=== Solicitação na compensação do cadastro do vendedor falhou. sellerId={}, requestId={}",
                sellerEvent.getSellerId(), sellerEvent.getRequestId());
        Seller seller = mapper.toDomain(sellerEvent);

        sellerPort.processEventCompensateFailure(seller);

        log.info("=== Processamento da falha na compensação, realizada com sucesso. sellerId={}, requestId={}, reasonFailure={}",
                sellerEvent.getSellerId(), sellerEvent.getRequestId(), sellerEvent.getReasonFailure());
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