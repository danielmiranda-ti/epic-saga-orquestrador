package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.mapper;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.SagaEtapa;
import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity.SagaEtapaEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SagaEtapaMapper {

    SagaEtapa toDomain(SagaEtapaEntity entity);

    SagaEtapaEntity toEntity(SagaEtapa sagaEtapa);
}
