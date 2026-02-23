package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO fuer einen Watchlist-Eintrag in der API-Antwort.
 *
 * Enthaelt die Watchlist-Daten zusammen mit den Stammdaten
 * der beobachteten Wertschrift (Ticker, Name).
 */
@Schema(description = "Watchlist-Eintrag mit Wertschrift-Informationen")
public class WatchlistEntryDto {

    @Schema(description = "Eindeutige Watchlist-Eintrags-ID", example = "1")
    private Long id;

    @Schema(description = "Benutzer-ID des Analysten", example = "demo-user")
    private String userId;

    @Schema(description = "Wertschrift-ID", example = "1")
    private Long securityId;

    @Schema(description = "Boersenticker der Wertschrift", example = "NESN")
    private String securityTicker;

    @Schema(description = "Name der Wertschrift", example = "Nestlé SA")
    private String securityName;

    @Schema(description = "Zeitpunkt der Aufnahme in die Watchlist", example = "2026-02-20T09:30:00")
    private LocalDateTime addedAt;

    @Schema(description = "Persoenliche Notizen zum Eintrag", example = "Kernposition - langfristiges Investment")
    private String notes;

    @Schema(description = "Benachrichtigung bei neuen Reports aktiviert", example = "true")
    private boolean alertOnNewReport;

    public WatchlistEntryDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    public String getSecurityTicker() {
        return securityTicker;
    }

    public void setSecurityTicker(String securityTicker) {
        this.securityTicker = securityTicker;
    }

    public String getSecurityName() {
        return securityName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isAlertOnNewReport() {
        return alertOnNewReport;
    }

    public void setAlertOnNewReport(boolean alertOnNewReport) {
        this.alertOnNewReport = alertOnNewReport;
    }
}
