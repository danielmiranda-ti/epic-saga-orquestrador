package com.arquitetura.epic.saga.orchestrator.core.domain.model.out;

import lombok.*;

@Data
@Builder
public class EtapaInfo {

    private String nome;
    private Object dados;
    private StatusEtapaEnum status;
}
