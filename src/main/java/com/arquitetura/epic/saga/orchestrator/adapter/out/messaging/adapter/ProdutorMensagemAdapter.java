package com.arquitetura.epic.saga.orchestrator.adapter.out.messaging.adapter;

import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import com.arquitetura.epic.saga.orchestrator.adapter.out.messaging.dto.MensagemDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProdutorMensagemAdapter implements ProdutorMensagemPort {

    private static final Logger log = LoggerFactory.getLogger(ProdutorMensagemAdapter.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonUtil jsonUtil;

    public void enviaMensagem(Optional<EtapaSaga> etapaOpt, String topico) {
        etapaOpt.ifPresentOrElse(
                etapa -> {
                    String correlationId = MDC.get("correlationId");
                    String vendedorId = getVendedorId(etapa);
                    String mensagemJson = criarMensagemJson(etapa, vendedorId);

                    ProducerRecord<String, String> record = new ProducerRecord<>(topico, mensagemJson);
                    record.headers().add("X-Correlation-Id",
                            correlationId != null ? correlationId.getBytes() : new byte[0]);

                    kafkaTemplate.send(record);
                    log.info("=== Mensagem enviada para o vendedor: {} no tópico: {}", vendedorId, topico);
                },
                () -> log.warn("=== Etapa não encontrada. Mensagem não enviada para o tópico: {}", topico)
        );
    }

    private String getVendedorId(EtapaSaga etapa) {
        return etapa.getSaga().getSellerId() != null
                ? etapa.getSaga().getSellerId()
                : "UNKNOWN";
    }

    private String criarMensagemJson(EtapaSaga etapa, String vendedorId) {
        MensagemDTO mensagem = MensagemDTO.builder()
                .solicitacaoId(etapa.getSaga().getSolicitacaoId().toString())
                .etapa(etapa.getNomeEtapa())
                .vendedorId(vendedorId)
                .payload(jsonUtil.fromJson(etapa.getPayload(), Object.class))
                .build();
        return jsonUtil.toJson(mensagem);
    }
}
