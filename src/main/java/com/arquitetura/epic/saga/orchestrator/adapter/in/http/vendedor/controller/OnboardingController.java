package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.controller;

import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.response.VendedorResponseDTO;
import com.arquitetura.epic.saga.orchestrator.core.port.in.onboarding.OnboardingVendedorPort;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.mappers.VendedorMapper;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request.VendedorDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/onboarding")
public class OnboardingController {

    private static final Logger log = LoggerFactory.getLogger(OnboardingController.class);

    private final OnboardingVendedorPort onboardingVendedorPort;
    private final VendedorMapper mapper;

    @PostMapping("/start")
    public VendedorResponseDTO startOnboarding(@RequestBody VendedorDTO vendedorDto) {
        log.info("=== Iniciando onboarding para o vendedor {}\n", vendedorDto.toString());
        var vendedor = mapper.toDomain(vendedorDto);
        onboardingVendedorPort.startSaga(vendedor);
        VendedorResponseDTO response = mapper.toResponseDTO(vendedor);
        response.setStatus("ONBOARDING_INICIADO");
        log.info("=== Onboarding Iniciado");
        return response;
    }

}
