package com.research.portal.application.service;

import com.research.portal.domain.model.AuditAction;
import com.research.portal.domain.model.AuditLog;
import com.research.portal.domain.port.in.AuditLogUseCase;
import com.research.portal.domain.port.out.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application Service für das Audit-Trail-System.
 *
 * Implementiert die FINMA-konforme Protokollierung aller relevanten
 * Benutzeraktionen. Jede Aktion wird mit Zeitstempel, Benutzer,
 * betroffener Entität und Beschreibung festgehalten.
 *
 * Da das System aktuell keine Authentifizierung hat, wird ein
 * Standard-Benutzer (system/System Administrator) verwendet.
 * In der Produktionsumgebung würde hier der authentifizierte
 * Benutzer aus dem SecurityContext gelesen werden.
 */
@Service
public class AuditService implements AuditLogUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private static final String DEFAULT_USER_ID = "system";
    private static final String DEFAULT_USER_NAME = "System Administrator";
    private static final String DEFAULT_USER_ROLE = "ADMIN";

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void log(AuditAction action, String entityType, Long entityId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setUserId(DEFAULT_USER_ID);
        auditLog.setUserName(DEFAULT_USER_NAME);
        auditLog.setUserRole(DEFAULT_USER_ROLE);
        auditLog.setDetails(details);

        AuditLog saved = auditLogRepository.save(auditLog);

        log.info("Audit [{}] {} {} (ID: {}) - {}",
                saved.getId(), action, entityType, entityId, details);
    }

    @Override
    public List<AuditLog> getRecentLogs(int limit) {
        if (limit <= 0) {
            limit = 50;
        }
        return auditLogRepository.findRecentLogs(limit);
    }

    @Override
    public List<AuditLog> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
}
