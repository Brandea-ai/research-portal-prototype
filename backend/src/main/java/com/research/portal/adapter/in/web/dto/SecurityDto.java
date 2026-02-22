package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Wertschrift-Stammdaten: Aktie, Obligation oder Derivat im Coverage-Universum")
public class SecurityDto {

    @Schema(description = "Eindeutige Wertschrift-ID", example = "1")
    private Long id;

    @Schema(description = "Börsenticker (SIX Swiss Exchange)", example = "NESN")
    private String ticker;

    @Schema(description = "International Securities Identification Number", example = "CH0038863350")
    private String isin;

    @Schema(description = "Vollständiger Name der Wertschrift", example = "Nestlé SA")
    private String name;

    @Schema(description = "Anlageklasse", example = "EQUITY",
            allowableValues = {"EQUITY", "FIXED_INCOME", "DERIVATIVES", "MACRO"})
    private String assetClass;

    @Schema(description = "Sektor nach GICS-Klassifikation", example = "Consumer Staples")
    private String sector;

    @Schema(description = "Branche innerhalb des Sektors", example = "Packaged Foods & Meats")
    private String industry;

    @Schema(description = "Börsenplatz", example = "SIX")
    private String exchange;

    @Schema(description = "Handelswährung", example = "CHF")
    private String currency;

    @Schema(description = "Marktkapitalisierung in Mio. der Handelswährung", example = "245000000000")
    private BigDecimal marketCap;

    public SecurityDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getIsin() { return isin; }
    public void setIsin(String isin) { this.isin = isin; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAssetClass() { return assetClass; }
    public void setAssetClass(String assetClass) { this.assetClass = assetClass; }

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
