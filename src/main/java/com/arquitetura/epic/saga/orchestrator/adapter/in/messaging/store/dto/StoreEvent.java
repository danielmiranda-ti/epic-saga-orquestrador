package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.store.dto;

import lombok.Data;

@Data
public class StoreEvent {
    private String requestId;
    private String storeId;
    private String sellerId;
    private String type;
    private String reasonFailure;
}
