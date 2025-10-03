package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.response;

import lombok.Data;

@Data
public class VendedorResponseDTO {
    private String vendedorId;
    private String status;
}
