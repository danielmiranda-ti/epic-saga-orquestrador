package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request;

import lombok.Data;

import java.util.List;

@Data
public class VendedorDTO {

    private String vendedorId;
    private DadosPessoaisDTO dadosPessoais;
    private DadosJuridicosDTO dadosJuridicos;
    private DadosBancariosDTO dadosBancarios;
    private DadosLojaDTO dadosLoja;
    private List<DocumentoDTO> documentos;
    private MetadadosDTO metadados;
}
