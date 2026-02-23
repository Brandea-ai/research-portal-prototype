package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request-DTO zum Hinzufuegen einer Wertschrift zur Watchlist.
 *
 * Enthaelt die erforderliche Wertschrift-ID sowie optionale
 * Notizen und Alert-Einstellungen.
 */
@Schema(description = "Anfrage zum Hinzufuegen einer Wertschrift zur Watchlist")
public class AddToWatchlistRequest {

    @NotNull(message = "securityId ist erforderlich")
    @Schema(description = "ID der zu beobachtenden Wertschrift", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long securityId;

    @Size(max = 500, message = "Notizen duerfen maximal 500 Zeichen lang sein")
    @Schema(description = "Persoenliche Notizen zum Eintrag",
            example = "Kernposition - langfristiges Investment")
    private String notes;

    @Schema(description = "Benachrichtigung bei neuen Reports aktivieren", example = "true",
            defaultValue = "true")
    private boolean alertOnNewReport = true;

    public AddToWatchlistRequest() {
    }

    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
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
