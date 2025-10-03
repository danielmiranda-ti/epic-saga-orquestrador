package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.verificacaofinanceira;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.*;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;


class VerificacaoFinanceiraListenerTest {

    private VerificacaoFinanceiraListener listener;

    @BeforeEach
    void setUp() {
        listener = new VerificacaoFinanceiraListener();
        MDC.clear();
    }

    @Test
    void onVerificacaoFinanceiraAprovada_deveColocarCorrelationIdNoMDC_QuandoHeaderPresente() {
        // Arrange
        String correlationId = "corr-xyz";
        String value = "vendedor-123";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("verificacao-financeira-sucesso", 0, 0L, null, value);
        Headers headers = new RecordHeaders();
        headers.add("X-Correlation-Id", correlationId.getBytes());
        injectField(record, "headers", headers);

        // Act
        listener.onVerificacaoFinanceiraAprovada(record);

        // Assert
        assertEquals(correlationId, MDC.get("correlationId"));
    }

    @Test
    void onVerificacaoFinanceiraAprovada_naoDeveColocarCorrelationIdNoMDC_QuandoHeaderAusente() {
        // Arrange
        String value = "vendedor-456";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("verificacao-financeira-sucesso", 0, 0L, null, value);
        Headers headers = new RecordHeaders(); // sem header
        injectField(record, "headers", headers);

        // Act
        listener.onVerificacaoFinanceiraAprovada(record);

        // Assert
        assertNull(MDC.get("correlationId"));
    }

    // Utilitário para injetar headers no ConsumerRecord (reflection)
    private void injectField(Object target, String field, Object value) {
        try {
            var f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}