package com.research.portal.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * Gesamtantwort der globalen Suche.
 * Enthaelt die Suchergebnisse, Metadaten und eine Aufschluesselung nach Typ.
 */
@Schema(description = "Antwort der globalen Suche über Reports, Analysten und Wertschriften")
public class SearchResponseDto {

    @Schema(description = "Der ursprüngliche Suchbegriff", example = "Nestlé")
    private String query;

    @Schema(description = "Gesamtanzahl gefundener Ergebnisse", example = "5")
    private int totalResults;

    @Schema(description = "Liste der Suchergebnisse, sortiert nach Relevanz")
    private List<SearchResultDto> results;

    @Schema(description = "Aufschlüsselung der Ergebnisse nach Typ",
            example = "{\"REPORT\": 3, \"ANALYST\": 1, \"SECURITY\": 1}")
    private Map<String, Integer> resultsByType;

    public SearchResponseDto() {}

    public SearchResponseDto(String query, int totalResults, List<SearchResultDto> results,
                             Map<String, Integer> resultsByType) {
        this.query = query;
        this.totalResults = totalResults;
        this.results = results;
        this.resultsByType = resultsByType;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }

    public List<SearchResultDto> getResults() { return results; }
    public void setResults(List<SearchResultDto> results) { this.results = results; }

    public Map<String, Integer> getResultsByType() { return resultsByType; }
    public void setResultsByType(Map<String, Integer> resultsByType) { this.resultsByType = resultsByType; }
}
