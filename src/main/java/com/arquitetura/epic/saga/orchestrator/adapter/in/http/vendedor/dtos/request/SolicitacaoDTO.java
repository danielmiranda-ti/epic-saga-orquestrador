package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

import java.util.List;

@Data
public class SolicitacaoDTO {

    private String solicitacaoId;
    private DadosPessoaisDTO dadosPessoais;
    private DadosJuridicosDTO dadosJuridicos;
    private DadosBancariosDTO dadosBancarios;

}
