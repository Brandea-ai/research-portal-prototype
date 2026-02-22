package com.research.portal.domain.model;

import java.math.BigDecimal;

/**
 * Eine Wertschrift (Aktie, Anleihe, etc.).
 * In der Schweiz sagt man "Wertschrift", nicht "Security" oder "Aktie".
 *
 * Beispiel: Nestlé (NESN), Novartis (NOVN), UBS (UBSG)
 */
public class Security {

    private Long id;
    private String ticker;
    private String isin;
    private String name;
    private AssetClass assetClass;
    private String sector;
    private String industry;
    private String exchange;
    private String currency;
    private BigDecimal marketCap;

    public Security() {}

    public Security(Long id, String ticker, String isin, String name,
                    AssetClass assetClass, String sector, String industry,
                    String exchange, String currency, BigDecimal marketCap) {
        this.id = id;
        this.ticker = ticker;
        this.isin = isin;
        this.name = name;
        this.assetClass = assetClass;
        this.sector = sector;
        this.industry = industry;
        this.exchange = exchange;
        this.currency = currency;
        this.marketCap = marketCap;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getIsin() { return isin; }
    public void setIsin(String isin) { this.isin = isin; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AssetClass getAssetClass() { return assetClass; }
    public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getMarketCap() { return marketCap; }
    public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }
}
