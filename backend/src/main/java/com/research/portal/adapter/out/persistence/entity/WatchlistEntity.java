package com.research.portal.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA-Entity fuer die Watchlist-Tabelle.
 *
 * Bildet die watchlist-Tabelle in der Datenbank ab.
 * Ermoeglicht es Analysten, Wertschriften zu beobachten
 * und Benachrichtigungen bei neuen Reports zu erhalten.
 */
@Entity
@Table(name = "watchlist")
public class WatchlistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "security_id", nullable = false)
    private Long securityId;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Column(length = 500)
    private String notes;

    @Column(name = "alert_on_new_report", nullable = false)
    private boolean alertOnNewReport = true;

    /**
     * Setzt den Zeitstempel beim Erstellen automatisch.
     */
    @PrePersist
    protected void onCreate() {
        if (this.addedAt == null) {
            this.addedAt = LocalDateTime.now();
        }
    }

    public WatchlistEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSecurityId() {
        return securityId;
    }

    public void setSecurityId(Long securityId) {
        this.securityId = securityId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isAlertOnNewReport() {
        return alertOnNewReport;
    }

    public void setAlertOnNewReport(boolean alertOnNewReport) {
        this.alertOnNewReport = alertOnNewReport;
    }
}
