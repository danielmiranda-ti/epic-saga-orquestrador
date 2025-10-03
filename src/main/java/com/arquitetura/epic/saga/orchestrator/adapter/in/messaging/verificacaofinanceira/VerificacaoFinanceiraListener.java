package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.verificacaofinanceira;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificacaoFinanceiraListener {

    private static final Logger log = LoggerFactory.getLogger(VerificacaoFinanceiraListener.class);


    @KafkaListener(topics = "verificacao-financeira-sucesso", groupId = "orchestrator")
    public void onVerificacaoFinanceiraAprovada(ConsumerRecord<String, String> record) {

        String correlationId = null;
        if (record.headers().lastHeader("X-Correlation-Id") != null) {
            correlationId = new String(record.headers().lastHeader("X-Correlation-Id").value());
        }
        if (correlationId != null) {
            MDC.put("correlationId", correlationId);
        }

        log.info("=== Sucesso no processamento da validação financeira para o vendedor: {}", record.value());
    }


//    @KafkaListener(topics = "verificacao-financeira-falha", groupId = "orchestrator")
//    public void onVerificacaoFinanceiraFalha(String vendedorId) {
//        statusMap.put(vendedorId, "Verificação Financeira Aprovada");
//        kafkaTemplate.send("cadastro-juridico-desfazimento", vendedorId);
//    }
}
