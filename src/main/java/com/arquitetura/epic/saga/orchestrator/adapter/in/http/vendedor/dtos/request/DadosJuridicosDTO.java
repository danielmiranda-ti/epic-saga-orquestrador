package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

@Data
public class DadosJuridicosDTO {
    private String razaoSocial;
    private String cnpj;
    private String inscricaoEstadual;
}
