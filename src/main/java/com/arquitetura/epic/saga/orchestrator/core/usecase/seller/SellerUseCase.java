package com.arquitetura.epic.saga.orchestrator.core.usecase.seller;

import com.arquitetura.epic.saga.orchestrator.core.port.in.seller.SellerPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerUseCase implements SellerPort {

    private static final Logger log = LoggerFactory.getLogger(SellerUseCase.class);

    private final ProdutorMensagemPort produtorMensagemPort;


    @Override
    public void process() {

    }
}