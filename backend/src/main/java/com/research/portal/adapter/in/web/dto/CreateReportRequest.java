package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Anfrage zum Erstellen oder Aktualisieren eines Research Reports")
public class CreateReportRequest {

    @NotNull(message = "analystId ist erforderlich")
    @Schema(description = "ID des zuständigen Analysten", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long analystId;

    @NotNull(message = "securityId ist erforderlich")
    @Schema(description = "ID der analysierten Wertschrift", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long securityId;

    @NotBlank(message = "reportType ist erforderlich")
    @Schema(description = "Typ des Reports", example = "INITIATION", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"INITIATION", "UPDATE", "QUARTERLY", "FLASH", "DEEP_DIVE", "CREDIT"})
    private String reportType;

    @NotBlank(message = "Titel ist erforderlich")
    @Schema(description = "Titel des Research Reports", example = "Nestlé SA - Initiation of Coverage",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Executive Summary ist erforderlich")
    @Schema(description = "Zusammenfassung der wichtigsten Erkenntnisse",
            example = "Wir initiieren die Coverage von Nestlé mit einem BUY-Rating und einem Kursziel von CHF 105.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String executiveSummary;

    @Schema(description = "Vollständiger Analysetext")
    private String fullText;

    @NotBlank(message = "Rating ist erforderlich")
    @Schema(description = "Aktuelle Empfehlung", example = "BUY", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"STRONG_BUY", "BUY", "HOLD", "SELL", "STRONG_SELL"})
    private String rating;

    @Schema(description = "Vorherige Empfehlung (bei Rating-Änderung)", example = "HOLD")
    private String previousRating;

    @NotNull(message = "Kursziel ist erforderlich")
    @Positive(message = "Kursziel muss positiv sein")
    @Schema(description = "Kursziel in der Handelswährung", example = "105.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal targetPrice;

    @Schema(description = "Vorheriges Kursziel", example = "98.50")
    private BigDecimal previousTarget;

    @Schema(description = "Aktueller Kurs der Wertschrift", example = "89.42")
    private BigDecimal currentPrice;

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

    public Long getAnalystId() { return analystId; }
    public void setAnalystId(Long analystId) { this.analystId = analystId; }

    public Long getSecurityId() { return securityId; }
    public void setSecurityId(Long securityId) { this.securityId = securityId; }

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

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public BigDecimal getPreviousTarget() { return previousTarget; }
    public void setPreviousTarget(BigDecimal previousTarget) { this.previousTarget = previousTarget; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public List<String> getInvestmentCatalysts() { return investmentCatalysts; }
    public void setInvestmentCatalysts(List<String> investmentCatalysts) { this.investmentCatalysts = investmentCatalysts; }

    public List<String> getKeyRisks() { return keyRisks; }
    public void setKeyRisks(List<String> keyRisks) { this.keyRisks = keyRisks; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
