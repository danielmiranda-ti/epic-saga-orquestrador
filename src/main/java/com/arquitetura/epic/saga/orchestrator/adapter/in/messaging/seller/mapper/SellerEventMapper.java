package com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.seller.mapper;

import com.arquitetura.epic.saga.orchestrator.adapter.in.messaging.seller.dto.SellerEvent;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Seller;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SellerEventMapper {
    Seller toDomain(SellerEvent sellerEvent);
}
