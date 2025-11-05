package com.arquitetura.epic.saga.orchestrator.core.port.in.seller;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Seller;

public interface SellerPort {

    void processEventRegisterSucess(Seller seller );

    void processEventRegisterFailure(Seller seller);

    void processEventCompensateSucess(Seller seller);

    void processEventCompensateFailure(Seller seller);
}