package com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.repository;

import com.arquitetura.epic.saga.orchestrator.adapter.out.persistence.saga.entity.SagaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SagaRepository extends JpaRepository<SagaEntity, UUID> {
}
