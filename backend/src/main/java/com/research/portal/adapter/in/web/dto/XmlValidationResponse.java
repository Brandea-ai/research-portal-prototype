package com.research.portal.adapter.in.web.dto;

import java.time.LocalDateTime;

/**
 * Antwort-DTO für die XML-Validierung.
 * Zeigt ob ein XML-Dokument dem Schema entspricht.
 */
public class XmlValidationResponse {

    private boolean valid;
    private String message;
    private LocalDateTime timestamp;

    public XmlValidationResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public XmlValidationResponse(boolean valid, String message) {
        this();
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
