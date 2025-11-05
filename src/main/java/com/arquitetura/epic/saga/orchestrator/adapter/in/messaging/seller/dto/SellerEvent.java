package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.seller.dto;

import lombok.Data;

@Data
public class SellerEvent {
    private String requestId;
    private String type;
    private String sellerId;
    private String reasonFailure;
}
