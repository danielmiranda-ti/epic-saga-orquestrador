package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.response;

import lombok.Data;

@Data
public class SolicitacaoResponseDTO {
    private String solicitacaoId;
    private String status;
}
