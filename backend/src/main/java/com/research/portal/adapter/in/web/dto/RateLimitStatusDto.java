package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO fuer den aktuellen Rate-Limit-Status eines Clients.
 * Wird von GET /api/rate-limit/status zurueckgegeben.
 */
@Schema(description = "Aktueller Rate-Limit-Status eines Clients")
public class RateLimitStatusDto {

    @Schema(description = "IP-Adresse des Clients", example = "192.168.1.100")
    private final String clientIp;

    @Schema(description = "Endpoint-Kategorie (READ, WRITE, SEARCH)", example = "READ")
    private final String category;

    @Schema(description = "Verbleibende erlaubte Requests im aktuellen Zeitfenster", example = "87")
    private final int remaining;

    @Schema(description = "Maximale Anzahl Requests pro Zeitfenster", example = "100")
    private final int limit;

    @Schema(description = "Zeitpunkt des Reset (Epoch-Millisekunden)", example = "1740312345000")
    private final long resetAt;

    @Schema(description = "Ob der Client aktuell limitiert ist", example = "false")
    private final boolean isLimited;

    /**
     * Erstellt ein neues RateLimitStatusDto.
     *
     * @param clientIp  die IP-Adresse des Clients
     * @param category  die Endpoint-Kategorie
     * @param remaining verbleibende erlaubte Requests
     * @param limit     maximale Requests pro Zeitfenster
     * @param resetAt   Reset-Zeitpunkt in Epoch-Millisekunden
     * @param isLimited ob der Client limitiert ist
     */
    public RateLimitStatusDto(String clientIp, String category, int remaining,
                               int limit, long resetAt, boolean isLimited) {
        this.clientIp = clientIp;
        this.category = category;
        this.remaining = remaining;
        this.limit = limit;
        this.resetAt = resetAt;
        this.isLimited = isLimited;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getCategory() {
        return category;
    }

    public int getRemaining() {
        return remaining;
    }

    public int getLimit() {
        return limit;
    }

    public long getResetAt() {
        return resetAt;
    }

    public boolean isLimited() {
        return isLimited;
    }
}
