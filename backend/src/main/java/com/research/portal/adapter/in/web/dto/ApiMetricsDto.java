package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * DTO fuer API-Nutzungsmetriken und Performance-Statistiken.
 * Wird von GET /api/metrics zurueckgegeben.
 */
@Schema(description = "API-Nutzungsmetriken und Performance-Statistiken")
public class ApiMetricsDto {

    @Schema(description = "Gesamtanzahl verarbeiteter API-Anfragen", example = "1542")
    private final long totalRequests;

    @Schema(description = "Anzahl fehlerhafter Anfragen (4xx + 5xx)", example = "23")
    private final long totalErrors;

    @Schema(description = "Durchschnittliche Antwortzeit in Millisekunden", example = "42.5")
    private final double avgResponseTimeMs;

    @Schema(description = "Anzahl Anfragen pro Endpunkt (z.B. 'GET /api/reports' -> 100)")
    private final Map<String, Long> requestsByEndpoint;

    @Schema(description = "Anzahl Anfragen pro HTTP-Statuscode (z.B. 200 -> 1500)")
    private final Map<Integer, Long> requestsByStatus;

    @Schema(description = "Laufzeit der Applikation seit dem letzten Start", example = "2h 15m 30s")
    private final String uptime;

    public ApiMetricsDto(long totalRequests,
                         long totalErrors,
                         double avgResponseTimeMs,
                         Map<String, Long> requestsByEndpoint,
                         Map<Integer, Long> requestsByStatus,
                         String uptime) {
        this.totalRequests = totalRequests;
        this.totalErrors = totalErrors;
        this.avgResponseTimeMs = avgResponseTimeMs;
        this.requestsByEndpoint = requestsByEndpoint;
        this.requestsByStatus = requestsByStatus;
        this.uptime = uptime;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getTotalErrors() {
        return totalErrors;
    }

    public double getAvgResponseTimeMs() {
        return avgResponseTimeMs;
    }

    public Map<String, Long> getRequestsByEndpoint() {
        return requestsByEndpoint;
    }

    public Map<Integer, Long> getRequestsByStatus() {
        return requestsByStatus;
    }

    public String getUptime() {
        return uptime;
    }
}
