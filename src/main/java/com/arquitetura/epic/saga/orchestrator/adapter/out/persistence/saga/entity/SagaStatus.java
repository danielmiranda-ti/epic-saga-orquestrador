package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.entity;

public enum SagaStatus {
    IN_PROGRESS,
    IN_COMPENSATION,
    COMPENSATED,
    SUCCESS,
    FAILURE;
}
