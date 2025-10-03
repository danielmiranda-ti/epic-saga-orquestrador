package com.arquitetura.epic.saga.orchestrator.adapter.out.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensagemDTO {

    private String sagaId;
    private String vendedorId;
    private Object payload;
}
