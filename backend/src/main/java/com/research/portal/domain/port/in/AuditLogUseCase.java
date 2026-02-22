package com.research.portal.domain.port.in;

import com.research.portal.domain.model.AuditAction;
import com.research.portal.domain.model.AuditLog;

import java.util.List;

/**
 * Use Case: Audit-Trail abfragen und Aktionen protokollieren.
 *
 * Stellt die Eingangs-Schnittstelle für das Audit-System bereit.
 * Controller und Aspekte nutzen dieses Interface, um Audit-Einträge
 * zu erstellen und abzufragen. FINMA-Compliance erfordert lückenlose
 * Nachvollziehbarkeit aller relevanten Aktionen.
 */
public interface AuditLogUseCase {

    /**
     * Protokolliert eine auditierbare Aktion im System.
     *
     * @param action     Art der Aktion (CREATE, UPDATE, DELETE, etc.)
     * @param entityType Typ der betroffenen Entität (REPORT, SECURITY, ANALYST)
     * @param entityId   ID der betroffenen Entität (kann null sein, z.B. bei LOGIN)
     * @param details    Beschreibung der Aktion im Klartext
     */
    void log(AuditAction action, String entityType, Long entityId, String details);

    /**
     * Gibt die letzten Audit-Einträge zurück (neueste zuerst).
     *
     * @param limit Maximale Anzahl der zurückgegebenen Einträge
     * @return Liste der Audit-Einträge, absteigend nach Zeitstempel sortiert
     */
    List<AuditLog> getRecentLogs(int limit);

    /**
     * Gibt alle Audit-Einträge für eine bestimmte Entität zurück.
     *
     * @param entityType Typ der Entität (z.B. "REPORT")
     * @param entityId   ID der Entität
     * @return Liste der Audit-Einträge für diese Entität
     */
    List<AuditLog> getLogsByEntity(String entityType, Long entityId);
}
