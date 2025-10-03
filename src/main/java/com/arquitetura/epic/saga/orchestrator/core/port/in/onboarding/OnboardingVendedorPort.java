package com.arquitetura.epic.saga.orchestrator.core.port.in.onboarding;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Vendedor;

public interface OnboardingVendedorPort {

    void startSaga(Vendedor vendedor);
}
