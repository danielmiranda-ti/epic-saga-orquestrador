package com.arquitetura.epic.saga.orchestrator.core.usecase.onboarding;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Vendedor;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.SagaEtapa;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnboardingVendedorUseCaseTest {

    @Mock
    private ProdutorMensagemPort produtorMensagemPort;

    @Mock
    private SagaService sagaService;

    @InjectMocks
    private OnboardingVendedorUseCase onboardingVendedorUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startSaga_shouldSendMessageForCadastroJuridico() {
        // Arrange
        Vendedor vendedor = mock(Vendedor.class);

        SagaEtapa etapaJuridica = mock(SagaEtapa.class);
        when(etapaJuridica.getNomeEtapa()).thenReturn("cadastro-juridico");

        SagaEtapa etapaOutra = mock(SagaEtapa.class);
        when(etapaOutra.getNomeEtapa()).thenReturn("outra-etapa");

        List<SagaEtapa> etapas = Arrays.asList(etapaOutra, etapaJuridica);

        Saga saga = mock(Saga.class);
        when(saga.getEtapasSaga()).thenReturn(etapas);

        when(sagaService.registrarSaga(vendedor)).thenReturn(saga);

        // Act
        onboardingVendedorUseCase.startSaga(vendedor);

        // Assert
        ArgumentCaptor<Optional<SagaEtapa>> etapaCaptor = ArgumentCaptor.forClass(Optional.class);
        verify(produtorMensagemPort).enviaMensagem(etapaCaptor.capture(), eq("cadastro-juridico-start"));
        assertTrue(etapaCaptor.getValue().isPresent());
        assertEquals(etapaJuridica, etapaCaptor.getValue().get());
    }

    @Test
    void startSaga_shouldSendEmptyOptionalIfNoCadastroJuridico() {
        // Arrange
        Vendedor vendedor = mock(Vendedor.class);

        SagaEtapa etapaOutra = mock(SagaEtapa.class);
        when(etapaOutra.getNomeEtapa()).thenReturn("outra-etapa");

        List<SagaEtapa> etapas = Collections.singletonList(etapaOutra);

        Saga saga = mock(Saga.class);
        when(saga.getEtapasSaga()).thenReturn(etapas);

        when(sagaService.registrarSaga(vendedor)).thenReturn(saga);

        // Act
        onboardingVendedorUseCase.startSaga(vendedor);

        // Assert
        ArgumentCaptor<Optional<SagaEtapa>> etapaCaptor = ArgumentCaptor.forClass(Optional.class);
        verify(produtorMensagemPort).enviaMensagem(etapaCaptor.capture(), eq("cadastro-juridico-start"));
        assertFalse(etapaCaptor.getValue().isPresent());
    }

}