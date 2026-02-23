package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.GlobalExceptionHandler;
import com.research.portal.adapter.out.persistence.repository.JpaAnalystRepository;
import com.research.portal.adapter.out.persistence.repository.JpaReportRepository;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests fuer {@link InfoController}.
 * Prueft App-Info und Statistik-Endpoints mit MockMvc.
 */
@WebMvcTest(InfoController.class)
@Import(GlobalExceptionHandler.class)
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JpaReportRepository reportRepository;

    @MockitoBean
    private JpaAnalystRepository analystRepository;

    @MockitoBean
    private JpaSecurityRepository securityRepository;

    @Nested
    @DisplayName("GET /api/info")
    class GetAppInfo {

        @Test
        @DisplayName("Gibt 200 mit App-Informationen zurueck")
        void shouldReturn200WithAppInfo() throws Exception {
            mockMvc.perform(get("/api/info"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Response enthaelt App-Name")
        void shouldContainAppName() throws Exception {
            mockMvc.perform(get("/api/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Research Portal API"));
        }

        @Test
        @DisplayName("Response enthaelt Version")
        void shouldContainVersion() throws Exception {
            mockMvc.perform(get("/api/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.version").value("1.0.0"));
        }

        @Test
        @DisplayName("Status ist UP")
        void shouldContainStatusUp() throws Exception {
            mockMvc.perform(get("/api/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"));
        }

        @Test
        @DisplayName("Response enthaelt Uptime in Sekunden")
        void shouldContainUptimeSeconds() throws Exception {
            mockMvc.perform(get("/api/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.uptimeSeconds", greaterThanOrEqualTo(0)));
        }

        @Test
        @DisplayName("Response enthaelt Java-Version")
        void shouldContainJavaVersion() throws Exception {
            mockMvc.perform(get("/api/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.javaVersion", notNullValue()));
        }

        @Test
        @DisplayName("Response enthaelt Beschreibung")
        void shouldContainDescription() throws Exception {
            mockMvc.perform(get("/api/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.description", notNullValue()));
        }
    }

    @Nested
    @DisplayName("GET /api/info/stats")
    class GetStats {

        @Test
        @DisplayName("Gibt 200 mit Statistiken zurueck")
        void shouldReturn200WithStats() throws Exception {
            when(reportRepository.count()).thenReturn(15L);
            when(analystRepository.count()).thenReturn(5L);
            when(securityRepository.count()).thenReturn(10L);

            mockMvc.perform(get("/api/info/stats"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Response enthaelt korrekten reportCount")
        void shouldContainCorrectReportCount() throws Exception {
            when(reportRepository.count()).thenReturn(42L);
            when(analystRepository.count()).thenReturn(5L);
            when(securityRepository.count()).thenReturn(10L);

            mockMvc.perform(get("/api/info/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reportCount").value(42));
        }

        @Test
        @DisplayName("Response enthaelt korrekten analystCount")
        void shouldContainCorrectAnalystCount() throws Exception {
            when(reportRepository.count()).thenReturn(15L);
            when(analystRepository.count()).thenReturn(7L);
            when(securityRepository.count()).thenReturn(10L);

            mockMvc.perform(get("/api/info/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.analystCount").value(7));
        }

        @Test
        @DisplayName("Response enthaelt korrekten securityCount")
        void shouldContainCorrectSecurityCount() throws Exception {
            when(reportRepository.count()).thenReturn(15L);
            when(analystRepository.count()).thenReturn(5L);
            when(securityRepository.count()).thenReturn(8L);

            mockMvc.perform(get("/api/info/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.securityCount").value(8));
        }

        @Test
        @DisplayName("Statistiken enthalten alle drei Zaehler gleichzeitig korrekt")
        void shouldContainAllCountsCorrectly() throws Exception {
            when(reportRepository.count()).thenReturn(100L);
            when(analystRepository.count()).thenReturn(12L);
            when(securityRepository.count()).thenReturn(25L);

            mockMvc.perform(get("/api/info/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reportCount").value(100))
                    .andExpect(jsonPath("$.analystCount").value(12))
                    .andExpect(jsonPath("$.securityCount").value(25));
        }

        @Test
        @DisplayName("Statistiken mit Nullwerten ergeben 0")
        void shouldReturnZeroCountsWhenEmpty() throws Exception {
            when(reportRepository.count()).thenReturn(0L);
            when(analystRepository.count()).thenReturn(0L);
            when(securityRepository.count()).thenReturn(0L);

            mockMvc.perform(get("/api/info/stats"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reportCount").value(0))
                    .andExpect(jsonPath("$.analystCount").value(0))
                    .andExpect(jsonPath("$.securityCount").value(0));
        }
    }
}
