package com.research.portal.adapter.out.persistence.adapter;

import com.research.portal.adapter.out.persistence.mapper.AuditLogPersistenceMapper;
import com.research.portal.adapter.out.persistence.repository.JpaAuditLogRepository;
import com.research.portal.domain.model.AuditLog;
import com.research.portal.domain.port.out.AuditLogRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Persistence-Adapter für den Audit-Trail.
 *
 * Implementiert den AuditLogRepository-Port und delegiert
 * die Datenbankoperationen an das Spring Data JPA Repository.
 * Konvertiert zwischen Domain-Modell und JPA-Entity.
 */
@Repository
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final JpaAuditLogRepository jpaRepository;
    private final AuditLogPersistenceMapper mapper;

    public AuditLogPersistenceAdapter(JpaAuditLogRepository jpaRepository,
                                       AuditLogPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        var entity = mapper.toEntity(auditLog);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<AuditLog> findRecentLogs(int limit) {
        return jpaRepository.findAllByOrderByTimestampDesc().stream()
                .limit(limit)
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId) {
        return jpaRepository
                .findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
