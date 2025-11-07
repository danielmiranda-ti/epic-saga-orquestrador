package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @Column(name = "seller_id")
    private String sellerId;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    // inicializa saga automaticamente em andamento
    @PrePersist
    public void prePersist() {
        if (this.dataInicio == null) {
            this.dataInicio = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = SagaStatus.IN_PROGRESS;
        }
        if(this.dataFim == null && SagaStatus.SUCCESS.equals(this.status)) {
            this.dataFim = LocalDateTime.now();
        }
    }
}
