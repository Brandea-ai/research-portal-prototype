package com.research.portal.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Historischer Eintrag einer Rating-Änderung.
 * Damit kann man nachverfolgen: Wann hat welcher Analyst
 * seine Empfehlung für welche Wertschrift geändert?
 *
 * Wichtig für FINMA-Compliance (Audit Trail).
 */
public class RatingHistory {

    private Long id;
    private Long securityId;
    private Long analystId;
    private LocalDate date;
    private Rating rating;
    private BigDecimal targetPrice;
    private Long reportId;

    public RatingHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSecurityId() { return securityId; }
    public void setSecurityId(Long securityId) { this.securityId = securityId; }

    public Long getAnalystId() { return analystId; }
    public void setAnalystId(Long analystId) { this.analystId = analystId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Rating getRating() { return rating; }
    public void setRating(Rating rating) { this.rating = rating; }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
}
