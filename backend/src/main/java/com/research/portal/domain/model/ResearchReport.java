package com.research.portal.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Ein Research Report: das Kernprodukt eines Analysten.
 *
 * Ein Analyst schreibt einen Report über eine Wertschrift,
 * gibt eine Empfehlung (Rating) und ein Kursziel ab.
 * Das ist der zentrale Workflow im Research Portal.
 */
public class ResearchReport {

    private Long id;
    private Long analystId;
    private Long securityId;
    private LocalDateTime publishedAt;
    private ReportType reportType;
    private String title;
    private String executiveSummary;
    private String fullText;
    private Rating rating;
    private Rating previousRating;
    private boolean ratingChanged;
    private BigDecimal targetPrice;
    private BigDecimal previousTarget;
    private BigDecimal currentPrice;
    private BigDecimal impliedUpside;
    private RiskLevel riskLevel;
    private List<String> investmentCatalysts;
    private List<String> keyRisks;
    private List<String> tags;

    public ResearchReport() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAnalystId() { return analystId; }
    public void setAnalystId(Long analystId) { this.analystId = analystId; }

    public Long getSecurityId() { return securityId; }
    public void setSecurityId(Long securityId) { this.securityId = securityId; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }

    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }

    public Rating getRating() { return rating; }
    public void setRating(Rating rating) { this.rating = rating; }

    public Rating getPreviousRating() { return previousRating; }
    public void setPreviousRating(Rating previousRating) { this.previousRating = previousRating; }

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

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public List<String> getInvestmentCatalysts() { return investmentCatalysts; }
    public void setInvestmentCatalysts(List<String> investmentCatalysts) { this.investmentCatalysts = investmentCatalysts; }

    public List<String> getKeyRisks() { return keyRisks; }
    public void setKeyRisks(List<String> keyRisks) { this.keyRisks = keyRisks; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
