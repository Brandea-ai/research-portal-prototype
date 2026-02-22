package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.AnalystDto;
import com.research.portal.adapter.in.web.dto.ErrorResponse;
import com.research.portal.adapter.in.web.mapper.AnalystApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.port.in.GetAnalystsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysts")
@Tag(name = "Analysts", description = "Analysten-Verwaltung")
public class AnalystController {

    private final GetAnalystsUseCase getAnalysts;
    private final AnalystApiMapper mapper;

    public AnalystController(GetAnalystsUseCase getAnalysts,
                              AnalystApiMapper mapper) {
        this.getAnalysts = getAnalysts;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(
            summary = "Alle Analysten abrufen",
            description = "Liefert eine Liste aller Research-Analysten mit ihren Profildaten, "
                    + "Coverage-Universum und Performance-Kennzahlen."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste der Analysten erfolgreich geladen",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AnalystDto.class)))
    )
    public List<AnalystDto> getAllAnalysts() {
        return getAnalysts.getAllAnalysts().stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Analyst nach ID abrufen",
            description = "Liefert das vollständige Profil eines Analysten inklusive Coverage-Universum, "
                    + "Star-Rating und 12-Monats-Trefferquote."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Analyst gefunden",
                    content = @Content(schema = @Schema(implementation = AnalystDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Analyst nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<AnalystDto> getAnalystById(
            @Parameter(description = "Eindeutige Analysten-ID", example = "1")
            @PathVariable Long id) {
        return getAnalysts.getAnalystById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Analyst", id));
    }
}
