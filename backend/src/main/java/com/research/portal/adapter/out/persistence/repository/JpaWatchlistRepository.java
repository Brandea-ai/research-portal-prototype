package com.research.portal.adapter.out.persistence.repository;

import com.research.portal.adapter.out.persistence.entity.WatchlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-Repository fuer Watchlist-Eintraege.
 *
 * Bietet CRUD-Operationen und benutzerspezifische Abfragen
 * fuer die Analysten-Watchlist.
 */
public interface JpaWatchlistRepository extends JpaRepository<WatchlistEntity, Long> {

    /**
     * Findet alle Watchlist-Eintraege eines Benutzers.
     *
     * @param userId die Benutzer-ID
     * @return Liste der Watchlist-Eintraege
     */
    List<WatchlistEntity> findByUserId(String userId);

    /**
     * Findet einen spezifischen Watchlist-Eintrag fuer Benutzer und Wertschrift.
     *
     * @param userId     die Benutzer-ID
     * @param securityId die Wertschrift-ID
     * @return Optional mit dem Eintrag, falls vorhanden
     */
    Optional<WatchlistEntity> findByUserIdAndSecurityId(String userId, Long securityId);

    /**
     * Prueft ob eine Wertschrift auf der Watchlist eines Benutzers ist.
     *
     * @param userId     die Benutzer-ID
     * @param securityId die Wertschrift-ID
     * @return true wenn die Wertschrift auf der Watchlist ist
     */
    boolean existsByUserIdAndSecurityId(String userId, Long securityId);

    /**
     * Entfernt einen Watchlist-Eintrag fuer Benutzer und Wertschrift.
     *
     * @param userId     die Benutzer-ID
     * @param securityId die Wertschrift-ID
     */
    void deleteByUserIdAndSecurityId(String userId, Long securityId);

    /**
     * Zaehlt die Anzahl der Watchlist-Eintraege eines Benutzers.
     *
     * @param userId die Benutzer-ID
     * @return Anzahl der Eintraege
     */
    long countByUserId(String userId);
}
