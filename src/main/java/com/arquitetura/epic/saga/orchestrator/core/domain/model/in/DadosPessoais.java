package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class DadosPessoais {

    private String nomeCompleto;
    private String cpfCnpj;
    private String email;
    private String telefone;
    private Endereco endereco;
}
