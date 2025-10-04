package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.mappers;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Solicitacao;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request.SolicitacaoDTO;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.response.SolicitacaoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VendedorMapper {

    Solicitacao toDomain(SolicitacaoDTO dto);

    SolicitacaoResponseDTO toResponseDTO(Solicitacao vendedor);
}
