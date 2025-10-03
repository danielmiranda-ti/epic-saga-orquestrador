package com.arquitetura.epic.saga.orchestrator.core.domain.model.out;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusSagaEnum {

    EM_ANDAMENTO,
    SUCESSO,
    FALHA;
}
