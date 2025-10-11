package com.arquitetura.epic.saga.orchestrator.core.usecase.store;

import com.arquitetura.epic.saga.orchestrator.core.port.in.store.StorePort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreUseCase implements StorePort {

    private static final Logger log = LoggerFactory.getLogger(StoreUseCase.class);

    private final ProdutorMensagemPort produtorMensagemPort;

    @Override
    public void process() {

    }
}