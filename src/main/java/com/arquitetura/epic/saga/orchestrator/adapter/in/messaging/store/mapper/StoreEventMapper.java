package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.store.mapper;

import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.store.dto.StoreEvent;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Store;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreEventMapper {
    Store toDomain(StoreEvent storeEvent);
}
