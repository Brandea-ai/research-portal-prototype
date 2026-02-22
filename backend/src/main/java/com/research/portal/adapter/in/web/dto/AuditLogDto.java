package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO für Audit-Log-Einträge in der REST API.
 *
 * Transportiert Audit-Daten an das Frontend ohne JPA-Entity
 * direkt zu exponieren (Hexagonale Architektur).
 */
@Schema(description = "Audit-Trail-Eintrag: Protokolliert eine Benutzeraktion im System (FINMA-Compliance)")
public class AuditLogDto {

    @Schema(description = "Eindeutige ID des Audit-Eintrags", example = "1")
    private Long id;

    @Schema(description = "Zeitstempel der Aktion", example = "2026-02-23T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Art der Aktion", example = "CREATE",
            allowableValues = {"CREATE", "UPDATE", "DELETE", "VIEW", "EXPORT", "LOGIN", "LOGOUT", "IMPORT"})
    private String action;

    @Schema(description = "Typ der betroffenen Entität", example = "REPORT")
    private String entityType;

    @Schema(description = "ID der betroffenen Entität", example = "1")
    private Long entityId;

    @Schema(description = "Benutzer-ID", example = "system")
    private String userId;

    @Schema(description = "Name des Benutzers", example = "System Administrator")
    private String userName;

    @Schema(description = "Rolle des Benutzers", example = "ADMIN")
    private String userRole;

    @Schema(description = "Beschreibung der Aktion", example = "Report 'Nestlé Update' erstellt")
    private String details;

    @Schema(description = "IP-Adresse des Benutzers", example = "192.168.1.100")
    private String ipAddress;

    public AuditLogDto() {
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
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
