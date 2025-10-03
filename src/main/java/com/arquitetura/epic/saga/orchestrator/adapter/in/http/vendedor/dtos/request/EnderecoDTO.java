package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

@Data
public class EnderecoDTO {
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
