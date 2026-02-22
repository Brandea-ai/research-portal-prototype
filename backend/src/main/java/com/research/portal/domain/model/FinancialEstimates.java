package com.research.portal.domain.model;

import java.math.BigDecimal;

/**
 * Finanzielle Schätzungen für eine Wertschrift pro Geschäftsjahr.
 * Diese Zahlen kommen typischerweise vom Analysten und werden
 * im Detail-Panel der Wertschrift angezeigt.
 *
 * Beispiel: Nestlé 2026E → Revenue: 95.2 Mrd CHF, EPS: 4.85 CHF
 */
public class FinancialEstimates {

    private Long id;
    private Long securityId;
    private int fiscalYear;
    private BigDecimal revenue;
    private BigDecimal revenueGrowth;
    private BigDecimal ebitda;
    private BigDecimal ebitdaMargin;
    private BigDecimal eps;
    private BigDecimal peRatio;
    private BigDecimal evEbitda;
    private BigDecimal dividendYield;

    public FinancialEstimates() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSecurityId() { return securityId; }
    public void setSecurityId(Long securityId) { this.securityId = securityId; }

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

    public BigDecimal getRevenueGrowth() { return revenueGrowth; }
    public void setRevenueGrowth(BigDecimal revenueGrowth) { this.revenueGrowth = revenueGrowth; }

    public BigDecimal getEbitda() { return ebitda; }
    public void setEbitda(BigDecimal ebitda) { this.ebitda = ebitda; }

    public BigDecimal getEbitdaMargin() { return ebitdaMargin; }
    public void setEbitdaMargin(BigDecimal ebitdaMargin) { this.ebitdaMargin = ebitdaMargin; }

    public BigDecimal getEps() { return eps; }
    public void setEps(BigDecimal eps) { this.eps = eps; }

    public BigDecimal getPeRatio() { return peRatio; }
    public void setPeRatio(BigDecimal peRatio) { this.peRatio = peRatio; }

    public BigDecimal getEvEbitda() { return evEbitda; }
    public void setEvEbitda(BigDecimal evEbitda) { this.evEbitda = evEbitda; }

    public BigDecimal getDividendYield() { return dividendYield; }
    public void setDividendYield(BigDecimal dividendYield) { this.dividendYield = dividendYield; }
}
