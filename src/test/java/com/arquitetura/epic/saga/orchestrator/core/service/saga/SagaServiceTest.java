package com.arquitetura.epic.saga.orchestrator.core.service.saga;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.*;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.*;
import com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga.EtapaSagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.saga.SagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SagaServiceTest {

    @Mock
    private SagaRepositoryPort sagaRepositoryPort;
    @Mock
    private EtapaSagaRepositoryPort etapaSagaRepositoryPort;
    @Mock
    private JsonUtil jsonUtil;

    @InjectMocks
    private SagaService sagaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarSaga_shouldRegisterSagaAndEtapas_whenVendedorIsValid() {
        // Arrange
        UUID solicitacaoId = UUID.randomUUID();
        Solicitacao solicitacao = mock(Solicitacao.class);
        when(solicitacao.getSolicitacaoId()).thenReturn(solicitacaoId);
        when(solicitacao.getDadosJuridicos()).thenReturn(mock(DadosJuridicos.class));
        when(solicitacao.getDadosBancarios()).thenReturn(mock(DadosBancarios.class));

        Saga saga = Saga.builder()
                .solicitacaoId(solicitacaoId)
                .status(StatusSagaEnum.IN_PROGRESS)
                .etapasSaga(new ArrayList<>())
                .build();

        Saga sagaPersistida = Saga.builder()
                .solicitacaoId(solicitacaoId)
                .status(StatusSagaEnum.IN_PROGRESS)
                .etapasSaga(new ArrayList<>())
                .build();

        when(sagaRepositoryPort.salvar(any(Saga.class))).thenReturn(sagaPersistida);

        // Mock JSON serialization
        when(jsonUtil.toJson(any())).thenReturn("json");

        // Mock etapa saving
        when(etapaSagaRepositoryPort.salvar(any(EtapaSaga.class)))
                .thenAnswer(invocation -> {
                    return invocation.<EtapaSaga>getArgument(0);
                });

        // Act
        Saga result = sagaService.registrarSaga(solicitacao);

        // Assert
        assertNotNull(result);
        assertEquals(solicitacaoId, result.getSolicitacaoId());
        assertEquals(StatusSagaEnum.IN_PROGRESS, result.getStatus());
        assertEquals(3, result.getEtapasSaga().size());

        verify(sagaRepositoryPort).salvar(any(Saga.class));
        verify(etapaSagaRepositoryPort, times(3)).salvar(any(EtapaSaga.class));
        verify(jsonUtil, times(3)).toJson(any());
    }

    @Test
    void registrarSaga_shouldThrowException_whenVendedorIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            sagaService.registrarSaga(null);
        });
        assertEquals("Solicitação não pode ser nula.", ex.getMessage());
    }

    @Test
    void registrarSaga_shouldThrowException_whenVendedorIdIsNull() {
        Solicitacao vendedor = mock(Solicitacao.class);
        when(vendedor.getSolicitacaoId()).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            sagaService.registrarSaga(vendedor);
        });
        assertEquals("Solicitação não pode ser nula.", ex.getMessage());
    }
}