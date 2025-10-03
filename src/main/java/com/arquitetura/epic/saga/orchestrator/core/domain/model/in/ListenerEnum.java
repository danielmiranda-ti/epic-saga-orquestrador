package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ListenerEnum {
    SUCESSO,
    FALHA
}
