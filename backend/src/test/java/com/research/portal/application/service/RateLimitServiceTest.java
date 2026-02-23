package com.research.portal.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit-Tests fuer {@link RateLimitService}.
 *
 * <p>Testet den Sliding-Window Rate-Limiting-Algorithmus,
 * Kategorie-spezifische Limits, IP-Isolation und Admin-Funktionen.
 */
@DisplayName("RateLimitService")
class RateLimitServiceTest {

    private RateLimitService service;

    @BeforeEach
    void setUp() {
        service = new RateLimitService();
    }

    @Nested
    @DisplayName("isAllowed")
    class IsAllowed {

        @Test
        @DisplayName("Erlaubt Request wenn unter dem Limit")
        void shouldAllowRequestUnderLimit() {
            boolean result = service.isAllowed("192.168.1.1", RateLimitService.CATEGORY_READ);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Blockiert Request wenn Limit erreicht ist")
        void shouldBlockRequestWhenLimitReached() {
            String ip = "10.0.0.1";
            String category = RateLimitService.CATEGORY_SEARCH;

            // SEARCH-Limit ist 20
            for (int i = 0; i < RateLimitService.SEARCH_LIMIT; i++) {
                assertThat(service.isAllowed(ip, category)).isTrue();
            }

            // Request Nummer 21 sollte blockiert werden
            assertThat(service.isAllowed(ip, category)).isFalse();
        }

        @Test
        @DisplayName("Verschiedene Kategorien haben separate Limits")
        void shouldHaveSeparateLimitsPerCategory() {
            String ip = "10.0.0.2";

            // SEARCH-Limit (20) ausschoepfen
            for (int i = 0; i < RateLimitService.SEARCH_LIMIT; i++) {
                service.isAllowed(ip, RateLimitService.CATEGORY_SEARCH);
            }

            // READ sollte immer noch erlaubt sein
            assertThat(service.isAllowed(ip, RateLimitService.CATEGORY_READ)).isTrue();
            // WRITE sollte immer noch erlaubt sein
            assertThat(service.isAllowed(ip, RateLimitService.CATEGORY_WRITE)).isTrue();
            // SEARCH sollte blockiert sein
            assertThat(service.isAllowed(ip, RateLimitService.CATEGORY_SEARCH)).isFalse();
        }

        @Test
        @DisplayName("Verschiedene IPs haben separate Limits")
        void shouldHaveSeparateLimitsPerIp() {
            String category = RateLimitService.CATEGORY_SEARCH;

            // IP 1: Limit ausschoepfen
            for (int i = 0; i < RateLimitService.SEARCH_LIMIT; i++) {
                service.isAllowed("10.0.0.10", category);
            }

            // IP 1 blockiert
            assertThat(service.isAllowed("10.0.0.10", category)).isFalse();

            // IP 2 sollte immer noch erlaubt sein
            assertThat(service.isAllowed("10.0.0.20", category)).isTrue();
        }

        @Test
        @DisplayName("WRITE-Limit ist korrekt auf 30 gesetzt")
        void shouldEnforceWriteLimit() {
            String ip = "10.0.0.3";
            String category = RateLimitService.CATEGORY_WRITE;

            for (int i = 0; i < RateLimitService.WRITE_LIMIT; i++) {
                assertThat(service.isAllowed(ip, category)).isTrue();
            }

            assertThat(service.isAllowed(ip, category)).isFalse();
        }

        @Test
        @DisplayName("READ-Limit ist korrekt auf 100 gesetzt")
        void shouldEnforceReadLimit() {
            String ip = "10.0.0.4";
            String category = RateLimitService.CATEGORY_READ;

            for (int i = 0; i < RateLimitService.READ_LIMIT; i++) {
                assertThat(service.isAllowed(ip, category)).isTrue();
            }

            assertThat(service.isAllowed(ip, category)).isFalse();
        }
    }

    @Nested
    @DisplayName("getRateLimitInfo")
    class GetRateLimitInfo {

        @Test
        @DisplayName("Gibt volles Limit zurueck wenn keine Requests vorhanden")
        void shouldReturnFullLimitWhenNoRequests() {
            RateLimitService.RateLimitInfo info =
                    service.getRateLimitInfo("192.168.1.1", RateLimitService.CATEGORY_READ);

            assertThat(info.remaining()).isEqualTo(RateLimitService.READ_LIMIT);
            assertThat(info.limit()).isEqualTo(RateLimitService.READ_LIMIT);
            assertThat(info.resetAtEpochMs()).isGreaterThan(System.currentTimeMillis());
        }

        @Test
        @DisplayName("Reduziert verbleibende Requests nach Nutzung")
        void shouldReduceRemainingAfterUsage() {
            String ip = "192.168.1.2";
            String category = RateLimitService.CATEGORY_READ;

            service.isAllowed(ip, category);
            service.isAllowed(ip, category);
            service.isAllowed(ip, category);

            RateLimitService.RateLimitInfo info = service.getRateLimitInfo(ip, category);

            assertThat(info.remaining()).isEqualTo(RateLimitService.READ_LIMIT - 3);
        }
    }

    @Nested
    @DisplayName("resetAll")
    class ResetAll {

        @Test
        @DisplayName("Setzt alle Limits zurueck sodass blockierte Clients wieder erlaubt sind")
        void shouldResetAllLimitsAllowingBlockedClients() {
            String ip = "10.0.0.5";
            String category = RateLimitService.CATEGORY_SEARCH;

            // Limit ausschoepfen
            for (int i = 0; i < RateLimitService.SEARCH_LIMIT; i++) {
                service.isAllowed(ip, category);
            }
            assertThat(service.isAllowed(ip, category)).isFalse();

            // Reset
            service.resetAll();

            // Sollte wieder erlaubt sein
            assertThat(service.isAllowed(ip, category)).isTrue();
        }
    }

    @Nested
    @DisplayName("getStats")
    class GetStats {

        @Test
        @DisplayName("Gibt korrekte Statistiken zurueck")
        void shouldReturnCorrectStats() {
            // Einige Requests durchfuehren und einen blockieren
            String ip = "10.0.0.6";
            String category = RateLimitService.CATEGORY_SEARCH;

            for (int i = 0; i < RateLimitService.SEARCH_LIMIT; i++) {
                service.isAllowed(ip, category);
            }
            // Dieser wird blockiert
            service.isAllowed(ip, category);

            Map<String, Object> stats = service.getStats();

            assertThat(stats).containsKey("totalBlocked");
            assertThat(stats).containsKey("activeClients");
            assertThat(stats).containsKey("blockedByCategory");
            assertThat((long) stats.get("totalBlocked")).isEqualTo(1);
            assertThat((int) stats.get("activeClients")).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Zaehlt blockierte Requests pro Kategorie korrekt")
        void shouldCountBlockedByCategory() {
            // SEARCH ausschoepfen und blockieren
            for (int i = 0; i < RateLimitService.SEARCH_LIMIT; i++) {
                service.isAllowed("10.0.0.7", RateLimitService.CATEGORY_SEARCH);
            }
            service.isAllowed("10.0.0.7", RateLimitService.CATEGORY_SEARCH);
            service.isAllowed("10.0.0.7", RateLimitService.CATEGORY_SEARCH);

            Map<String, Object> stats = service.getStats();

            @SuppressWarnings("unchecked")
            Map<String, Integer> blockedByCategory =
                    (Map<String, Integer>) stats.get("blockedByCategory");

            assertThat(blockedByCategory).containsKey(RateLimitService.CATEGORY_SEARCH);
            assertThat(blockedByCategory.get(RateLimitService.CATEGORY_SEARCH)).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("determineCategory")
    class DetermineCategory {

        @Test
        @DisplayName("Gibt SEARCH zurueck fuer /api/search Pfad")
        void shouldReturnSearchForSearchPath() {
            assertThat(service.determineCategory("GET", "/api/search")).isEqualTo(RateLimitService.CATEGORY_SEARCH);
            assertThat(service.determineCategory("POST", "/api/search/query")).isEqualTo(RateLimitService.CATEGORY_SEARCH);
        }

        @Test
        @DisplayName("Gibt READ zurueck fuer GET Requests")
        void shouldReturnReadForGetRequests() {
            assertThat(service.determineCategory("GET", "/api/reports")).isEqualTo(RateLimitService.CATEGORY_READ);
        }

        @Test
        @DisplayName("Gibt WRITE zurueck fuer POST/PUT/DELETE Requests")
        void shouldReturnWriteForMutatingRequests() {
            assertThat(service.determineCategory("POST", "/api/reports")).isEqualTo(RateLimitService.CATEGORY_WRITE);
            assertThat(service.determineCategory("PUT", "/api/reports/1")).isEqualTo(RateLimitService.CATEGORY_WRITE);
            assertThat(service.determineCategory("DELETE", "/api/reports/1")).isEqualTo(RateLimitService.CATEGORY_WRITE);
        }
    }

    @Nested
    @DisplayName("cleanup")
    class Cleanup {

        @Test
        @DisplayName("Cleanup laeuft ohne Fehler auf leerer Map")
        void shouldRunWithoutErrorOnEmptyMap() {
            service.cleanup();
            // Kein Fehler erwartet
            Map<String, Object> stats = service.getStats();
            assertThat((int) stats.get("activeClients")).isZero();
        }
    }
}
