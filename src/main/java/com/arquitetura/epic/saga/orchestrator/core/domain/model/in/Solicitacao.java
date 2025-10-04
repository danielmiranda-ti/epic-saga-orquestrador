package com.arquitetura.epic.saga.orchestrator.core.domain.model.in;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class Solicitacao implements Serializable {

    private UUID solicitacaoId;

    private DadosPessoais dadosPessoais;
    private DadosJuridicos dadosJuridicos;
    private DadosBancarios dadosBancarios;
//    private DadosLoja dadosLoja;
//
//    private List<Documento> documentos;
//
//    private Metadados metadados;

}
