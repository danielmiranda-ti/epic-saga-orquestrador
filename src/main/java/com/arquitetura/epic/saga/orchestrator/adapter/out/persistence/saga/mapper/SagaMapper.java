package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.mapper;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.Saga;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.entity.SagaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SagaMapper {
    Saga toDomain(SagaEntity entity);
    SagaEntity toEntity(Saga saga);
}
