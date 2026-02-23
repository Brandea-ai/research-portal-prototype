package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO fuer das Ergebnis einer Datenvalidierung.
 * Wird von GET /api/validation und POST /api/validation/run zurueckgegeben.
 */
@Schema(description = "Ergebnis der Datenintegritaetspruefung")
public class DataValidationResultDto {

    @Schema(description = "Anzahl Reports ohne gueltigen Analysten", example = "0")
    private int orphanedReports;

    @Schema(description = "Anzahl Reports ohne gueltige Wertschrift-Referenz", example = "0")
    private int invalidSecurityRefs;

    @Schema(description = "Anzahl Wertschriften mit negativer MarketCap", example = "0")
    private int negativeMarketCaps;

    @Schema(description = "Anzahl Analysten mit ungueltiger Accuracy (ausserhalb 0-100%)", example = "0")
    private int invalidAccuracies;

    @Schema(description = "Gesamtanzahl aller gefundenen Probleme", example = "0")
    private int totalIssues;

    @Schema(description = "Gibt an, ob Integritaetsprobleme gefunden wurden", example = "false")
    private boolean hasIssues;

    @Schema(description = "Zeitpunkt des letzten Validierungslaufs (ISO-8601)", example = "2026-02-23T14:30:00")
    private String lastRunAt;

    @Schema(description = "Geschaetzter naechster automatischer Validierungslauf (ISO-8601)", example = "2026-02-23T20:30:00")
    private String nextRunAt;

    public DataValidationResultDto() {
    }

    public DataValidationResultDto(int orphanedReports,
                                   int invalidSecurityRefs,
                                   int negativeMarketCaps,
                                   int invalidAccuracies,
                                   int totalIssues,
                                   boolean hasIssues,
                                   String lastRunAt,
                                   String nextRunAt) {
        this.orphanedReports = orphanedReports;
        this.invalidSecurityRefs = invalidSecurityRefs;
        this.negativeMarketCaps = negativeMarketCaps;
        this.invalidAccuracies = invalidAccuracies;
        this.totalIssues = totalIssues;
        this.hasIssues = hasIssues;
        this.lastRunAt = lastRunAt;
        this.nextRunAt = nextRunAt;
    }

    public int getOrphanedReports() {
        return orphanedReports;
    }

    public void setOrphanedReports(int orphanedReports) {
        this.orphanedReports = orphanedReports;
    }

    public int getInvalidSecurityRefs() {
        return invalidSecurityRefs;
    }

    public void setInvalidSecurityRefs(int invalidSecurityRefs) {
        this.invalidSecurityRefs = invalidSecurityRefs;
    }

    public int getNegativeMarketCaps() {
        return negativeMarketCaps;
    }

    public void setNegativeMarketCaps(int negativeMarketCaps) {
        this.negativeMarketCaps = negativeMarketCaps;
    }

    public int getInvalidAccuracies() {
        return invalidAccuracies;
    }

    public void setInvalidAccuracies(int invalidAccuracies) {
        this.invalidAccuracies = invalidAccuracies;
    }

    public int getTotalIssues() {
        return totalIssues;
    }

    public void setTotalIssues(int totalIssues) {
        this.totalIssues = totalIssues;
    }

    public boolean isHasIssues() {
        return hasIssues;
    }

    public void setHasIssues(boolean hasIssues) {
        this.hasIssues = hasIssues;
    }

    public String getLastRunAt() {
        return lastRunAt;
    }

    public void setLastRunAt(String lastRunAt) {
        this.lastRunAt = lastRunAt;
    }

    public String getNextRunAt() {
        return nextRunAt;
    }

    public void setNextRunAt(String nextRunAt) {
        this.nextRunAt = nextRunAt;
    }
}
