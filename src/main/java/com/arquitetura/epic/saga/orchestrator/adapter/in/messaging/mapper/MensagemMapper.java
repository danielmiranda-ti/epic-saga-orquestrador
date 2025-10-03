package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.mapper;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.SagaTopico;
import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.dto.share.SagaRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MensagemMapper {

    SagaTopico toDomain(SagaRequestDTO sagaRequestDTO);
}
