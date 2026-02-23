package com.research.portal.config;

import com.research.portal.adapter.in.web.dto.ApiMetricsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Unit-Tests fuer {@link ApiMetricsCollector}.
 *
 * <p>Prueft die korrekte Aggregation von API-Metriken, inklusive
 * Zaehler, Durchschnittswerte, Aufschluesselungen und Thread-Safety.
 */
@DisplayName("ApiMetricsCollector")
class ApiMetricsCollectorTest {

    private ApiMetricsCollector collector;

    @BeforeEach
    void setUp() {
        collector = new ApiMetricsCollector();
    }

    @Nested
    @DisplayName("recordRequest")
    class RecordRequest {

        @Test
        @DisplayName("Inkrementiert totalRequests bei jedem Aufruf")
        void shouldIncrementTotalRequests() {
            collector.recordRequest("GET", "/api/reports", 200, 10);
            collector.recordRequest("POST", "/api/reports", 201, 20);
            collector.recordRequest("GET", "/api/analysts", 200, 5);

            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getTotalRequests()).isEqualTo(3);
        }

        @Test
        @DisplayName("Zaehlt 4xx-Fehler als Errors")
        void shouldCount4xxAsErrors() {
            collector.recordRequest("GET", "/api/reports/999", 404, 5);
            collector.recordRequest("POST", "/api/reports", 400, 8);

            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getTotalErrors()).isEqualTo(2);
        }

        @Test
        @DisplayName("Zaehlt 5xx-Fehler als Errors")
        void shouldCount5xxAsErrors() {
            collector.recordRequest("GET", "/api/reports", 500, 100);
            collector.recordRequest("GET", "/api/reports", 503, 50);

            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getTotalErrors()).isEqualTo(2);
        }

        @Test
        @DisplayName("Zaehlt 2xx und 3xx nicht als Errors")
        void shouldNotCountSuccessAsErrors() {
            collector.recordRequest("GET", "/api/reports", 200, 10);
            collector.recordRequest("GET", "/api/reports", 201, 10);
            collector.recordRequest("GET", "/api/reports", 301, 10);

            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getTotalErrors()).isZero();
        }

        @Test
        @DisplayName("Befuellt requestsByEndpoint Map korrekt")
        void shouldPopulateRequestsByEndpoint() {
            collector.recordRequest("GET", "/api/reports", 200, 10);
            collector.recordRequest("GET", "/api/reports", 200, 15);
            collector.recordRequest("POST", "/api/reports", 201, 30);
            collector.recordRequest("GET", "/api/analysts", 200, 5);

            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getRequestsByEndpoint())
                    .containsEntry("GET /api/reports", 2L)
                    .containsEntry("POST /api/reports", 1L)
                    .containsEntry("GET /api/analysts", 1L)
                    .hasSize(3);
        }

        @Test
        @DisplayName("Befuellt requestsByStatus Map korrekt")
        void shouldPopulateRequestsByStatus() {
            collector.recordRequest("GET", "/api/reports", 200, 10);
            collector.recordRequest("GET", "/api/reports", 200, 15);
            collector.recordRequest("GET", "/api/reports/999", 404, 5);
            collector.recordRequest("POST", "/api/reports", 201, 20);

            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getRequestsByStatus())
                    .containsEntry(200, 2L)
                    .containsEntry(404, 1L)
                    .containsEntry(201, 1L)
                    .hasSize(3);
        }

        @Test
        @DisplayName("Berechnet avgResponseTimeMs korrekt")
        void shouldCalculateAvgResponseTime() {
            collector.recordRequest("GET", "/api/reports", 200, 10);
            collector.recordRequest("GET", "/api/reports", 200, 20);
            collector.recordRequest("GET", "/api/reports", 200, 30);

            ApiMetricsDto metrics = collector.getMetrics();
            // Durchschnitt: (10 + 20 + 30) / 3 = 20.0
            assertThat(metrics.getAvgResponseTimeMs()).isCloseTo(20.0, within(0.01));
        }

        @Test
        @DisplayName("Gibt avgResponseTimeMs 0.0 zurueck wenn keine Requests vorhanden")
        void shouldReturnZeroAvgWhenNoRequests() {
            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getAvgResponseTimeMs()).isZero();
        }
    }

    @Nested
    @DisplayName("reset")
    class Reset {

        @Test
        @DisplayName("Setzt alle Werte auf Null zurueck")
        void shouldResetAllValues() {
            collector.recordRequest("GET", "/api/reports", 200, 10);
            collector.recordRequest("GET", "/api/reports/999", 404, 5);
            collector.recordRequest("POST", "/api/reports", 500, 100);

            collector.reset();

            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getTotalRequests()).isZero();
            assertThat(metrics.getTotalErrors()).isZero();
            assertThat(metrics.getAvgResponseTimeMs()).isZero();
            assertThat(metrics.getRequestsByEndpoint()).isEmpty();
            assertThat(metrics.getRequestsByStatus()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Thread-Safety")
    class ThreadSafety {

        @Test
        @DisplayName("Zaehlt korrekt bei parallelen Aufrufen")
        void shouldCountCorrectlyUnderConcurrency() throws InterruptedException {
            int threadCount = 10;
            int requestsPerThread = 100;
            int totalExpected = threadCount * requestsPerThread;

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            for (int t = 0; t < threadCount; t++) {
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        for (int i = 0; i < requestsPerThread; i++) {
                            collector.recordRequest("GET", "/api/reports", 200, 5);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            // Alle Threads gleichzeitig starten
            startLatch.countDown();
            boolean completed = doneLatch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            assertThat(completed).isTrue();

            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getTotalRequests()).isEqualTo(totalExpected);
            assertThat(metrics.getRequestsByEndpoint())
                    .containsEntry("GET /api/reports", (long) totalExpected);
            assertThat(metrics.getRequestsByStatus())
                    .containsEntry(200, (long) totalExpected);
        }
    }

    @Nested
    @DisplayName("formatUptime")
    class FormatUptime {

        @Test
        @DisplayName("Formatiert Sekunden ohne Stunden und Minuten")
        void shouldFormatSecondsOnly() {
            String result = collector.formatUptime(45_000);
            assertThat(result).isEqualTo("45s");
        }

        @Test
        @DisplayName("Formatiert Minuten und Sekunden")
        void shouldFormatMinutesAndSeconds() {
            String result = collector.formatUptime(135_000); // 2m 15s
            assertThat(result).isEqualTo("2m 15s");
        }

        @Test
        @DisplayName("Formatiert Stunden, Minuten und Sekunden")
        void shouldFormatHoursMinutesSeconds() {
            // 2h 15m 30s = 2*3600 + 15*60 + 30 = 8130s = 8130000ms
            String result = collector.formatUptime(8_130_000);
            assertThat(result).isEqualTo("2h 15m 30s");
        }

        @Test
        @DisplayName("Formatiert 0ms als 0s")
        void shouldFormatZero() {
            String result = collector.formatUptime(0);
            assertThat(result).isEqualTo("0s");
        }
    }

    @Nested
    @DisplayName("getMetrics")
    class GetMetrics {

        @Test
        @DisplayName("Liefert Uptime-String im korrekten Format")
        void shouldContainUptime() {
            ApiMetricsDto metrics = collector.getMetrics();
            assertThat(metrics.getUptime()).isNotNull();
            assertThat(metrics.getUptime()).matches("(\\d+h )?\\d+m \\d+s|\\d+s");
        }
    }
}
