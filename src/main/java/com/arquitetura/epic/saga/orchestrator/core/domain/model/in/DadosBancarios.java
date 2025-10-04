package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class DadosBancarios {

    private String banco;
    private String agencia;
    private String numeroConta;
    private String tipoConta;

}
