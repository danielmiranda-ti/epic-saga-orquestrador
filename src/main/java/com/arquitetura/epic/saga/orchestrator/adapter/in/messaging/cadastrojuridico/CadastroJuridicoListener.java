package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.cadastrojuridico;

import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.ListenerEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.cadastrojuridico.CadastroJuridicoPort;
import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.dto.share.SagaRequestDTO;
import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.mapper.MensagemMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastroJuridicoListener {

    private static final Logger log = LoggerFactory.getLogger(CadastroJuridicoListener.class);
    private CadastroJuridicoPort cadastroJuridicoPort;
    private MensagemMapper mensagemMapper;
    private JsonUtil jsonUtil;


    @KafkaListener(topics = "cadastro-juridico-sucesso", groupId = "orchestrator")
    public void onCadastroJuridicoAprovado(ConsumerRecord<String, String> record) {

        extractedCorrelation(record);
        log.info("=== Sucesso no processamento do cadastro juridico para o vendedor: {}", record.value());

        SagaRequestDTO sagaRequest = jsonUtil.fromJson(record.value(), SagaRequestDTO.class);

        var sagaTopico = mensagemMapper.toDomain(sagaRequest);

        cadastroJuridicoPort.processar(sagaTopico, ListenerEnum.SUCESSO);

        log.info("=== Mensagem postada no tópico 'verificacao-financeira-start' para o vendedor: {}\n", record.value());

    }

    @KafkaListener(topics = "cadastro-juridico-falha", groupId = "orchestrator")
    public void onCadastroJuridicoFalha(ConsumerRecord<String, String> record) {

        extractedCorrelation(record);
        log.info("=== Falha no processamento do cadastro juridico para o vendedor: {}\n", record.value());

        SagaRequestDTO sagaRequest = jsonUtil.fromJson(record.value(), SagaRequestDTO.class);

        var sagaTopico = mensagemMapper.toDomain(sagaRequest);

        cadastroJuridicoPort.processar(sagaTopico, ListenerEnum.FALHA);


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
