package com.research.portal.application.service;

import com.research.portal.adapter.out.persistence.entity.WatchlistEntity;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import com.research.portal.adapter.out.persistence.repository.JpaWatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service fuer die Verwaltung der Analysten-Watchlist.
 *
 * Ermoeglicht das Hinzufuegen, Entfernen und Verwalten
 * von Wertschriften auf der persoenlichen Beobachtungsliste.
 */
@Service
@Transactional(readOnly = true)
public class WatchlistService {

    private final JpaWatchlistRepository watchlistRepository;
    private final JpaSecurityRepository securityRepository;

    /**
     * Erstellt eine neue WatchlistService-Instanz.
     *
     * @param watchlistRepository das Watchlist-Repository
     * @param securityRepository  das Wertschrift-Repository
     */
    public WatchlistService(JpaWatchlistRepository watchlistRepository,
                            JpaSecurityRepository securityRepository) {
        this.watchlistRepository = watchlistRepository;
        this.securityRepository = securityRepository;
    }

    /**
     * Gibt alle Watchlist-Eintraege eines Benutzers zurueck.
     *
     * @param userId die Benutzer-ID
     * @return Liste der Watchlist-Eintraege
     */
    public List<WatchlistEntity> getWatchlist(String userId) {
        return watchlistRepository.findByUserId(userId);
    }

    /**
     * Fuegt eine Wertschrift zur Watchlist hinzu.
     *
     * @param userId           die Benutzer-ID
     * @param securityId       die Wertschrift-ID
     * @param notes            optionale Notizen
     * @param alertOnNewReport ob bei neuen Reports benachrichtigt werden soll
     * @return der erstellte Watchlist-Eintrag
     * @throws IllegalArgumentException wenn die Wertschrift nicht existiert
     * @throws IllegalStateException    wenn die Wertschrift bereits auf der Watchlist ist
     */
    @Transactional
    public WatchlistEntity addToWatchlist(String userId, Long securityId,
                                          String notes, boolean alertOnNewReport) {
        if (!securityRepository.existsById(securityId)) {
            throw new IllegalArgumentException(
                    "Wertschrift mit ID " + securityId + " existiert nicht");
        }

        if (watchlistRepository.existsByUserIdAndSecurityId(userId, securityId)) {
            throw new IllegalStateException(
                    "Wertschrift mit ID " + securityId + " ist bereits auf der Watchlist");
        }

        WatchlistEntity entity = new WatchlistEntity();
        entity.setUserId(userId);
        entity.setSecurityId(securityId);
        entity.setNotes(notes);
        entity.setAlertOnNewReport(alertOnNewReport);

        return watchlistRepository.save(entity);
    }

    /**
     * Aktualisiert einen bestehenden Watchlist-Eintrag.
     *
     * @param userId           die Benutzer-ID
     * @param securityId       die Wertschrift-ID
     * @param notes            neue Notizen
     * @param alertOnNewReport neuer Alert-Status
     * @return der aktualisierte Watchlist-Eintrag
     * @throws IllegalArgumentException wenn der Eintrag nicht gefunden wurde
     */
    @Transactional
    public WatchlistEntity updateEntry(String userId, Long securityId,
                                       String notes, boolean alertOnNewReport) {
        WatchlistEntity entity = watchlistRepository
                .findByUserIdAndSecurityId(userId, securityId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Watchlist-Eintrag fuer Wertschrift " + securityId + " nicht gefunden"));

        entity.setNotes(notes);
        entity.setAlertOnNewReport(alertOnNewReport);

        return watchlistRepository.save(entity);
    }

    /**
     * Entfernt eine Wertschrift von der Watchlist.
     *
     * @param userId     die Benutzer-ID
     * @param securityId die Wertschrift-ID
     */
    @Transactional
    public void removeFromWatchlist(String userId, Long securityId) {
        watchlistRepository.deleteByUserIdAndSecurityId(userId, securityId);
    }

    /**
     * Prueft ob eine Wertschrift auf der Watchlist eines Benutzers ist.
     *
     * @param userId     die Benutzer-ID
     * @param securityId die Wertschrift-ID
     * @return true wenn die Wertschrift auf der Watchlist ist
     */
    public boolean isOnWatchlist(String userId, Long securityId) {
        return watchlistRepository.existsByUserIdAndSecurityId(userId, securityId);
    }

    /**
     * Gibt die Anzahl der Watchlist-Eintraege eines Benutzers zurueck.
     *
     * @param userId die Benutzer-ID
     * @return Anzahl der Eintraege
     */
    public long getWatchlistCount(String userId) {
        return watchlistRepository.countByUserId(userId);
    }
}
