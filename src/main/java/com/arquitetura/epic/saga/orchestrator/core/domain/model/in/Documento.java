package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class Documento {
    private String tipo;
    private String url;
}
