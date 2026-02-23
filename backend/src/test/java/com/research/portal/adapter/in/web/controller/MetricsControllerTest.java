package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.ApiMetricsDto;
import com.research.portal.config.ApiMetricsCollector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests fuer {@link MetricsController}.
 * Testet HTTP-Endpunkte mit MockMvc.
 */
@WebMvcTest(MetricsController.class)
@DisplayName("MetricsController")
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApiMetricsCollector metricsCollector;

    private ApiMetricsDto createSampleMetrics() {
        return new ApiMetricsDto(
                150,
                5,
                42.5,
                Map.of(
                        "GET /api/reports", 100L,
                        "POST /api/reports", 30L,
                        "GET /api/analysts", 20L
                ),
                Map.of(
                        200, 140L,
                        201, 5L,
                        404, 3L,
                        500, 2L
                ),
                "1h 30m 15s"
        );
    }

    @Nested
    @DisplayName("GET /api/metrics")
    class GetMetrics {

        @Test
        @DisplayName("Gibt 200 mit Metriken zurueck")
        void shouldReturn200WithMetrics() throws Exception {
            when(metricsCollector.getMetrics()).thenReturn(createSampleMetrics());

            mockMvc.perform(get("/api/metrics"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Response enthaelt totalRequests")
        void shouldContainTotalRequests() throws Exception {
            when(metricsCollector.getMetrics()).thenReturn(createSampleMetrics());

            mockMvc.perform(get("/api/metrics"))
                    .andExpect(jsonPath("$.totalRequests").value(150));
        }

        @Test
        @DisplayName("Response enthaelt totalErrors")
        void shouldContainTotalErrors() throws Exception {
            when(metricsCollector.getMetrics()).thenReturn(createSampleMetrics());

            mockMvc.perform(get("/api/metrics"))
                    .andExpect(jsonPath("$.totalErrors").value(5));
        }

        @Test
        @DisplayName("Response enthaelt avgResponseTimeMs")
        void shouldContainAvgResponseTime() throws Exception {
            when(metricsCollector.getMetrics()).thenReturn(createSampleMetrics());

            mockMvc.perform(get("/api/metrics"))
                    .andExpect(jsonPath("$.avgResponseTimeMs").value(42.5));
        }

        @Test
        @DisplayName("Response enthaelt requestsByEndpoint als Map")
        void shouldContainRequestsByEndpoint() throws Exception {
            when(metricsCollector.getMetrics()).thenReturn(createSampleMetrics());

            mockMvc.perform(get("/api/metrics"))
                    .andExpect(jsonPath("$.requestsByEndpoint").isMap())
                    .andExpect(jsonPath("$.requestsByEndpoint['GET /api/reports']").value(100))
                    .andExpect(jsonPath("$.requestsByEndpoint['POST /api/reports']").value(30));
        }

        @Test
        @DisplayName("Response enthaelt requestsByStatus als Map")
        void shouldContainRequestsByStatus() throws Exception {
            when(metricsCollector.getMetrics()).thenReturn(createSampleMetrics());

            mockMvc.perform(get("/api/metrics"))
                    .andExpect(jsonPath("$.requestsByStatus").isMap())
                    .andExpect(jsonPath("$.requestsByStatus['200']").value(140))
                    .andExpect(jsonPath("$.requestsByStatus['404']").value(3));
        }

        @Test
        @DisplayName("Response enthaelt Uptime-String")
        void shouldContainUptime() throws Exception {
            when(metricsCollector.getMetrics()).thenReturn(createSampleMetrics());

            mockMvc.perform(get("/api/metrics"))
                    .andExpect(jsonPath("$.uptime").value("1h 30m 15s"));
        }

        @Test
        @DisplayName("Gibt leere Metriken zurueck wenn noch keine Requests erfasst wurden")
        void shouldReturnEmptyMetricsWhenNoRequests() throws Exception {
            ApiMetricsDto emptyMetrics = new ApiMetricsDto(
                    0, 0, 0.0, Map.of(), Map.of(), "0s"
            );
            when(metricsCollector.getMetrics()).thenReturn(emptyMetrics);

            mockMvc.perform(get("/api/metrics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalRequests").value(0))
                    .andExpect(jsonPath("$.totalErrors").value(0))
                    .andExpect(jsonPath("$.avgResponseTimeMs").value(0.0))
                    .andExpect(jsonPath("$.requestsByEndpoint").isEmpty())
                    .andExpect(jsonPath("$.requestsByStatus").isEmpty());
        }
    }

    @Nested
    @DisplayName("DELETE /api/metrics")
    class ResetMetrics {

        @Test
        @DisplayName("Gibt 204 zurueck")
        void shouldReturn204() throws Exception {
            mockMvc.perform(delete("/api/metrics"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Ruft reset() auf dem Collector auf")
        void shouldCallResetOnCollector() throws Exception {
            mockMvc.perform(delete("/api/metrics"));

            verify(metricsCollector).reset();
        }
    }
}
