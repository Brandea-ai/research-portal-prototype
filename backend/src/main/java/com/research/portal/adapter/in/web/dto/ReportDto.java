package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Research Report mit Rating, Kursziel und Analyse einer Wertschrift")
public class ReportDto {

    @Schema(description = "Eindeutige Report-ID", example = "1")
    private Long id;

    @Schema(description = "ID des zuständigen Analysten", example = "1")
    private Long analystId;

    @Schema(description = "ID der analysierten Wertschrift", example = "1")
    private Long securityId;

    @Schema(description = "Publikationszeitpunkt des Reports", example = "2026-02-20T09:30:00")
    private LocalDateTime publishedAt;

    @Schema(description = "Typ des Reports", example = "INITIATION",
            allowableValues = {"INITIATION", "UPDATE", "QUARTERLY", "FLASH", "DEEP_DIVE", "CREDIT"})
    private String reportType;

    @Schema(description = "Titel des Research Reports", example = "Nestlé SA - Initiation of Coverage: Defensiver Qualitätswert mit stabiler Dividende")
    private String title;

    @Schema(description = "Zusammenfassung der wichtigsten Erkenntnisse", example = "Wir initiieren die Coverage von Nestlé mit einem BUY-Rating und einem Kursziel von CHF 105.")
    private String executiveSummary;

    @Schema(description = "Vollständiger Analysetext")
    private String fullText;

    @Schema(description = "Aktuelle Empfehlung", example = "BUY",
            allowableValues = {"STRONG_BUY", "BUY", "HOLD", "SELL", "STRONG_SELL"})
    private String rating;

    @Schema(description = "Vorherige Empfehlung (falls geändert)", example = "HOLD")
    private String previousRating;

    @Schema(description = "Kennzeichnet ob sich das Rating geändert hat", example = "true")
    private boolean ratingChanged;

    @Schema(description = "Kursziel in der Handelswährung", example = "105.00")
    private BigDecimal targetPrice;

    @Schema(description = "Vorheriges Kursziel", example = "98.50")
    private BigDecimal previousTarget;

    @Schema(description = "Aktueller Kurs der Wertschrift", example = "89.42")
    private BigDecimal currentPrice;

    @Schema(description = "Impliziertes Aufwärtspotenzial in Prozent", example = "17.43")
    private BigDecimal impliedUpside;

    @Schema(description = "Risikoeinstufung", example = "MEDIUM",
            allowableValues = {"LOW", "MEDIUM", "HIGH", "SPECULATIVE"})
    private String riskLevel;

    @Schema(description = "Katalysatoren für die Kursentwicklung",
            example = "[\"Margenexpansion durch Pricing Power\", \"Aktienrückkaufprogramm CHF 5 Mrd.\"]")
    private List<String> investmentCatalysts;

    @Schema(description = "Wesentliche Risiken für die Anlage",
            example = "[\"Währungsrisiko durch globale Diversifikation\", \"Input-Kosten-Inflation\"]")
    private List<String> keyRisks;

    @Schema(description = "Schlagwörter zur Kategorisierung",
            example = "[\"Schweiz\", \"Consumer Staples\", \"Dividende\"]")
    private List<String> tags;

    public ReportDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAnalystId() { return analystId; }
    public void setAnalystId(Long analystId) { this.analystId = analystId; }

    public Long getSecurityId() { return securityId; }
    public void setSecurityId(Long securityId) { this.securityId = securityId; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }

    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getPreviousRating() { return previousRating; }
    public void setPreviousRating(String previousRating) { this.previousRating = previousRating; }

    public boolean isRatingChanged() { return ratingChanged; }
    public void setRatingChanged(boolean ratingChanged) { this.ratingChanged = ratingChanged; }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public BigDecimal getPreviousTarget() { return previousTarget; }
    public void setPreviousTarget(BigDecimal previousTarget) { this.previousTarget = previousTarget; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getImpliedUpside() { return impliedUpside; }
    public void setImpliedUpside(BigDecimal impliedUpside) { this.impliedUpside = impliedUpside; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public List<String> getInvestmentCatalysts() { return investmentCatalysts; }
    public void setInvestmentCatalysts(List<String> investmentCatalysts) { this.investmentCatalysts = investmentCatalysts; }

    public List<String> getKeyRisks() { return keyRisks; }
    public void setKeyRisks(List<String> keyRisks) { this.keyRisks = keyRisks; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
