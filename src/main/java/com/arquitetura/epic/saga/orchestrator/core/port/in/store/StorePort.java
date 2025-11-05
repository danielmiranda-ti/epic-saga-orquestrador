package com.arquitetura.epic.saga.orchestrator.core.port.in.store;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Store;

public interface StorePort {

    void processEventRegisterSucess(Store store);
    void processEventRegisterFailure(Store store);
    void processEventCompensateSucess(Store store);
    void processEventCompensateFailure(Store store);
    
    /*
    * void processEventRegisterSucess(Seller seller );

    void processEventRegisterFailure(Seller seller);

    void processEventCompensateSucess(Seller seller);

    void processEventCompensateFailure(Seller seller);
    * 
    * */
}