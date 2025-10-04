package com.arquitetura.epic.saga.orchestrator.core.usecase.onboarding;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Solicitacao;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

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
        Solicitacao solicitacao = Solicitacao.builder().solicitacaoId(UUID.randomUUID()).build();

        EtapaSaga etapaJuridica = EtapaSaga.builder().nomeEtapa(TipoEtapaEnum.CADASTRAR_DADOS_PESSOAIS.name()).build();

        EtapaSaga etapaOutra = EtapaSaga.builder().nomeEtapa("outra-etapa").build();

        List<EtapaSaga> etapas = Arrays.asList(etapaOutra, etapaJuridica);

        Saga saga = Saga.builder().etapasSaga(etapas).build();

        when(sagaService.registrarSaga(solicitacao)).thenReturn(saga);

        // Act
        var status = onboardingVendedorUseCase.startSaga(solicitacao);

        // Assert
        ArgumentCaptor<Optional<EtapaSaga>> etapaCaptor = ArgumentCaptor.forClass(Optional.class);
        verify(produtorMensagemPort).enviaMensagem(etapaCaptor.capture(), eq("cadastro-vendedor-start"));
        assertTrue(etapaCaptor.getValue().isPresent());
        assertEquals(etapaJuridica, etapaCaptor.getValue().get());
    }

    @Test
    void startSaga_shouldSendEmptyOptionalIfNoCadastroJuridico() {
        // Arrange
        Solicitacao solicitacao = Solicitacao.builder().solicitacaoId(UUID.randomUUID()).build();

        EtapaSaga etapaOutra = EtapaSaga.builder().nomeEtapa("outra-etapa").build();

        List<EtapaSaga> etapas = Collections.singletonList(etapaOutra);

        Saga saga = Saga.builder().etapasSaga(etapas).build();

        when(sagaService.buscarSagaPorSolicitacaoId("id-qualquer")).thenReturn(Optional.empty());

        when(sagaService.registrarSaga(solicitacao)).thenReturn(saga);

        // Act
        var status = onboardingVendedorUseCase.startSaga(solicitacao);

        // Assert
        verifyNoInteractions(produtorMensagemPort);

    }

    @Test
    void startSaga_shouldNotSendMessageIfNoCadastroDadosPessoais() {
        // Arrange
        Solicitacao solicitacao = Solicitacao.builder().solicitacaoId(UUID.randomUUID()).build();

        EtapaSaga etapaOutra = mock(EtapaSaga.class);
        when(etapaOutra.getNomeEtapa()).thenReturn("outra-etapa");

        List<EtapaSaga> etapas = Collections.singletonList(etapaOutra);

        Saga saga = mock(Saga.class);
        when(saga.getEtapasSaga()).thenReturn(etapas);

        when(sagaService.registrarSaga(solicitacao)).thenReturn(saga);

        // Act
        onboardingVendedorUseCase.startSaga(solicitacao);

        // Assert
        verify(produtorMensagemPort, never()).enviaMensagem(any(), any());
    }

}