package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.mapper;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity.EtapaSagaEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SagaEtapaMapper {

    EtapaSaga toDomain(EtapaSagaEntity entity);

    EtapaSagaEntity toEntity(EtapaSaga sagaEtapa);
}
