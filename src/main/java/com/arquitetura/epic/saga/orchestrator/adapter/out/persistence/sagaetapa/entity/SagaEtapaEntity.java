package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity;

import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.entity.SagaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saga_etapas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaEtapaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_id", nullable = false)
    @ToString.Exclude // evita loop no Lombok
    @EqualsAndHashCode.Exclude
    private SagaEntity saga;

    @Column(nullable = false)
    private String nomeEtapa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtapaStatusEnum status;

    @Lob
    private String payloadUsado; // JSON do payload da etapa

    @Column(nullable = false)
    private LocalDateTime dataExecucao;

    @PrePersist
    public void prePersist() {
        if (this.dataExecucao == null) {
            this.dataExecucao = LocalDateTime.now();
        }
//        if (this.status == null) {
//            this.status = EtapaStatus.EM_ANDAMENTO;
//        }
    }
}
