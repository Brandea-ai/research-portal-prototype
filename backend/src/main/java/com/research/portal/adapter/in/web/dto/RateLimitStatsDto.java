package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * DTO fuer Rate-Limiting-Statistiken.
 * Wird von GET /api/rate-limit/stats zurueckgegeben.
 */
@Schema(description = "Statistiken ueber das API Rate Limiting")
public class RateLimitStatsDto {

    @Schema(description = "Gesamtanzahl blockierter Requests", example = "42")
    private final long totalBlocked;

    @Schema(description = "Anzahl aktuell aktiver Clients", example = "5")
    private final int activeClients;

    @Schema(description = "Anzahl blockierter Requests pro Kategorie",
            example = "{\"READ\": 10, \"WRITE\": 25, \"SEARCH\": 7}")
    private final Map<String, Integer> blockedByCategory;

    /**
     * Erstellt ein neues RateLimitStatsDto.
     *
     * @param totalBlocked     Gesamtanzahl blockierter Requests
     * @param activeClients    Anzahl aktuell aktiver Clients
     * @param blockedByCategory Blockierungen pro Kategorie
     */
    public RateLimitStatsDto(long totalBlocked, int activeClients,
                              Map<String, Integer> blockedByCategory) {
        this.totalBlocked = totalBlocked;
        this.activeClients = activeClients;
        this.blockedByCategory = blockedByCategory;
    }

    public long getTotalBlocked() {
        return totalBlocked;
    }

    public int getActiveClients() {
        return activeClients;
    }

    public Map<String, Integer> getBlockedByCategory() {
        return blockedByCategory;
    }
}
