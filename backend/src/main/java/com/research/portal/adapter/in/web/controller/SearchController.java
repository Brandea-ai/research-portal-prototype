package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.ErrorResponse;
import com.research.portal.adapter.in.web.dto.SearchResponseDto;
import com.research.portal.application.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-Controller fuer die globale Suche.
 * Ermoeglicht die gleichzeitige Suche ueber Reports, Analysten und Wertschriften
 * mit optionaler Typ-Filterung und konfigurierbarem Limit.
 */
@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Globale Suche über alle Entitäten")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Globale Suche ueber alle Entitaeten (Reports, Analysten, Wertschriften).
     *
     * @param q     Suchbegriff (Pflichtparameter)
     * @param type  Optionaler Typ-Filter: REPORT, ANALYST oder SECURITY
     * @param limit Maximale Anzahl Ergebnisse (Default: 20, Max: 100)
     * @return SearchResponseDto mit sortierten Ergebnissen und Metadaten
     */
    @GetMapping
    @Operation(
            summary = "Globale Suche",
            description = "Durchsucht Reports, Analysten und Wertschriften gleichzeitig. "
                    + "Ergebnisse werden nach Relevanz sortiert. "
                    + "Optional kann nach Typ gefiltert und die Anzahl begrenzt werden."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Suchergebnisse erfolgreich geladen",
                    content = @Content(schema = @Schema(implementation = SearchResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fehlender oder ungültiger Suchbegriff",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<SearchResponseDto> search(
            @Parameter(description = "Suchbegriff (case-insensitive)", example = "Nestlé", required = true)
            @RequestParam String q,
            @Parameter(description = "Optionaler Typ-Filter", example = "REPORT",
                    schema = @Schema(allowableValues = {"REPORT", "ANALYST", "SECURITY"}))
            @RequestParam(required = false) String type,
            @Parameter(description = "Maximale Anzahl Ergebnisse (1-100)", example = "20")
            @RequestParam(defaultValue = "20") int limit) {

        SearchResponseDto response = searchService.search(q, type, limit);
        return ResponseEntity.ok(response);
    }
}
