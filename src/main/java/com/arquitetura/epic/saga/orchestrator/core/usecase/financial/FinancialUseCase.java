package com.arquitetura.epic.saga.orchestrator.core.usecase.financial;

import com.arquitetura.epic.saga.orchestrator.core.port.in.financial.FinancialPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialUseCase implements FinancialPort {

    private static final Logger log = LoggerFactory.getLogger(FinancialUseCase.class);

    private final ProdutorMensagemPort produtorMensagemPort;

    @Value("${kafka.topic.command.register.store}")
    private String topicStore;

    @Override
    public void process() {

    }
}