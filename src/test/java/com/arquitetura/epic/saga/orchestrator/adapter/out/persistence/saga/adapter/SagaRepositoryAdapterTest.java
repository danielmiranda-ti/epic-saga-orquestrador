package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.adapter;

import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.entity.SagaEntity;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.mapper.SagaMapper;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.repository.SagaRepository;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SagaRepositoryAdapterTest {

    @Mock
    private SagaRepository sagaRepository;
    @Mock
    private SagaMapper sagaMapper;

    @InjectMocks
    private SagaRepositoryAdapter sagaRepositoryAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void salvar_devePersistirSagaERetornarDomain() {
        Saga saga = mock(Saga.class);
        SagaEntity sagaEntity = mock(SagaEntity.class);
        SagaEntity sagaEntityPersisted = mock(SagaEntity.class);
        Saga sagaDomain = mock(Saga.class);

        when(sagaMapper.toEntity(saga)).thenReturn(sagaEntity);
        when(sagaRepository.save(sagaEntity)).thenReturn(sagaEntityPersisted);
        when(sagaMapper.toDomain(sagaEntityPersisted)).thenReturn(sagaDomain);

        Saga result = sagaRepositoryAdapter.salvar(saga);

        assertNotNull(result);
        assertEquals(sagaDomain, result);
        verify(sagaMapper).toEntity(saga);
        verify(sagaRepository).save(sagaEntity);
        verify(sagaMapper).toDomain(sagaEntityPersisted);
    }

    @Test
    void buscarPorId_deveRetornarSagaQuandoEncontrado() {
        UUID sagaId = UUID.randomUUID();
        SagaEntity sagaEntity = mock(SagaEntity.class);
        Saga sagaDomain = mock(Saga.class);

        when(sagaRepository.findById(sagaId)).thenReturn(Optional.of(sagaEntity));
        when(sagaMapper.toDomain(sagaEntity)).thenReturn(sagaDomain);

        Optional<Saga> result = sagaRepositoryAdapter.buscarPorId(sagaId.toString());

        assertTrue(result.isPresent());
        assertEquals(sagaDomain, result.get());
        verify(sagaRepository).findById(sagaId);
        verify(sagaMapper).toDomain(sagaEntity);
    }

    @Test
    void buscarPorId_deveRetornarVazioQuandoNaoEncontrado() {
        UUID sagaId = UUID.randomUUID();

        when(sagaRepository.findById(sagaId)).thenReturn(Optional.empty());

        Optional<Saga> result = sagaRepositoryAdapter.buscarPorId(sagaId.toString());

        assertFalse(result.isPresent());
        verify(sagaRepository).findById(sagaId);
        verifyNoInteractions(sagaMapper);
    }
}