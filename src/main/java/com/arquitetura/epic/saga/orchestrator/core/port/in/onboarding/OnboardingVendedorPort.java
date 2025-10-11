package com.arquitetura.epic.saga.orchestrator.core.port.in.onboarding;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Solicitacao;

public interface OnboardingVendedorPort {

    String startSaga(Solicitacao vendedor);
}
