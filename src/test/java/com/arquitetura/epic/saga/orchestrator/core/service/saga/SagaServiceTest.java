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
import java.util.List;
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
        UUID vendedorId = UUID.randomUUID();
        Vendedor vendedor = mock(Vendedor.class);
        when(vendedor.getVendedorId()).thenReturn(vendedorId);
        when(vendedor.getDadosJuridicos()).thenReturn(mock(DadosJuridicos.class));
        when(vendedor.getDocumentos()).thenReturn(mock(List.class));
        when(vendedor.getDadosBancarios()).thenReturn(mock(DadosBancarios.class));
        when(vendedor.getDadosLoja()).thenReturn(mock(DadosLoja.class));

        Saga saga = Saga.builder()
                .vendedorId(vendedorId)
                .status(StatusSagaEnum.EM_ANDAMENTO)
                .etapasSaga(new ArrayList<>())
                .build();

        Saga sagaPersistida = Saga.builder()
                .vendedorId(vendedorId)
                .status(StatusSagaEnum.EM_ANDAMENTO)
                .etapasSaga(new ArrayList<>())
                .build();

        when(sagaRepositoryPort.salvar(any(Saga.class))).thenReturn(sagaPersistida);

        // Mock JSON serialization
        when(jsonUtil.toJson(any())).thenReturn("json");

        // Mock etapa saving
        when(etapaSagaRepositoryPort.salvar(any(SagaEtapa.class)))
                .thenAnswer(invocation -> {
                    return invocation.<SagaEtapa>getArgument(0);
                });

        // Act
        Saga result = sagaService.registrarSaga(vendedor);

        // Assert
        assertNotNull(result);
        assertEquals(vendedorId, result.getVendedorId());
        assertEquals(StatusSagaEnum.EM_ANDAMENTO, result.getStatus());
        assertEquals(4, result.getEtapasSaga().size());

        verify(sagaRepositoryPort).salvar(any(Saga.class));
        verify(etapaSagaRepositoryPort, times(4)).salvar(any(SagaEtapa.class));
        verify(jsonUtil, times(4)).toJson(any());
    }

    @Test
    void registrarSaga_shouldThrowException_whenVendedorIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            sagaService.registrarSaga(null);
        });
        assertEquals("Vendedor ou vendedorId não pode ser nulo", ex.getMessage());
    }

    @Test
    void registrarSaga_shouldThrowException_whenVendedorIdIsNull() {
        Vendedor vendedor = mock(Vendedor.class);
        when(vendedor.getVendedorId()).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            sagaService.registrarSaga(vendedor);
        });
        assertEquals("Vendedor ou vendedorId não pode ser nulo", ex.getMessage());
    }
}