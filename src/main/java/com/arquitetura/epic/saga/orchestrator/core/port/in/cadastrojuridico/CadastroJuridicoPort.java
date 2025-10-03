package com.arquitetura.epic.saga.orchestrator.core.port.in.cadastrojuridico;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.ListenerEnum;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.SagaTopico;

public interface CadastroJuridicoPort {

    void processar(SagaTopico sagaTopico, ListenerEnum listenerEnum);

}
