package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

@Data
public class DadosJuridicosDTO {
    private String nomeFantasia;
    private String cnpj;
    private String inscricaoEstadual;
    private String urlLoja;
    private String logoUrl;
    private String politicaEntrega;
    private String politicaDevolucao;
}
