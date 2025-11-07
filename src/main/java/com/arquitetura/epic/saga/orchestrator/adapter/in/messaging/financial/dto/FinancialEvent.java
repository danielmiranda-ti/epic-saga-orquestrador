package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.financial.dto;

import lombok.Data;

@Data
public class FinancialEvent {
    private String requestId;
    private String financialId;
    private String sellerId;
    private String type;
    private String reasonFailure;
}
