package com.research.portal.adapter.out.persistence.mapper;

import com.research.portal.adapter.out.persistence.entity.AuditLogEntity;
import com.research.portal.domain.model.AuditAction;
import com.research.portal.domain.model.AuditLog;
import org.springframework.stereotype.Component;

/**
 * Mapper zwischen AuditLog Domain-Modell und AuditLogEntity (JPA).
 *
 * Trennt die Domain-Schicht sauber von der Persistence-Schicht.
 * Konvertiert AuditAction Enum zu/von String für die Datenbank.
 */
@Component
public class AuditLogPersistenceMapper {

    /**
     * Konvertiert eine JPA-Entity in das Domain-Modell.
     *
     * @param entity die JPA-Entity
     * @return das Domain-Modell
     */
    public AuditLog toDomain(AuditLogEntity entity) {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(entity.getId());
        auditLog.setTimestamp(entity.getTimestamp());
        auditLog.setAction(AuditAction.valueOf(entity.getAction()));
        auditLog.setEntityType(entity.getEntityType());
        auditLog.setEntityId(entity.getEntityId());
        auditLog.setUserId(entity.getUserId());
        auditLog.setUserName(entity.getUserName());
        auditLog.setUserRole(entity.getUserRole());
        auditLog.setDetails(entity.getDetails());
        auditLog.setIpAddress(entity.getIpAddress());
        return auditLog;
    }

    /**
     * Konvertiert ein Domain-Modell in eine JPA-Entity.
     *
     * @param domain das Domain-Modell
     * @return die JPA-Entity
     */
    public AuditLogEntity toEntity(AuditLog domain) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(domain.getId());
        entity.setTimestamp(domain.getTimestamp());
        entity.setAction(domain.getAction().name());
        entity.setEntityType(domain.getEntityType());
        entity.setEntityId(domain.getEntityId());
        entity.setUserId(domain.getUserId());
        entity.setUserName(domain.getUserName());
        entity.setUserRole(domain.getUserRole());
        entity.setDetails(domain.getDetails());
        entity.setIpAddress(domain.getIpAddress());
        return entity;
    }
}
