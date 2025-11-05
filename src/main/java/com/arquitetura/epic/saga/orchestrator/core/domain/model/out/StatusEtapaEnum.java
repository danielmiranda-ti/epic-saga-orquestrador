package com.arquitetura.epic.saga.orchestrator.core.domain.model.out;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusEtapaEnum {
    PENDING,
    IN_PROGRESS,
    IN_COMPENSATION,
    COMPENSATED,
    COMPENSATED_FAILURE,
    SUCCESS,
    FAILURE
}
