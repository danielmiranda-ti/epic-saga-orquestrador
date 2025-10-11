package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.dto.share;

import lombok.Data;

@Data

public class SagaRequestDTO {
    private String requestId;
    private String type;
    private String sellerId;
}
