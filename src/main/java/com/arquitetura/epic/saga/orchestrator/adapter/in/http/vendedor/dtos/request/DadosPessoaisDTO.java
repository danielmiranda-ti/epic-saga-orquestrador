package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

@Data
public class DadosPessoaisDTO {

    private String nomeCompleto;
    private String documento;
    private String email;
    private String telefone;
    private EnderecoDTO endereco;

}
