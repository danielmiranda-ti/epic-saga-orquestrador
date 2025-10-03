package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

@Data
public class DocumentoDTO {
    private String tipo;
    private String url;
}
