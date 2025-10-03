package com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.mappers;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Vendedor;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.request.VendedorDTO;
import com.arquitetura.epic.saga.orchestrator.adapter.in.http.vendedor.dtos.response.VendedorResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VendedorMapper {

    Vendedor toDomain(VendedorDTO dto);

    VendedorResponseDTO toResponseDTO(Vendedor vendedor);
}
