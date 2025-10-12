package com.arquitetura.epic.saga.orchestrator.core.usecase.onboarding;

import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Solicitacao;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.shared.TipoEtapaEnum;
import com.arquitetura.epic.saga.orchestrator.core.port.in.onboarding.OnboardingVendedorPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.produtormensagem.ProdutorMensagemPort;
import com.arquitetura.epic.saga.orchestrator.core.service.saga.SagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OnboardingVendedorUseCase implements OnboardingVendedorPort {

    private final ProdutorMensagemPort produtorMensagemPort;
    private final SagaService sagaService;

    @Value("${kafka.topic.command.register.seller}")
    private String topicoCadastroVendedorStart;

    @Override
    public String startSaga(Solicitacao solicitacao) {

        // Tenta buscar uma saga existente
        return sagaService.buscarSagaPorSolicitacaoId(solicitacao.getSolicitacaoId().toString())
                .map(saga -> saga.getStatus().name())
                .orElseGet(() -> {
                    // Registra nova saga se não existir
                    var saga = sagaService.registrarSaga(solicitacao);

                    // Busca a etapa de dados pessoais
                    saga.getEtapasSaga().stream()
                            .filter(etapa -> TipoEtapaEnum.SELLER_REGISTRATION.name().equals(etapa.getNomeEtapa()))
                            .findFirst()
                            .ifPresent(etapa -> produtorMensagemPort.enviaMensagem(Optional.of(etapa), "cadastro-vendedor-start"));

                    return "INICIADO";
                });
    }

}
