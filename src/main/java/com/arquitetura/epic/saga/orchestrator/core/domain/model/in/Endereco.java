package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Endereco {
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String complemento;
    private String pais;
}
