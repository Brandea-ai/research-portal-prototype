package com.research.portal.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "research_reports")
public class ResearchReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analyst_id", nullable = false)
    private Long analystId;

    @Column(name = "security_id", nullable = false)
    private Long securityId;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    @Column(nullable = false)
    private String title;

    @Column(name = "executive_summary", length = 2000)
    private String executiveSummary;

    @Column(name = "full_text", length = 10000)
    private String fullText;

    @Column(nullable = false)
    private String rating;

    @Column(name = "previous_rating")
    private String previousRating;

    @Column(name = "rating_changed")
    private boolean ratingChanged;

    @Column(name = "target_price", precision = 10, scale = 2)
    private BigDecimal targetPrice;

    @Column(name = "previous_target", precision = 10, scale = 2)
    private BigDecimal previousTarget;

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "implied_upside", precision = 5, scale = 2)
    private BigDecimal impliedUpside;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "investment_catalysts", length = 1000)
    private String investmentCatalysts;

    @Column(name = "key_risks", length = 1000)
    private String keyRisks;

    @Column(length = 500)
    private String tags;

    public ResearchReportEntity() {}

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

    public String getInvestmentCatalysts() { return investmentCatalysts; }
    public void setInvestmentCatalysts(String investmentCatalysts) { this.investmentCatalysts = investmentCatalysts; }

    public String getKeyRisks() { return keyRisks; }
    public void setKeyRisks(String keyRisks) { this.keyRisks = keyRisks; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
