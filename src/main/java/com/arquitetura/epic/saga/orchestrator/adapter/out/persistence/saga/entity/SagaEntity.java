package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.entity;

import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity.EtapaSagaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sagas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID solicitacaoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @Builder.Default
    @OneToMany(mappedBy = "saga", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EtapaSagaEntity> etapas = new ArrayList<>();

    // método utilitário para adicionar etapa
//    public void adicionarEtapa(SagaEtapaEntity etapa) {
//        etapa.setSaga(this);
//        this.etapas.add(etapa);
//    }

    // inicializa saga automaticamente em andamento
    @PrePersist
    public void prePersist() {
        if (this.dataInicio == null) {
            this.dataInicio = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = SagaStatus.EM_ANDAMENTO;
        }
    }
}
