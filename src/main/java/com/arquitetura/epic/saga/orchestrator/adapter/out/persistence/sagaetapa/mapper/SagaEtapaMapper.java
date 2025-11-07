package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.mapper;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity.EtapaSagaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface SagaEtapaMapper {

    @Mapping(target = "saga.id", source = "sagaId")
    EtapaSaga toDomain(EtapaSagaEntity entity);

    @Mapping(target = "sagaId", source = "saga.id")
    EtapaSagaEntity toEntity(EtapaSaga sagaEtapa);
}
