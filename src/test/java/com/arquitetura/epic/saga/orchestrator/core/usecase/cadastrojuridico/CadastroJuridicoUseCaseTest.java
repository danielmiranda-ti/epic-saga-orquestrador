package com.arquitetura.epic.saga.orchestrator.core.usecase.cadastrojuridico;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.ListenerEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.SagaTopico;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.StatusSagaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga.EtapaSagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.saga.SagaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class CadastroJuridicoUseCaseTest {

    @Mock
    private ProdutorMensagemPort produtorMensagemPort;
    @Mock
    private EtapaSagaRepositoryPort etapaSagaRepositoryPort;
    @Mock
    private SagaRepositoryPort sagaRepositoryPort;

    @InjectMocks
    private CadastroJuridicoUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processar_sucesso_deveEnviarMensagemEAtualizarStatus() {
        // Arrange
        SagaTopico sagaTopico = SagaTopico.builder()
                .etapaId(UUID.randomUUID().toString())
                .sagaId(UUID.randomUUID().toString())
                .build();
        EtapaSaga etapa = mock(EtapaSaga.class);

        when(etapaSagaRepositoryPort.buscarPorId(sagaTopico.getEtapaId()))
                .thenReturn(Optional.of(etapa));

        // Act
        useCase.processar(sagaTopico, ListenerEnum.SUCESSO);

        // Assert
        verify(produtorMensagemPort).enviaMensagem(Optional.of(etapa), "verificacao-financeira-start");
        verify(etapa).setStatus(StatusEtapaEnum.SUCESSO);
        verify(etapaSagaRepositoryPort).salvar(etapa);
        verifyNoMoreInteractions(sagaRepositoryPort);
    }

    @Test
    void processar_falha_deveAtualizarStatusEtapaEStatusSaga() {
        // Arrange
        UUID etapaId = UUID.randomUUID();
        UUID sagaId = UUID.randomUUID();
        SagaTopico sagaTopico = SagaTopico.builder()
                .etapaId(etapaId.toString())
                .sagaId(sagaId.toString())
                .build();
        EtapaSaga etapa = mock(EtapaSaga.class);
        Saga saga = mock(Saga.class);

        when(etapaSagaRepositoryPort.buscarPorId(etapaId.toString())).thenReturn(Optional.of(etapa));
        when(sagaRepositoryPort.buscarPorId(sagaId.toString())).thenReturn(Optional.of(saga));

        // Act
        useCase.processar(sagaTopico, ListenerEnum.FALHA);

        // Assert
        verify(etapa).setStatus(StatusEtapaEnum.FALHA);
        verify(etapaSagaRepositoryPort).salvar(etapa);
        verify(saga).setStatus(StatusSagaEnum.FALHA);
        verify(sagaRepositoryPort).salvar(saga);
    }

    @Test
    void processar_sucesso_semEtapaNaoDeveChamarNada() {
        // Arrange
        SagaTopico sagaTopico = SagaTopico.builder()
                .etapaId(UUID.randomUUID().toString())
                .sagaId(UUID.randomUUID().toString())
                .build();

        when(etapaSagaRepositoryPort.buscarPorId(sagaTopico.getEtapaId()))
                .thenReturn(Optional.empty());

        // Act
        useCase.processar(sagaTopico, ListenerEnum.SUCESSO);

        // Assert
        verifyNoInteractions(produtorMensagemPort);
        verify(etapaSagaRepositoryPort).buscarPorId(any());
        verifyNoMoreInteractions(etapaSagaRepositoryPort);
        verifyNoInteractions(sagaRepositoryPort);
    }

    @Test
    void processar_falha_semSagaNaoDeveChamarSalvarSaga() {
        // Arrange
        UUID etapaId = UUID.randomUUID();
        UUID sagaId = UUID.randomUUID();
        SagaTopico sagaTopico = SagaTopico.builder()
                .etapaId(etapaId.toString())
                .sagaId(sagaId.toString())
                .build();
        EtapaSaga etapa = mock(EtapaSaga.class);

        when(etapaSagaRepositoryPort.buscarPorId(etapaId.toString())).thenReturn(Optional.of(etapa));
        when(sagaRepositoryPort.buscarPorId(sagaId.toString())).thenReturn(Optional.empty());

        // Act
        useCase.processar(sagaTopico, ListenerEnum.FALHA);

        // Assert
        verify(etapa).setStatus(StatusEtapaEnum.FALHA);
        verify(etapaSagaRepositoryPort).salvar(etapa);
        verify(sagaRepositoryPort).buscarPorId(sagaId.toString());
        verifyNoMoreInteractions(sagaRepositoryPort);
    }

    @Test
    void processar_default_deveLogarWarn() {
        // Arrange
        SagaTopico sagaTopico = SagaTopico.builder()
                .etapaId(UUID.randomUUID().toString())
                .sagaId(UUID.randomUUID().toString())
                .build();

        // Act
        useCase.processar(sagaTopico, null);

        // Assert
        // Apenas verifica que não lança exceção e não interage com dependências
        verifyNoInteractions(produtorMensagemPort, sagaRepositoryPort);
    }
}