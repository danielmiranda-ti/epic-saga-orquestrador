package com.arquitetura.epic.saga.orchestrator.core.usecase.onboarding;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Vendedor;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.SagaEtapa;
import com.arquitetura.epic.saga.orchestrator.core.port.in.onboarding.OnboardingVendedorPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingVendedorUseCase implements OnboardingVendedorPort {

    private final ProdutorMensagemPort produtorMensagemPort;
    private final SagaService sagaService;

    @Override
    public void startSaga(Vendedor vendedor) {
        // Registra a entrada dos dados para controlar a orquestração.
        var saga = sagaService.registrarSaga(vendedor);

        List<SagaEtapa> etapas = saga.getEtapasSaga();

        var etapaJuridica = etapas
                .stream()
                .filter(sagaEtapa -> "cadastro-juridico".equals(sagaEtapa.getNomeEtapa()))
                .findFirst();

        produtorMensagemPort.enviaMensagem(etapaJuridica, "cadastro-juridico-start");

    }

}
