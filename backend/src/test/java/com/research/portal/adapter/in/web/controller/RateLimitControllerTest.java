package com.research.portal.adapter.in.web.controller;

import com.research.portal.application.service.RateLimitService;
import com.research.portal.application.service.RateLimitService.RateLimitInfo;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests fuer {@link RateLimitController}.
 * Testet HTTP-Endpunkte mit MockMvc.
 */
@WebMvcTest(RateLimitController.class)
@DisplayName("RateLimitController")
class RateLimitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateLimitService rateLimitService;

    @Nested
    @DisplayName("GET /api/rate-limit/status")
    class GetStatus {

        @Test
        @DisplayName("Gibt 200 mit Rate-Limit-Status zurueck")
        void shouldReturn200WithStatus() throws Exception {
            RateLimitInfo info = new RateLimitInfo(95, 100, System.currentTimeMillis() + 60000);
            when(rateLimitService.getRateLimitInfo(anyString(), eq(RateLimitService.CATEGORY_READ)))
                    .thenReturn(info);

            mockMvc.perform(get("/api/rate-limit/status"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Response enthaelt clientIp und category")
        void shouldContainClientIpAndCategory() throws Exception {
            RateLimitInfo info = new RateLimitInfo(95, 100, System.currentTimeMillis() + 60000);
            when(rateLimitService.getRateLimitInfo(anyString(), eq(RateLimitService.CATEGORY_READ)))
                    .thenReturn(info);

            mockMvc.perform(get("/api/rate-limit/status"))
                    .andExpect(jsonPath("$.clientIp").isNotEmpty())
                    .andExpect(jsonPath("$.category").value("READ"));
        }

        @Test
        @DisplayName("Response enthaelt remaining und limit")
        void shouldContainRemainingAndLimit() throws Exception {
            RateLimitInfo info = new RateLimitInfo(87, 100, System.currentTimeMillis() + 60000);
            when(rateLimitService.getRateLimitInfo(anyString(), eq(RateLimitService.CATEGORY_READ)))
                    .thenReturn(info);

            mockMvc.perform(get("/api/rate-limit/status"))
                    .andExpect(jsonPath("$.remaining").value(87))
                    .andExpect(jsonPath("$.limit").value(100));
        }

        @Test
        @DisplayName("isLimited ist true wenn remaining 0 ist")
        void shouldBeIsLimitedWhenRemainingIsZero() throws Exception {
            RateLimitInfo info = new RateLimitInfo(0, 100, System.currentTimeMillis() + 60000);
            when(rateLimitService.getRateLimitInfo(anyString(), eq(RateLimitService.CATEGORY_READ)))
                    .thenReturn(info);

            mockMvc.perform(get("/api/rate-limit/status"))
                    .andExpect(jsonPath("$.limited").value(true));
        }
    }

    @Nested
    @DisplayName("DELETE /api/rate-limit/reset")
    class ResetLimits {

        @Test
        @DisplayName("Gibt 200 mit Bestaetigungsnachricht zurueck")
        void shouldReturn200WithMessage() throws Exception {
            mockMvc.perform(delete("/api/rate-limit/reset"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Alle Rate-Limits zurueckgesetzt"));
        }

        @Test
        @DisplayName("Ruft resetAll() auf dem Service auf")
        void shouldCallResetAllOnService() throws Exception {
            mockMvc.perform(delete("/api/rate-limit/reset"));

            verify(rateLimitService).resetAll();
        }
    }

    @Nested
    @DisplayName("GET /api/rate-limit/stats")
    class GetStats {

        @Test
        @DisplayName("Gibt 200 mit Statistiken zurueck")
        void shouldReturn200WithStats() throws Exception {
            Map<String, Object> stats = Map.of(
                    "totalBlocked", 42L,
                    "activeClients", 5,
                    "blockedByCategory", Map.of("READ", 10, "WRITE", 25, "SEARCH", 7)
            );
            when(rateLimitService.getStats()).thenReturn(stats);

            mockMvc.perform(get("/api/rate-limit/stats"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Response enthaelt totalBlocked und activeClients")
        void shouldContainTotalBlockedAndActiveClients() throws Exception {
            Map<String, Object> stats = Map.of(
                    "totalBlocked", 42L,
                    "activeClients", 5,
                    "blockedByCategory", Map.of("READ", 10)
            );
            when(rateLimitService.getStats()).thenReturn(stats);

            mockMvc.perform(get("/api/rate-limit/stats"))
                    .andExpect(jsonPath("$.totalBlocked").value(42))
                    .andExpect(jsonPath("$.activeClients").value(5));
        }

        @Test
        @DisplayName("Response enthaelt blockedByCategory als Map")
        void shouldContainBlockedByCategoryMap() throws Exception {
            Map<String, Object> stats = Map.of(
                    "totalBlocked", 42L,
                    "activeClients", 5,
                    "blockedByCategory", Map.of("READ", 10, "SEARCH", 7)
            );
            when(rateLimitService.getStats()).thenReturn(stats);

            mockMvc.perform(get("/api/rate-limit/stats"))
                    .andExpect(jsonPath("$.blockedByCategory").isMap())
                    .andExpect(jsonPath("$.blockedByCategory.READ").value(10))
                    .andExpect(jsonPath("$.blockedByCategory.SEARCH").value(7));
        }
    }
}
