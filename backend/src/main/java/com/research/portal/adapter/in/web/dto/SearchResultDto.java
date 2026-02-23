package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Einzelnes Suchergebnis aus der globalen Suche.
 * Repraesentiert ein gefundenes Objekt (Report, Analyst oder Wertschrift)
 * mit einheitlicher Struktur fuer die Darstellung im Frontend.
 */
@Schema(description = "Einzelnes Suchergebnis der globalen Suche")
public class SearchResultDto {

    @Schema(description = "Typ des Ergebnisses", example = "REPORT",
            allowableValues = {"REPORT", "ANALYST", "SECURITY"})
    private String type;

    @Schema(description = "Eindeutige ID des Ergebnisses", example = "1")
    private Long id;

    @Schema(description = "Titel des Ergebnisses (Report-Titel, Analyst-Name oder Wertschrift-Name)",
            example = "Nestlé SA - Initiation of Coverage")
    private String title;

    @Schema(description = "Untertitel mit zusaetzlichem Kontext (Rating + Typ, Titel + Abteilung, Ticker + Sektor)",
            example = "BUY | INITIATION")
    private String subtitle;

    @Schema(description = "Textausschnitt mit dem gefundenen Suchbegriff",
            example = "...Nestlé zeigt starkes Wachstum im Q4...")
    private String highlight;

    @Schema(description = "Relevanz-Score von 0.0 (niedrig) bis 1.0 (exakter Treffer)", example = "0.8")
    private double relevance;

    public SearchResultDto() {}

    public SearchResultDto(String type, Long id, String title, String subtitle, String highlight, double relevance) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.highlight = highlight;
        this.relevance = relevance;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getHighlight() { return highlight; }
    public void setHighlight(String highlight) { this.highlight = highlight; }

    public double getRelevance() { return relevance; }
    public void setRelevance(double relevance) { this.relevance = relevance; }
}
