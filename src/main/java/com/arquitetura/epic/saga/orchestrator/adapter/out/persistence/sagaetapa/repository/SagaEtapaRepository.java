package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.repository;

import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.sagaetapa.entity.EtapaSagaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SagaEtapaRepository extends JpaRepository<EtapaSagaEntity, UUID> {

    List<EtapaSagaEntity> findBySagaIdAndNomeEtapaIn(UUID sagaId, List<String> tipos);
}
