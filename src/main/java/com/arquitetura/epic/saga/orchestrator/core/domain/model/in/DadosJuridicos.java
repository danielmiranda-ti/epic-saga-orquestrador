package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DadosJuridicos {

    private String nomeFantasia;
    private String cnpj;
    private String urlLoja;
    private String logoUrl;
    private String politicaEntrega;
    private String politicaDevolucao;

}
