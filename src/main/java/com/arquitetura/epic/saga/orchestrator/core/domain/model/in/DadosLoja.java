package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class DadosLoja {
    private String nomeFantasia;
    private String categoriaPrincipal;
    private String descricao;
    private String urlLoja;
    private String politicaEntrega;
    private String politicaDevolucao;
}
