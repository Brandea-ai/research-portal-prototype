package com.research.portal.domain.port.out;

import com.research.portal.domain.model.AuditLog;

import java.util.List;

/**
 * Port (Ausgangs-Schnittstelle) für die Audit-Log-Persistenz.
 *
 * Die Domain definiert WAS sie braucht (Speichern, Abfragen).
 * Der Persistence-Adapter entscheidet WIE er es liefert (JPA, JDBC, etc.).
 */
public interface AuditLogRepository {

    /**
     * Speichert einen neuen Audit-Eintrag.
     *
     * @param auditLog der zu speichernde Eintrag
     * @return der gespeicherte Eintrag mit generierter ID
     */
    AuditLog save(AuditLog auditLog);

    /**
     * Gibt die neuesten Audit-Einträge zurück.
     *
     * @param limit maximale Anzahl
     * @return Liste der Einträge, absteigend nach Zeitstempel
     */
    List<AuditLog> findRecentLogs(int limit);

    /**
     * Gibt alle Audit-Einträge für eine bestimmte Entität zurück.
     *
     * @param entityType Typ der Entität
     * @param entityId   ID der Entität
     * @return Liste der Einträge für diese Entität
     */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
}
