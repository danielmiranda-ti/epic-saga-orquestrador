package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.controller;

import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.mappers.VendedorMapper;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request.SolicitacaoDTO;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.response.SolicitacaoResponseDTO;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Solicitacao;
import com.arquitetura.epic.saga.orchestrator.core.port.in.onboarding.OnboardingVendedorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnboardingControllerTest {

    private OnboardingVendedorPort onboardingVendedorPort;
    private VendedorMapper mapper;
    private OnboardingController controller;

    @BeforeEach
    void setUp() {
        onboardingVendedorPort = mock(OnboardingVendedorPort.class);
        mapper = mock(VendedorMapper.class);
        controller = new OnboardingController(onboardingVendedorPort, mapper);
    }

    @Test
    void startOnboarding_deveRetornarResponseComStatusIniciado() {
        // Arrange
        SolicitacaoDTO vendedorDTO = new SolicitacaoDTO();
        Solicitacao vendedor = Solicitacao.builder().build();
        SolicitacaoResponseDTO responseDTO = new SolicitacaoResponseDTO();
        responseDTO.setSolicitacaoId("123");

        when(mapper.toDomain(vendedorDTO)).thenReturn(vendedor);
        when(mapper.toResponseDTO(vendedor)).thenReturn(responseDTO);
        when(onboardingVendedorPort.startSaga(vendedor)).thenReturn("INICIADO");

        // Act
        SolicitacaoResponseDTO result = controller.startOnboarding(vendedorDTO);

        // Assert
        verify(onboardingVendedorPort).startSaga(vendedor);
        verify(mapper).toDomain(vendedorDTO);
        verify(mapper).toResponseDTO(vendedor);

        assertEquals("123", result.getSolicitacaoId());
        assertEquals("INICIADO", result.getStatus());
    }

}