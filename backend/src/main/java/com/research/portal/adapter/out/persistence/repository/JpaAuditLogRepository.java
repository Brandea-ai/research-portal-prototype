package com.research.portal.adapter.out.persistence.repository;

import com.research.portal.adapter.out.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA Repository für Audit-Log-Einträge.
 *
 * Stellt die Datenbankabfragen für den Audit-Trail bereit.
 */
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    /**
     * Findet die neuesten Audit-Einträge, absteigend nach Zeitstempel sortiert.
     */
    List<AuditLogEntity> findAllByOrderByTimestampDesc();

    /**
     * Findet alle Audit-Einträge für eine bestimmte Entität.
     *
     * @param entityType Typ der Entität (z.B. "REPORT")
     * @param entityId   ID der Entität
     * @return Audit-Einträge, absteigend nach Zeitstempel sortiert
     */
    List<AuditLogEntity> findByEntityTypeAndEntityIdOrderByTimestampDesc(
            String entityType, Long entityId);
}
