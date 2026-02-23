package com.research.portal.config;

import com.research.portal.adapter.in.web.dto.ApiMetricsDto;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Thread-sicherer Collector fuer API-Nutzungsmetriken.
 *
 * <p>Sammelt Anfragezaehler, Fehlerzaehler, Antwortzeiten und Aufschluesselungen
 * nach Endpunkt und HTTP-Statuscode. Alle Operationen sind lock-free und
 * fuer hohe Parallelitaet optimiert (AtomicLong + ConcurrentHashMap).
 *
 * <p>Wird vom {@link RequestLoggingInterceptor} nach jeder HTTP-Anfrage befuellt
 * und vom MetricsController als DTO ausgeliefert.
 *
 * <p>Wird als Bean in {@link WebMvcInterceptorConfig} registriert
 * (nicht als @Component, um WebMvcTest-Slices nicht zu beeinflussen).
 */
public class ApiMetricsCollector {

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final AtomicLong totalDurationMs = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> requestsByEndpoint = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, AtomicLong> requestsByStatus = new ConcurrentHashMap<>();

    /**
     * Zeichnet eine einzelne HTTP-Anfrage auf.
     *
     * @param method     HTTP-Methode (GET, POST, PUT, DELETE etc.)
     * @param uri        Request-URI (z.B. /api/reports)
     * @param status     HTTP-Statuscode der Response (z.B. 200, 404, 500)
     * @param durationMs Verarbeitungsdauer in Millisekunden
     */
    public void recordRequest(String method, String uri, int status, long durationMs) {
        totalRequests.incrementAndGet();
        totalDurationMs.addAndGet(durationMs);

        // 4xx und 5xx als Fehler zaehlen
        if (status >= 400) {
            totalErrors.incrementAndGet();
        }

        // Endpunkt-Zaehler: "GET /api/reports"
        String endpointKey = method + " " + uri;
        requestsByEndpoint
                .computeIfAbsent(endpointKey, k -> new AtomicLong(0))
                .incrementAndGet();

        // Status-Zaehler: 200 -> count
        requestsByStatus
                .computeIfAbsent(status, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    /**
     * Liefert eine Momentaufnahme aller gesammelten Metriken als DTO.
     *
     * @return aktuelle API-Metriken
     */
    public ApiMetricsDto getMetrics() {
        long requests = totalRequests.get();
        long errors = totalErrors.get();
        long duration = totalDurationMs.get();

        double avgResponseTime = requests > 0 ? (double) duration / requests : 0.0;

        Map<String, Long> endpointMap = requestsByEndpoint.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        Map<Integer, Long> statusMap = requestsByStatus.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        String uptime = formatUptime(ManagementFactory.getRuntimeMXBean().getUptime());

        return new ApiMetricsDto(requests, errors, avgResponseTime, endpointMap, statusMap, uptime);
    }

    /**
     * Setzt alle Metriken auf Null zurueck. Primaer fuer Tests gedacht.
     */
    public void reset() {
        totalRequests.set(0);
        totalErrors.set(0);
        totalDurationMs.set(0);
        requestsByEndpoint.clear();
        requestsByStatus.clear();
    }

    /**
     * Formatiert eine Uptime in Millisekunden als lesbaren String.
     *
     * @param uptimeMs Uptime in Millisekunden
     * @return formatierte Uptime, z.B. "2h 15m 30s"
     */
    String formatUptime(long uptimeMs) {
        long totalSeconds = uptimeMs / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            sb.append(minutes).append("m ");
        }
        sb.append(seconds).append("s");
        return sb.toString();
    }
}
