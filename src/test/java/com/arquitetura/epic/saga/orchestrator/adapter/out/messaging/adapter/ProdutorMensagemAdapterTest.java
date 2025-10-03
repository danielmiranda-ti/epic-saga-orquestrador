package com.arquitetura.epic.saga.orchestrator.adapter.out.messaging.adapter;

import com.arquitetura.epic.saga.orchestrator.adapter.out.messaging.dto.MensagemDTO;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.SagaEtapa;
import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProdutorMensagemAdapterTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private JsonUtil jsonUtil;

    @InjectMocks
    private ProdutorMensagemAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new ProdutorMensagemAdapter(kafkaTemplate, jsonUtil);
        MDC.clear();
    }

    @Test
    void enviaMensagem_deveEnviarMensagemQuandoEtapaPresente() {
        // Arrange
        String topico = "topico-teste";
        String correlationId = "corr-123";
        MDC.put("correlationId", correlationId);

        Saga saga = mock(Saga.class);
        when(saga.getId()).thenReturn(UUID.randomUUID());
        when(saga.getVendedorId()).thenReturn(UUID.randomUUID());

        SagaEtapa etapa = mock(SagaEtapa.class);
        when(etapa.getSaga()).thenReturn(saga);
        when(etapa.getPayloadUsado()).thenReturn("{\"foo\":\"bar\"}");

        Object payloadObj = new Object();
        when(jsonUtil.fromJson(any(), eq(Object.class))).thenReturn(payloadObj);
        when(jsonUtil.toJson(any(MensagemDTO.class))).thenReturn("{json}");

        // Act
        adapter.enviaMensagem(Optional.of(etapa), topico);

        // Assert
        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());
        ProducerRecord<String, String> record = captor.getValue();
        assertEquals(topico, record.topic());
        assertEquals("{json}", record.value());
        assertNotNull(record.headers().lastHeader("X-Correlation-Id"));
        assertArrayEquals(correlationId.getBytes(), record.headers().lastHeader("X-Correlation-Id").value());
    }

    @Test
    void enviaMensagem_deveLogarWarnQuandoEtapaNaoPresente() {
        // Arrange
        String topico = "topico-teste";

        // Act
        adapter.enviaMensagem(Optional.empty(), topico);

        // Assert
        verifyNoInteractions(kafkaTemplate);
        // Não é possível verificar log diretamente sem framework extra, mas o método não deve lançar exceção
    }

    @Test
    void getVendedorId_deveRetornarUnknownQuandoVendedorIdNulo() {
        // Arrange
        Saga saga = mock(Saga.class);
        when(saga.getVendedorId()).thenReturn(null);
        when(saga.getId()).thenReturn(UUID.randomUUID());
        SagaEtapa etapa = mock(SagaEtapa.class);
        when(etapa.getSaga()).thenReturn(saga);

        // Act (usando reflection para testar método privado)
        String vendedorId = org.springframework.test.util.ReflectionTestUtils.invokeMethod(adapter, "getVendedorId", etapa);

        // Assert
        assertEquals("UNKNOWN", vendedorId);
    }

    @Test
    void enviaMensagem_deveAdicionarHeaderVazioQuandoCorrelationIdNulo() {
        // Arrange
        String topico = "topico-teste";
        // NÃO setar MDC.put("correlationId", ...)
        Saga saga = mock(Saga.class);
        when(saga.getId()).thenReturn(UUID.randomUUID());
        when(saga.getVendedorId()).thenReturn(UUID.randomUUID());
        SagaEtapa etapa = mock(SagaEtapa.class);
        when(etapa.getSaga()).thenReturn(saga);
        when(etapa.getPayloadUsado()).thenReturn("{\"foo\":\"bar\"}");
        Object payloadObj = new Object();
        when(jsonUtil.fromJson(any(), eq(Object.class))).thenReturn(payloadObj);
        when(jsonUtil.toJson(any(MensagemDTO.class))).thenReturn("{json}");

        // Act
        adapter.enviaMensagem(Optional.of(etapa), topico);

        // Assert
        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());
        ProducerRecord<String, String> record = captor.getValue();
        assertEquals(topico, record.topic());
        assertEquals("{json}", record.value());
        assertNotNull(record.headers().lastHeader("X-Correlation-Id"));
        // O valor do header deve ser um array vazio
        assertArrayEquals(new byte[0], record.headers().lastHeader("X-Correlation-Id").value());
    }
}