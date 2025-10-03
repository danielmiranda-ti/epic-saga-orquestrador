package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.controller;

import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.mappers.VendedorMapper;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request.VendedorDTO;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.response.VendedorResponseDTO;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Vendedor;
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
        VendedorDTO vendedorDTO = new VendedorDTO();
        Vendedor vendedor = Vendedor.builder().build();
        VendedorResponseDTO responseDTO = new VendedorResponseDTO();
        responseDTO.setVendedorId("123");

        when(mapper.toDomain(vendedorDTO)).thenReturn(vendedor);
        when(mapper.toResponseDTO(vendedor)).thenReturn(responseDTO);

        // Act
        VendedorResponseDTO result = controller.startOnboarding(vendedorDTO);

        // Assert
        verify(onboardingVendedorPort).startSaga(vendedor);
        verify(mapper).toDomain(vendedorDTO);
        verify(mapper).toResponseDTO(vendedor);

        assertEquals("123", result.getVendedorId());
        assertEquals("ONBOARDING_INICIADO", result.getStatus());
    }

}