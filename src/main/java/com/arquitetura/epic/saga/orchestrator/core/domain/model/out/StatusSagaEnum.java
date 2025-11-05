package com.arquitetura.epic.saga.orchestrator.core.domain.model.out;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusSagaEnum {

    IN_PROGRESS,
    IN_COMPENSATION,
    COMPENSATED,
    SUCCESS,
    FAILURE;
}
