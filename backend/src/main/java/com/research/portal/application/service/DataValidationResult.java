package com.research.portal.application.service;

import java.time.LocalDateTime;

/**
 * Ergebnis einer Datenvalidierung.
 * Erfasst die Anzahl der gefundenen Integritaetsprobleme in den einzelnen Entitaeten.
 * Thread-safe durch Immutability (alle Felder sind final).
 */
public class DataValidationResult {

    private final int orphanedReports;
    private final int invalidSecurityRefs;
    private final int negativeMarketCaps;
    private final int invalidAccuracies;
    private final LocalDateTime validatedAt;

    /**
     * Erstellt ein neues Validierungsergebnis.
     *
     * @param orphanedReports    Reports ohne gueltigen Analyst
     * @param invalidSecurityRefs Reports ohne gueltige Security-Referenz
     * @param negativeMarketCaps Securities mit negativer MarketCap
     * @param invalidAccuracies  Analysten mit Accuracy ausserhalb 0-100%
     * @param validatedAt        Zeitpunkt der Validierung
     */
    public DataValidationResult(int orphanedReports,
                                int invalidSecurityRefs,
                                int negativeMarketCaps,
                                int invalidAccuracies,
                                LocalDateTime validatedAt) {
        this.orphanedReports = orphanedReports;
        this.invalidSecurityRefs = invalidSecurityRefs;
        this.negativeMarketCaps = negativeMarketCaps;
        this.invalidAccuracies = invalidAccuracies;
        this.validatedAt = validatedAt;
    }

    /**
     * Prueft, ob Integritaetsprobleme gefunden wurden.
     *
     * @return true wenn mindestens ein Problem existiert
     */
    public boolean hasIssues() {
        return totalIssues() > 0;
    }

    /**
     * Gibt die Gesamtanzahl aller gefundenen Probleme zurueck.
     *
     * @return Summe aller Problemkategorien
     */
    public int totalIssues() {
        return orphanedReports + invalidSecurityRefs + negativeMarketCaps + invalidAccuracies;
    }

    public int getOrphanedReports() {
        return orphanedReports;
    }

    public int getInvalidSecurityRefs() {
        return invalidSecurityRefs;
    }

    public int getNegativeMarketCaps() {
        return negativeMarketCaps;
    }

    public int getInvalidAccuracies() {
        return invalidAccuracies;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }
}
