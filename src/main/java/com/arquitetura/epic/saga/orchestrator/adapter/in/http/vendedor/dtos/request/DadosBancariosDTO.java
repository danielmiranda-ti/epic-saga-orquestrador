package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

@Data
public class DadosBancariosDTO {
    private String banco;
    private String agencia;
    private String conta;
}
