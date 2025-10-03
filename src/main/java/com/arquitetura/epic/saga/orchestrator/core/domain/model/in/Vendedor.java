package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Vendedor implements Serializable {

    private UUID vendedorId;

    private DadosPessoais dadosPessoais;
    private DadosJuridicos dadosJuridicos;
    private DadosBancarios dadosBancarios;
    private DadosLoja dadosLoja;

    private List<Documento> documentos;

    private Metadados metadados;

}
