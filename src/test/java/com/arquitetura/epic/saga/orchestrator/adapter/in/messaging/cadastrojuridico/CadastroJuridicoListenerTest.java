package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.cadastrojuridico;

import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.dto.share.SagaRequestDTO;
import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.mapper.MensagemMapper;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.ListenerEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.SagaTopico;
import com.arquitetura.epic.saga.orchestrator.core.port.in.cadastrojuridico.CadastroJuridicoPort;
import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.MDC;

//import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class CadastroJuridicoListenerTest {

    @Mock
    private CadastroJuridicoPort cadastroJuridicoPort;
    @Mock
    private MensagemMapper mensagemMapper;
    @Mock
    private JsonUtil jsonUtil;

    @InjectMocks
    private CadastroJuridicoListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new CadastroJuridicoListener();
        // Injeta mocks nos campos privados (pois não são final)
        injectField(listener, "cadastroJuridicoPort", cadastroJuridicoPort);
        injectField(listener, "mensagemMapper", mensagemMapper);
        injectField(listener, "jsonUtil", jsonUtil);
        MDC.clear();
    }

    @Test
    void onCadastroJuridicoAprovado_deveProcessarComSucesso() {
        // Arrange
        String value = "{\"sagaId\":\"1\",\"etapaId\":\"2\",\"vendedorId\":\"3\"}";
        SagaRequestDTO sagaRequestDTO = new SagaRequestDTO();
        SagaTopico sagaTopico = SagaTopico.builder().build();

        ConsumerRecord<String, String> record = new ConsumerRecord<>("cadastro-juridico-sucesso", 0, 0L, null, value);
        Headers headers = new RecordHeaders();
        headers.add("X-Correlation-Id", "corr-123".getBytes());
        when(jsonUtil.fromJson(value, SagaRequestDTO.class)).thenReturn(sagaRequestDTO);
        when(mensagemMapper.toDomain(sagaRequestDTO)).thenReturn(sagaTopico);

        // Simula headers
        injectField(record, "headers", headers);

        // Act
        listener.onCadastroJuridicoAprovado(record);

        // Assert
        verify(jsonUtil).fromJson(value, SagaRequestDTO.class);
        verify(mensagemMapper).toDomain(sagaRequestDTO);
        verify(cadastroJuridicoPort).processar(sagaTopico, ListenerEnum.SUCESSO);
    }

    @Test
    void onCadastroJuridicoFalha_deveProcessarComFalha() {
        // Arrange
        String value = "{\"sagaId\":\"1\",\"etapaId\":\"2\",\"vendedorId\":\"3\"}";
        SagaRequestDTO sagaRequestDTO = new SagaRequestDTO();
        SagaTopico sagaTopico = SagaTopico.builder().build();

        ConsumerRecord<String, String> record = new ConsumerRecord<>("cadastro-juridico-falha", 0, 0L, null, value);
        Headers headers = new RecordHeaders();
        headers.add("X-Correlation-Id", "corr-456".getBytes());
        when(jsonUtil.fromJson(value, SagaRequestDTO.class)).thenReturn(sagaRequestDTO);
        when(mensagemMapper.toDomain(sagaRequestDTO)).thenReturn(sagaTopico);

        injectField(record, "headers", headers);

        // Act
        listener.onCadastroJuridicoFalha(record);

        // Assert
        verify(jsonUtil).fromJson(value, SagaRequestDTO.class);
        verify(mensagemMapper).toDomain(sagaRequestDTO);
        verify(cadastroJuridicoPort).processar(sagaTopico, ListenerEnum.FALHA);
    }

    @Test
    void onCadastroJuridicoAprovado_naoDeveColocarCorrelationIdNoMDC_QuandoHeaderAusente() {
        // Arrange
        String value = "{\"sagaId\":\"1\",\"etapaId\":\"2\",\"vendedorId\":\"3\"}";
        SagaRequestDTO sagaRequestDTO = new SagaRequestDTO();
        SagaTopico sagaTopico = SagaTopico.builder().build();
        ConsumerRecord<String, String> record = new ConsumerRecord<>("cadastro-juridico-sucesso", 0, 0L, null, value);
        Headers headers = new RecordHeaders(); // Não adiciona o header
        when(jsonUtil.fromJson(value, SagaRequestDTO.class)).thenReturn(sagaRequestDTO);
        when(mensagemMapper.toDomain(sagaRequestDTO)).thenReturn(sagaTopico);
        injectField(record, "headers", headers);

        // Act
        listener.onCadastroJuridicoAprovado(record);

        // Assert
        verify(jsonUtil).fromJson(value, SagaRequestDTO.class);
        verify(mensagemMapper).toDomain(sagaRequestDTO);
        verify(cadastroJuridicoPort).processar(sagaTopico, ListenerEnum.SUCESSO);

//        isNull(org.slf4j.MDC.get("correlationId"));
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void onCadastroJuridicoFalha_naoDeveColocarCorrelationIdNoMDC_QuandoHeaderAusente() {
        // Arrange
        String value = "{\"sagaId\":\"1\",\"etapaId\":\"2\",\"vendedorId\":\"3\"}";
        SagaRequestDTO sagaRequestDTO = new SagaRequestDTO();
        SagaTopico sagaTopico = SagaTopico.builder().build();
        ConsumerRecord<String, String> record = new ConsumerRecord<>("cadastro-juridico-falha", 0, 0L, null, value);
        Headers headers = new RecordHeaders(); // Não adiciona o header
        when(jsonUtil.fromJson(value, SagaRequestDTO.class)).thenReturn(sagaRequestDTO);
        when(mensagemMapper.toDomain(sagaRequestDTO)).thenReturn(sagaTopico);
        injectField(record, "headers", headers);

        // Act
        listener.onCadastroJuridicoFalha(record);

        // Assert
        verify(jsonUtil).fromJson(value, SagaRequestDTO.class);
        verify(mensagemMapper).toDomain(sagaRequestDTO);
        verify(cadastroJuridicoPort).processar(sagaTopico, ListenerEnum.FALHA);
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void onCadastroJuridicoAprovado_naoDeveColocarCorrelationIdNoMDC_QuandoCorrelationEmBranco() {
        // Arrange
        String value = "{\"sagaId\":\"1\",\"etapaId\":\"2\",\"vendedorId\":\"3\"}";
        SagaRequestDTO sagaRequestDTO = new SagaRequestDTO();
        SagaTopico sagaTopico = SagaTopico.builder().build();
        ConsumerRecord<String, String> record = new ConsumerRecord<>("cadastro-juridico-sucesso", 0, 0L, null, value);
        Headers headers = new RecordHeaders(); // Não adiciona o header "X-Correlation-Id"
        // injeta headers vazios
        headers.add("X-Correlation-Id", "   ".getBytes());
        injectField(record, "headers", headers);

        when(jsonUtil.fromJson(value, SagaRequestDTO.class)).thenReturn(sagaRequestDTO);
        when(mensagemMapper.toDomain(sagaRequestDTO)).thenReturn(sagaTopico);

        // Act
        listener.onCadastroJuridicoAprovado(record);

        // Assert
        verify(jsonUtil).fromJson(value, SagaRequestDTO.class);
        verify(mensagemMapper).toDomain(sagaRequestDTO);
        verify(cadastroJuridicoPort).processar(sagaTopico, ListenerEnum.SUCESSO);
        assertNull(MDC.get("correlationId")); // Não deve setar o correlationId
    }

    // Utilitário para injetar mocks em campos privados não-final
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