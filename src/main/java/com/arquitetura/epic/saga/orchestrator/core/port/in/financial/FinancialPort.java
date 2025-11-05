package com.arquitetura.epic.saga.orchestrator.core.port.in.financial;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Financial;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Seller;

public interface FinancialPort {

//    void process();

    void processEventRegisterSucess(Financial financial);

    void processEventRegisterFailure(Financial financial);

    void processEventCompensateSucess(Financial financial);

    void processEventCompensateFailure(Financial financial);
}
