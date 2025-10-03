package com.arquitetura.epic.saga.orchestrator.core.domain.model.out;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Saga {

    private UUID id;

    private UUID vendedorId;

    private StatusSagaEnum status;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @Builder.Default
    private List<SagaEtapa> etapasSaga = new ArrayList<>();;
}
