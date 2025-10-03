package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

@Data
public class DadosLojaDTO {
    private String nomeLoja;
    private String categoria;
    private String descricao;
}
