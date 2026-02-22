package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Standardisierte Fehlerantwort der API")
public class ErrorResponse {

    @Schema(description = "HTTP-Statuscode", example = "404")
    private int status;

    @Schema(description = "Fehlerkategorie", example = "Not Found")
    private String error;

    @Schema(description = "Fehlerbeschreibung", example = "Report mit ID 99 wurde nicht gefunden")
    private String message;

    @Schema(description = "Zeitpunkt des Fehlers", example = "2026-02-20T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "Detaillierte Validierungsfehler (optional)",
            example = "[\"title: Titel ist erforderlich\", \"rating: Rating ist erforderlich\"]")
    private List<String> details;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, List<String> details) {
        this(status, error, message);
        this.details = details;
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<String> getDetails() { return details; }
}
