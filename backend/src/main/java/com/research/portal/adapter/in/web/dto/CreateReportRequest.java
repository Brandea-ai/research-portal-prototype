package com.research.portal.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class CreateReportRequest {

    @NotNull(message = "analystId ist erforderlich")
    private Long analystId;

    @NotNull(message = "securityId ist erforderlich")
    private Long securityId;

    @NotBlank(message = "reportType ist erforderlich")
    private String reportType;

    @NotBlank(message = "Titel ist erforderlich")
    private String title;

    @NotBlank(message = "Executive Summary ist erforderlich")
    private String executiveSummary;

    private String fullText;

    @NotBlank(message = "Rating ist erforderlich")
    private String rating;

    private String previousRating;

    @NotNull(message = "Kursziel ist erforderlich")
    @Positive(message = "Kursziel muss positiv sein")
    private BigDecimal targetPrice;

    private BigDecimal previousTarget;
    private BigDecimal currentPrice;
    private String riskLevel;
    private List<String> investmentCatalysts;
    private List<String> keyRisks;
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
