package com.research.portal.application.service;

import com.research.portal.adapter.out.persistence.entity.WatchlistEntity;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import com.research.portal.adapter.out.persistence.repository.JpaWatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests fuer den WatchlistService.
 * Testet die Verwaltung der Analysten-Watchlist mit gemockten Repositories.
 */
@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @Mock
    private JpaWatchlistRepository watchlistRepository;

    @Mock
    private JpaSecurityRepository securityRepository;

    private WatchlistService watchlistService;

    @BeforeEach
    void setUp() {
        watchlistService = new WatchlistService(watchlistRepository, securityRepository);
    }

    /**
     * Erstellt eine Test-WatchlistEntity mit den angegebenen Werten.
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

    @Nested
    @DisplayName("getWatchlist()")
    class GetWatchlist {

        @Test
        @DisplayName("Gibt alle Watchlist-Eintraege eines Benutzers zurueck")
        void shouldReturnAllEntriesForUser() {
            var entries = List.of(
                    createTestEntry(1L, "demo-user", 1L, "Nestlé Position"),
                    createTestEntry(2L, "demo-user", 3L, "Roche beobachten"),
                    createTestEntry(3L, "demo-user", 4L, "UBS Momentum")
            );
            when(watchlistRepository.findByUserId("demo-user")).thenReturn(entries);

            var result = watchlistService.getWatchlist("demo-user");

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSecurityId()).isEqualTo(1L);
            assertThat(result.get(1).getSecurityId()).isEqualTo(3L);
            assertThat(result.get(2).getSecurityId()).isEqualTo(4L);
            verify(watchlistRepository).findByUserId("demo-user");
        }

        @Test
        @DisplayName("Gibt leere Liste zurueck wenn keine Eintraege vorhanden")
        void shouldReturnEmptyListWhenNoEntries() {
            when(watchlistRepository.findByUserId("empty-user")).thenReturn(List.of());

            var result = watchlistService.getWatchlist("empty-user");

            assertThat(result).isEmpty();
            verify(watchlistRepository).findByUserId("empty-user");
        }
    }

    @Nested
    @DisplayName("addToWatchlist()")
    class AddToWatchlist {

        @Test
        @DisplayName("Erstellt erfolgreich einen neuen Watchlist-Eintrag")
        void shouldCreateNewEntry() {
            when(securityRepository.existsById(1L)).thenReturn(true);
            when(watchlistRepository.existsByUserIdAndSecurityId("demo-user", 1L)).thenReturn(false);

            WatchlistEntity savedEntity = createTestEntry(1L, "demo-user", 1L, "Neue Notiz");
            when(watchlistRepository.save(any(WatchlistEntity.class))).thenReturn(savedEntity);

            var result = watchlistService.addToWatchlist("demo-user", 1L, "Neue Notiz", true);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUserId()).isEqualTo("demo-user");
            assertThat(result.getSecurityId()).isEqualTo(1L);
            assertThat(result.getNotes()).isEqualTo("Neue Notiz");
            assertThat(result.isAlertOnNewReport()).isTrue();
            verify(watchlistRepository).save(any(WatchlistEntity.class));
        }

        @Test
        @DisplayName("Wirft IllegalArgumentException wenn Wertschrift nicht existiert")
        void shouldThrowWhenSecurityNotFound() {
            when(securityRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> watchlistService.addToWatchlist("demo-user", 999L, "Test", true))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("999")
                    .hasMessageContaining("existiert nicht");

            verify(watchlistRepository, never()).save(any());
        }

        @Test
        @DisplayName("Wirft IllegalStateException bei doppeltem Eintrag")
        void shouldThrowWhenDuplicate() {
            when(securityRepository.existsById(1L)).thenReturn(true);
            when(watchlistRepository.existsByUserIdAndSecurityId("demo-user", 1L)).thenReturn(true);

            assertThatThrownBy(() -> watchlistService.addToWatchlist("demo-user", 1L, "Test", true))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("bereits auf der Watchlist");

            verify(watchlistRepository, never()).save(any());
        }

        @Test
        @DisplayName("Speichert Eintrag mit alertOnNewReport=false korrekt")
        void shouldSaveWithAlertDisabled() {
            when(securityRepository.existsById(2L)).thenReturn(true);
            when(watchlistRepository.existsByUserIdAndSecurityId("demo-user", 2L)).thenReturn(false);

            WatchlistEntity savedEntity = createTestEntry(2L, "demo-user", 2L, "Ohne Alert");
            savedEntity.setAlertOnNewReport(false);
            when(watchlistRepository.save(any(WatchlistEntity.class))).thenReturn(savedEntity);

            var result = watchlistService.addToWatchlist("demo-user", 2L, "Ohne Alert", false);

            assertThat(result.isAlertOnNewReport()).isFalse();
            verify(watchlistRepository).save(any(WatchlistEntity.class));
        }
    }

    @Nested
    @DisplayName("updateEntry()")
    class UpdateEntry {

        @Test
        @DisplayName("Aktualisiert Notizen und Alert-Status erfolgreich")
        void shouldUpdateNotesAndAlert() {
            WatchlistEntity existing = createTestEntry(1L, "demo-user", 1L, "Alt");
            when(watchlistRepository.findByUserIdAndSecurityId("demo-user", 1L))
                    .thenReturn(Optional.of(existing));

            WatchlistEntity updated = createTestEntry(1L, "demo-user", 1L, "Neue Notizen");
            updated.setAlertOnNewReport(false);
            when(watchlistRepository.save(any(WatchlistEntity.class))).thenReturn(updated);

            var result = watchlistService.updateEntry("demo-user", 1L, "Neue Notizen", false);

            assertThat(result.getNotes()).isEqualTo("Neue Notizen");
            assertThat(result.isAlertOnNewReport()).isFalse();
            verify(watchlistRepository).save(any(WatchlistEntity.class));
        }

        @Test
        @DisplayName("Wirft IllegalArgumentException wenn Eintrag nicht gefunden")
        void shouldThrowWhenEntryNotFound() {
            when(watchlistRepository.findByUserIdAndSecurityId("demo-user", 99L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> watchlistService.updateEntry("demo-user", 99L, "Test", true))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nicht gefunden");
        }
    }

    @Nested
    @DisplayName("removeFromWatchlist()")
    class RemoveFromWatchlist {

        @Test
        @DisplayName("Entfernt Eintrag erfolgreich")
        void shouldRemoveEntry() {
            watchlistService.removeFromWatchlist("demo-user", 1L);

            verify(watchlistRepository).deleteByUserIdAndSecurityId("demo-user", 1L);
        }
    }

    @Nested
    @DisplayName("isOnWatchlist()")
    class IsOnWatchlist {

        @Test
        @DisplayName("Gibt true zurueck wenn Wertschrift auf Watchlist")
        void shouldReturnTrueWhenOnWatchlist() {
            when(watchlistRepository.existsByUserIdAndSecurityId("demo-user", 1L)).thenReturn(true);

            var result = watchlistService.isOnWatchlist("demo-user", 1L);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Gibt false zurueck wenn Wertschrift nicht auf Watchlist")
        void shouldReturnFalseWhenNotOnWatchlist() {
            when(watchlistRepository.existsByUserIdAndSecurityId("demo-user", 99L)).thenReturn(false);

            var result = watchlistService.isOnWatchlist("demo-user", 99L);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getWatchlistCount()")
    class GetWatchlistCount {

        @Test
        @DisplayName("Gibt korrekte Anzahl zurueck")
        void shouldReturnCorrectCount() {
            when(watchlistRepository.countByUserId("demo-user")).thenReturn(3L);

            var result = watchlistService.getWatchlistCount("demo-user");

            assertThat(result).isEqualTo(3L);
            verify(watchlistRepository).countByUserId("demo-user");
        }

        @Test
        @DisplayName("Gibt 0 zurueck wenn keine Eintraege vorhanden")
        void shouldReturnZeroWhenEmpty() {
            when(watchlistRepository.countByUserId("new-user")).thenReturn(0L);

            var result = watchlistService.getWatchlistCount("new-user");

            assertThat(result).isEqualTo(0L);
        }
    }
}
