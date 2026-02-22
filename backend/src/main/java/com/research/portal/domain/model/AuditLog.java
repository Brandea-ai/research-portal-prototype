package com.research.portal.domain.model;

import java.time.LocalDateTime;

/**
 * Domain-Modell für einen Audit-Trail-Eintrag.
 *
 * Jede regulatorisch relevante Aktion im System wird als AuditLog
 * festgehalten. Dies ist ein zentraler Bestandteil der FINMA-Compliance:
 * Wer hat wann was geändert muss jederzeit nachvollziehbar sein.
 *
 * Keine Framework-Abhängigkeiten in der Domain-Schicht.
 */
public class AuditLog {

    private Long id;
    private LocalDateTime timestamp;
    private AuditAction action;
    private String entityType;
    private Long entityId;
    private String userId;
    private String userName;
    private String userRole;
    private String details;
    private String ipAddress;

    public AuditLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
