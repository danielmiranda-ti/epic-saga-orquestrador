package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.adapter;

import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity.SagaEtapaEntity;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.mapper.SagaEtapaMapper;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.repository.SagaEtapaRepository;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.SagaEtapa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EtapaSagaRepositoryAdapterTest {

    @Mock
    private SagaEtapaRepository sagaEtapaRepository;
    @Mock
    private SagaEtapaMapper sagaEtapaMapper;

    @InjectMocks
    private EtapaSagaRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void salvar_devePersistirSagaEtapaERetornarDomain() {
        SagaEtapa sagaEtapa = mock(SagaEtapa.class);
        SagaEtapaEntity entity = mock(SagaEtapaEntity.class);
        SagaEtapaEntity persistedEntity = mock(SagaEtapaEntity.class);
        SagaEtapa domain = mock(SagaEtapa.class);

        when(sagaEtapaMapper.toEntity(sagaEtapa)).thenReturn(entity);
        when(sagaEtapaRepository.save(entity)).thenReturn(persistedEntity);
        when(sagaEtapaMapper.toDomain(persistedEntity)).thenReturn(domain);

        SagaEtapa result = adapter.salvar(sagaEtapa);

        assertNotNull(result);
        assertEquals(domain, result);
        verify(sagaEtapaMapper).toEntity(sagaEtapa);
        verify(sagaEtapaRepository).save(entity);
        verify(sagaEtapaMapper).toDomain(persistedEntity);
    }

    @Test
    void buscarPorId_deveRetornarSagaEtapaQuandoEncontrado() {
        UUID etapaId = UUID.randomUUID();
        SagaEtapaEntity entity = mock(SagaEtapaEntity.class);
        SagaEtapa domain = mock(SagaEtapa.class);

        when(sagaEtapaRepository.findById(etapaId)).thenReturn(Optional.of(entity));
        when(sagaEtapaMapper.toDomain(entity)).thenReturn(domain);

        Optional<SagaEtapa> result = adapter.buscarPorId(etapaId.toString());

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(sagaEtapaRepository).findById(etapaId);
        verify(sagaEtapaMapper).toDomain(entity);
    }

    @Test
    void buscarPorId_deveRetornarVazioQuandoNaoEncontrado() {
        UUID etapaId = UUID.randomUUID();

        when(sagaEtapaRepository.findById(etapaId)).thenReturn(Optional.empty());

        Optional<SagaEtapa> result = adapter.buscarPorId(etapaId.toString());

        assertFalse(result.isPresent());
        verify(sagaEtapaRepository).findById(etapaId);
        verifyNoInteractions(sagaEtapaMapper);
    }
}