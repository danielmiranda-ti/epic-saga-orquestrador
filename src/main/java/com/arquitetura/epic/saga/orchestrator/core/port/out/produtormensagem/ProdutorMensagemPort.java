package com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.SagaEtapa;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Component
public interface ProdutorMensagemPort {

    void enviaMensagem(Optional<SagaEtapa> etapaOpt, String topico);

}
