package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DadosPessoais {

    private String nomeCompleto;
    private String documento;
    private String email;
    private String telefone;
    private Endereco endereco;
}
