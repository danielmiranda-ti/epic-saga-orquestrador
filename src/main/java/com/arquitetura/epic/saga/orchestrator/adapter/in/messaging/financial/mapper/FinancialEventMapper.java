package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.financial.mapper;

import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.financial.dto.FinancialEvent;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Financial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FinancialEventMapper {
    Financial toDomain(FinancialEvent financialEvent);
}
