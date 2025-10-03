package com.arquitetura.epic.saga.orchestrator.core.service.saga;

import com.arquitetura.epic.saga.orchestrator.infraestrutura.util.JsonUtil;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.in.Vendedor;
import com.arquitetura.epic.saga.orchestrator.core.domain.model.out.*;
import com.arquitetura.epic.saga.orchestrator.core.port.out.etapasaga.EtapaSagaRepositoryPort;
import com.arquitetura.epic.saga.orchestrator.core.port.out.saga.SagaRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
//@AllArgsConstructor
public class SagaService {

    private static final String ETAPA_CADASTRO_JURIDICO = "cadastro-juridico";
    private static final String ETAPA_ANALISE_DOCUMENTAL = "analise-documental";
    private static final String ETAPA_INTEGRACAO_CONTABIL = "integracao-contabil";
    private static final String ETAPA_CONFIGURACAO_LOJA = "configuracao-loja";

    private final SagaRepositoryPort sagaRepositoryPort;
    private final EtapaSagaRepositoryPort etapaSagaRepositoryPort;
    private final JsonUtil jsonUtil;

    @Transactional
    public Saga registrarSaga(Vendedor vendedor) {

        if (vendedor == null || vendedor.getVendedorId() == null) {
            throw new IllegalArgumentException("Vendedor ou vendedorId não pode ser nulo");
        }

        var saga = Saga.builder()
                .vendedorId(vendedor.getVendedorId())
                .status(StatusSagaEnum.EM_ANDAMENTO)
                .build();

        var sagaPersistida = sagaRepositoryPort.salvar(saga);

        List<EtapaInfo> etapas = List.of(
                EtapaInfo.builder().nome(ETAPA_CADASTRO_JURIDICO).dados(vendedor.getDadosJuridicos()).build(),
                EtapaInfo.builder().nome(ETAPA_ANALISE_DOCUMENTAL).dados(vendedor.getDocumentos()).build(),
                EtapaInfo.builder().nome(ETAPA_INTEGRACAO_CONTABIL).dados(vendedor.getDadosBancarios()).build(),
                EtapaInfo.builder().nome(ETAPA_CONFIGURACAO_LOJA).dados(vendedor.getDadosLoja()).build()
        );

        etapas.forEach(etapa -> {
            var sagaEtapa = criarEtapa(sagaPersistida, etapa.getNome(), etapa.getDados());
            sagaPersistida.getEtapasSaga().add(sagaEtapa);
        });

        return sagaPersistida;
    }

    private SagaEtapa criarEtapa(Saga saga, String nomeEtapa, Object payload) {
        var sagaEtapa = SagaEtapa.builder()
                .saga(saga)
                .nomeEtapa(nomeEtapa)
                .payloadUsado(jsonUtil.toJson(payload))
                .status(StatusEtapaEnum.EM_ANDAMENTO)
                .build();

        return etapaSagaRepositoryPort.salvar(sagaEtapa);
    }

}
