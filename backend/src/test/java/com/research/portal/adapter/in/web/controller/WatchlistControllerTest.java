package com.research.portal.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.portal.adapter.in.web.GlobalExceptionHandler;
import com.research.portal.adapter.in.web.dto.AddToWatchlistRequest;
import com.research.portal.adapter.out.persistence.entity.SecurityEntity;
import com.research.portal.adapter.out.persistence.entity.WatchlistEntity;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import com.research.portal.application.service.WatchlistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests fuer WatchlistController.
 * Testet Watchlist-Endpoints mit MockMvc.
 */
@WebMvcTest(WatchlistController.class)
@Import(GlobalExceptionHandler.class)
class WatchlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WatchlistService watchlistService;

    @MockitoBean
    private JpaSecurityRepository securityRepository;

    /**
     * Erstellt eine Test-WatchlistEntity.
     */
    private WatchlistEntity createTestEntry(Long id, String userId, Long securityId, String notes) {
        WatchlistEntity entity = new WatchlistEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setSecurityId(securityId);
        entity.setNotes(notes);
        entity.setAlertOnNewReport(true);
        entity.setAddedAt(LocalDateTime.of(2026, 2, 20, 10, 0));
        return entity;
    }

    /**
     * Erstellt eine Test-SecurityEntity.
     */
    private SecurityEntity createTestSecurity(Long id, String ticker, String name) {
        SecurityEntity entity = new SecurityEntity();
        entity.setId(id);
        entity.setTicker(ticker);
        entity.setName(name);
        return entity;
    }

    @Nested
    @DisplayName("GET /api/watchlist")
    class GetWatchlist {

        @Test
        @DisplayName("Gibt 200 und Liste der Watchlist-Eintraege zurueck")
        void shouldReturnWatchlistEntries() throws Exception {
            var entries = List.of(
                    createTestEntry(1L, "demo-user", 1L, "Nestlé Position"),
                    createTestEntry(2L, "demo-user", 3L, "Roche beobachten")
            );
            when(watchlistService.getWatchlist("demo-user")).thenReturn(entries);
            when(securityRepository.findById(1L))
                    .thenReturn(Optional.of(createTestSecurity(1L, "NESN", "Nestlé SA")));
            when(securityRepository.findById(3L))
                    .thenReturn(Optional.of(createTestSecurity(3L, "ROG", "Roche Holding AG")));

            mockMvc.perform(get("/api/watchlist")
                            .header("X-User-Id", "demo-user"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].securityId").value(1))
                    .andExpect(jsonPath("$[0].securityTicker").value("NESN"))
                    .andExpect(jsonPath("$[0].securityName").value("Nestlé SA"))
                    .andExpect(jsonPath("$[0].notes").value("Nestlé Position"))
                    .andExpect(jsonPath("$[1].securityTicker").value("ROG"));
        }

        @Test
        @DisplayName("Verwendet Default-userId wenn kein Header gesetzt")
        void shouldUseDefaultUserId() throws Exception {
            when(watchlistService.getWatchlist("demo-user")).thenReturn(List.of());

            mockMvc.perform(get("/api/watchlist"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(watchlistService).getWatchlist("demo-user");
        }

        @Test
        @DisplayName("Verwendet den X-User-Id Header korrekt")
        void shouldUseCustomUserId() throws Exception {
            when(watchlistService.getWatchlist("custom-user")).thenReturn(List.of());

            mockMvc.perform(get("/api/watchlist")
                            .header("X-User-Id", "custom-user"))
                    .andExpect(status().isOk());

            verify(watchlistService).getWatchlist("custom-user");
        }
    }

    @Nested
    @DisplayName("POST /api/watchlist")
    class AddToWatchlist {

        @Test
        @DisplayName("Gibt 201 Created bei erfolgreichem Hinzufuegen zurueck")
        void shouldReturn201WhenCreated() throws Exception {
            WatchlistEntity savedEntity = createTestEntry(1L, "demo-user", 1L, "Test-Notiz");
            when(watchlistService.addToWatchlist(eq("demo-user"), eq(1L), eq("Test-Notiz"), eq(true)))
                    .thenReturn(savedEntity);
            when(securityRepository.findById(1L))
                    .thenReturn(Optional.of(createTestSecurity(1L, "NESN", "Nestlé SA")));

            AddToWatchlistRequest request = new AddToWatchlistRequest();
            request.setSecurityId(1L);
            request.setNotes("Test-Notiz");
            request.setAlertOnNewReport(true);

            mockMvc.perform(post("/api/watchlist")
                            .header("X-User-Id", "demo-user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.securityId").value(1))
                    .andExpect(jsonPath("$.securityTicker").value("NESN"))
                    .andExpect(jsonPath("$.notes").value("Test-Notiz"));
        }

        @Test
        @DisplayName("Gibt 400 zurueck wenn securityId fehlt")
        void shouldReturn400WhenSecurityIdMissing() throws Exception {
            AddToWatchlistRequest request = new AddToWatchlistRequest();
            request.setNotes("Test");

            mockMvc.perform(post("/api/watchlist")
                            .header("X-User-Id", "demo-user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Gibt 400 zurueck wenn Wertschrift nicht existiert")
        void shouldReturn400WhenSecurityNotFound() throws Exception {
            when(watchlistService.addToWatchlist(eq("demo-user"), eq(999L), any(), anyBoolean()))
                    .thenThrow(new IllegalArgumentException("Wertschrift mit ID 999 existiert nicht"));

            AddToWatchlistRequest request = new AddToWatchlistRequest();
            request.setSecurityId(999L);

            mockMvc.perform(post("/api/watchlist")
                            .header("X-User-Id", "demo-user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Wertschrift mit ID 999 existiert nicht"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/watchlist/{securityId}")
    class RemoveFromWatchlist {

        @Test
        @DisplayName("Gibt 204 No Content bei erfolgreichem Entfernen zurueck")
        void shouldReturn204WhenRemoved() throws Exception {
            mockMvc.perform(delete("/api/watchlist/1")
                            .header("X-User-Id", "demo-user"))
                    .andExpect(status().isNoContent());

            verify(watchlistService).removeFromWatchlist("demo-user", 1L);
        }
    }

    @Nested
    @DisplayName("GET /api/watchlist/{securityId}/check")
    class CheckWatchlist {

        @Test
        @DisplayName("Gibt 200 mit true zurueck wenn auf Watchlist")
        void shouldReturnTrueWhenOnWatchlist() throws Exception {
            when(watchlistService.isOnWatchlist("demo-user", 1L)).thenReturn(true);

            mockMvc.perform(get("/api/watchlist/1/check")
                            .header("X-User-Id", "demo-user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.onWatchlist").value(true));
        }

        @Test
        @DisplayName("Gibt 200 mit false zurueck wenn nicht auf Watchlist")
        void shouldReturnFalseWhenNotOnWatchlist() throws Exception {
            when(watchlistService.isOnWatchlist("demo-user", 99L)).thenReturn(false);

            mockMvc.perform(get("/api/watchlist/99/check")
                            .header("X-User-Id", "demo-user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.onWatchlist").value(false));
        }
    }

    @Nested
    @DisplayName("GET /api/watchlist/count")
    class GetWatchlistCount {

        @Test
        @DisplayName("Gibt 200 mit korrekter Anzahl zurueck")
        void shouldReturnCount() throws Exception {
            when(watchlistService.getWatchlistCount("demo-user")).thenReturn(5L);

            mockMvc.perform(get("/api/watchlist/count")
                            .header("X-User-Id", "demo-user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.count").value(5));
        }
    }

    @Nested
    @DisplayName("PUT /api/watchlist/{securityId}")
    class UpdateEntry {

        @Test
        @DisplayName("Gibt 200 mit aktualisiertem Eintrag zurueck")
        void shouldReturnUpdatedEntry() throws Exception {
            WatchlistEntity updated = createTestEntry(1L, "demo-user", 1L, "Aktualisierte Notizen");
            updated.setAlertOnNewReport(false);
            when(watchlistService.updateEntry(eq("demo-user"), eq(1L), eq("Aktualisierte Notizen"), eq(false)))
                    .thenReturn(updated);
            when(securityRepository.findById(1L))
                    .thenReturn(Optional.of(createTestSecurity(1L, "NESN", "Nestlé SA")));

            AddToWatchlistRequest request = new AddToWatchlistRequest();
            request.setSecurityId(1L);
            request.setNotes("Aktualisierte Notizen");
            request.setAlertOnNewReport(false);

            mockMvc.perform(put("/api/watchlist/1")
                            .header("X-User-Id", "demo-user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.notes").value("Aktualisierte Notizen"))
                    .andExpect(jsonPath("$.alertOnNewReport").value(false));
        }
    }
}
