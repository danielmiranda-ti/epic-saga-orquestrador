package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity;

public enum EtapaStatusEnum {
    PENDING,
    IN_PROGRESS,
    IN_COMPENSATION,
    COMPENSATED,
    COMPENSATED_FAILURE,
    SUCCESS,
    FAILURE
}
