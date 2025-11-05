package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Financial {
    private String requestId;
    private String type;
    private String sellerId;
    private String reasonFailure;
}
