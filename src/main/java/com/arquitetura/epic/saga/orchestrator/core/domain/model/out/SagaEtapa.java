package com.arquitetura.epic.saga.orchestrator.core.domain.model.out;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SagaEtapa {

    private UUID id;

    private Saga saga;

    private String nomeEtapa;

    private StatusEtapaEnum status;

    private String payloadUsado;

    private LocalDateTime dataExecucao;
}
