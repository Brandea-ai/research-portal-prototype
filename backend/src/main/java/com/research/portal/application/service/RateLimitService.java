package com.research.portal.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service fuer API Rate Limiting mit Sliding-Window-Algorithmus.
 *
 * <p>Verwaltet Request-Limits pro Client-IP und Endpoint-Kategorie.
 * Jede Kategorie hat ein eigenes Limit:
 * <ul>
 *   <li>READ (GET-Requests): 100 Requests pro Minute</li>
 *   <li>WRITE (POST/PUT/DELETE-Requests): 30 Requests pro Minute</li>
 *   <li>SEARCH (/api/search): 20 Requests pro Minute</li>
 * </ul>
 *
 * <p>Thread-sicher durch Verwendung von {@link ConcurrentHashMap}
 * und {@link ConcurrentLinkedDeque}.
 *
 * <p>Wird als Bean in {@link com.research.portal.config.RateLimitConfig} registriert
 * (nicht als @Component, um WebMvcTest-Slices nicht zu beeinflussen).
 */
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    /** Zeitfenster fuer das Sliding Window in Millisekunden (1 Minute). */
    static final long WINDOW_MS = 60_000;

    /** Limit fuer READ-Endpoints (GET-Requests). */
    static final int READ_LIMIT = 100;

    /** Limit fuer WRITE-Endpoints (POST/PUT/DELETE-Requests). */
    static final int WRITE_LIMIT = 30;

    /** Limit fuer SEARCH-Endpoints (/api/search). */
    static final int SEARCH_LIMIT = 20;

    /** Kategorie fuer lesende Zugriffe. */
    public static final String CATEGORY_READ = "READ";

    /** Kategorie fuer schreibende Zugriffe. */
    public static final String CATEGORY_WRITE = "WRITE";

    /** Kategorie fuer Such-Zugriffe. */
    public static final String CATEGORY_SEARCH = "SEARCH";

    /**
     * Speichert Timestamps der Requests pro Client-IP und Kategorie.
     * Key-Format: "clientIp:category"
     */
    private final ConcurrentHashMap<String, Deque<Long>> requestTimestamps = new ConcurrentHashMap<>();

    /** Zaehler fuer blockierte Requests pro Kategorie. */
    private final ConcurrentHashMap<String, AtomicLong> blockedCountByCategory = new ConcurrentHashMap<>();

    /** Gesamtanzahl blockierter Requests. */
    private final AtomicLong totalBlocked = new AtomicLong(0);

    /**
     * Prueft ob ein Request fuer die gegebene Client-IP und Kategorie erlaubt ist.
     *
     * <p>Entfernt abgelaufene Timestamps aus dem Sliding Window und prueft,
     * ob das Limit fuer die Kategorie bereits erreicht ist.
     * Bei Erlaubnis wird der aktuelle Timestamp hinzugefuegt.
     *
     * @param clientIp die IP-Adresse des Clients
     * @param category die Endpoint-Kategorie (READ, WRITE, SEARCH)
     * @return true wenn der Request erlaubt ist, false wenn das Limit erreicht ist
     */
    public boolean isAllowed(String clientIp, String category) {
        String key = buildKey(clientIp, category);
        int limit = getLimitForCategory(category);
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_MS;

        Deque<Long> timestamps = requestTimestamps.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        // Abgelaufene Eintraege entfernen
        while (!timestamps.isEmpty() && timestamps.peekFirst() != null && timestamps.peekFirst() < windowStart) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= limit) {
            totalBlocked.incrementAndGet();
            blockedCountByCategory
                    .computeIfAbsent(category, k -> new AtomicLong(0))
                    .incrementAndGet();
            log.warn("Rate limit ueberschritten fuer IP={} Kategorie={} (Limit={})", clientIp, category, limit);
            return false;
        }

        timestamps.addLast(now);
        return true;
    }

    /**
     * Gibt die aktuellen Rate-Limit-Informationen fuer eine Client-IP und Kategorie zurueck.
     *
     * @param clientIp die IP-Adresse des Clients
     * @param category die Endpoint-Kategorie (READ, WRITE, SEARCH)
     * @return {@link RateLimitInfo} mit verbleibenden Requests, Limit und Reset-Zeitpunkt
     */
    public RateLimitInfo getRateLimitInfo(String clientIp, String category) {
        String key = buildKey(clientIp, category);
        int limit = getLimitForCategory(category);
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_MS;

        Deque<Long> timestamps = requestTimestamps.get(key);
        if (timestamps == null || timestamps.isEmpty()) {
            return new RateLimitInfo(limit, limit, now + WINDOW_MS);
        }

        // Abgelaufene Eintraege zaehlen nicht
        long activeCount = timestamps.stream()
                .filter(ts -> ts >= windowStart)
                .count();

        int remaining = Math.max(0, limit - (int) activeCount);

        // Reset-Zeit ist der Zeitpunkt, an dem der aelteste aktive Eintrag ablaeuft
        long resetAt = timestamps.stream()
                .filter(ts -> ts >= windowStart)
                .findFirst()
                .map(ts -> ts + WINDOW_MS)
                .orElse(now + WINDOW_MS);

        return new RateLimitInfo(remaining, limit, resetAt);
    }

    /**
     * Setzt alle Rate-Limits zurueck.
     * Admin-Operation zum Freigeben aller blockierten Clients.
     */
    public void resetAll() {
        requestTimestamps.clear();
        log.info("Alle Rate-Limits zurueckgesetzt");
    }

    /**
     * Gibt Statistiken ueber das Rate-Limiting zurueck.
     *
     * @return Map mit Statistik-Werten (totalBlocked, activeClients, blockedByCategory)
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBlocked", totalBlocked.get());
        stats.put("activeClients", countActiveClients());

        Map<String, Integer> categoryStats = new HashMap<>();
        blockedCountByCategory.forEach((category, count) ->
                categoryStats.put(category, (int) count.get()));
        stats.put("blockedByCategory", categoryStats);

        return stats;
    }

    /**
     * Entfernt abgelaufene Eintraege aus dem Sliding Window.
     * Laeuft alle 5 Minuten automatisch per @Scheduled.
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cleanup() {
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_MS;
        int removedEntries = 0;

        var iterator = requestTimestamps.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            Deque<Long> timestamps = entry.getValue();

            while (!timestamps.isEmpty() && timestamps.peekFirst() != null && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
                removedEntries++;
            }

            if (timestamps.isEmpty()) {
                iterator.remove();
            }
        }

        if (removedEntries > 0) {
            log.debug("Rate-Limit Cleanup: {} abgelaufene Eintraege entfernt", removedEntries);
        }
    }

    /**
     * Gibt das Limit fuer die angegebene Kategorie zurueck.
     *
     * @param category die Endpoint-Kategorie
     * @return das konfigurierte Limit
     */
    int getLimitForCategory(String category) {
        return switch (category) {
            case CATEGORY_WRITE -> WRITE_LIMIT;
            case CATEGORY_SEARCH -> SEARCH_LIMIT;
            default -> READ_LIMIT;
        };
    }

    /**
     * Bestimmt die Kategorie basierend auf HTTP-Methode und Request-URI.
     *
     * @param method die HTTP-Methode (GET, POST, PUT, DELETE)
     * @param uri    die Request-URI
     * @return die ermittelte Kategorie (READ, WRITE oder SEARCH)
     */
    public String determineCategory(String method, String uri) {
        if (uri != null && uri.startsWith("/api/search")) {
            return CATEGORY_SEARCH;
        }
        if ("GET".equalsIgnoreCase(method)) {
            return CATEGORY_READ;
        }
        return CATEGORY_WRITE;
    }

    private String buildKey(String clientIp, String category) {
        return clientIp + ":" + category;
    }

    private int countActiveClients() {
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_MS;

        return (int) requestTimestamps.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(ts -> ts >= windowStart))
                .map(entry -> entry.getKey().split(":")[0])
                .distinct()
                .count();
    }

    /**
     * Value Object fuer Rate-Limit-Informationen.
     *
     * @param remaining    verbleibende erlaubte Requests im aktuellen Zeitfenster
     * @param limit        maximale Anzahl Requests pro Zeitfenster
     * @param resetAtEpochMs Zeitpunkt (Epoch-Millisekunden) an dem das Zeitfenster zurueckgesetzt wird
     */
    public record RateLimitInfo(int remaining, int limit, long resetAtEpochMs) {
    }
}
