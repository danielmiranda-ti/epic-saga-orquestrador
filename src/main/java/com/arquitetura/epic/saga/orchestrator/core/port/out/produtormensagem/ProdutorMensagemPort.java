package com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.EtapaSaga;

import java.util.Optional;

//@Component
public interface ProdutorMensagemPort {

    void enviaMensagem(Optional<EtapaSaga> etapaOpt, String topico);

}
