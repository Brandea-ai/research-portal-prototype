package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.ErrorResponse;
import com.research.portal.adapter.in.web.dto.SecurityDto;
import com.research.portal.adapter.in.web.mapper.SecurityApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.port.in.GetSecuritiesUseCase;
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
@RequestMapping("/api/securities")
@Tag(name = "Securities", description = "Wertschriften-Stammdaten")
public class SecurityController {

    private final GetSecuritiesUseCase getSecurities;
    private final SecurityApiMapper mapper;

    public SecurityController(GetSecuritiesUseCase getSecurities,
                               SecurityApiMapper mapper) {
        this.getSecurities = getSecurities;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(
            summary = "Alle Wertschriften abrufen",
            description = "Liefert eine Liste aller im System erfassten Wertschriften (Aktien, Obligationen, Derivate). "
                    + "Umfasst das gesamte Coverage-Universum der Bank."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste der Wertschriften erfolgreich geladen",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SecurityDto.class)))
    )
    public List<SecurityDto> getAllSecurities() {
        return getSecurities.getAllSecurities().stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Wertschrift nach ID abrufen",
            description = "Liefert die Stammdaten einer einzelnen Wertschrift anhand ihrer eindeutigen ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Wertschrift gefunden",
                    content = @Content(schema = @Schema(implementation = SecurityDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Wertschrift nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<SecurityDto> getSecurityById(
            @Parameter(description = "Eindeutige Wertschrift-ID", example = "1")
            @PathVariable Long id) {
        return getSecurities.getSecurityById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Security", id));
    }

    @GetMapping(params = "ticker")
    @Operation(
            summary = "Wertschrift nach Ticker suchen",
            description = "Sucht eine Wertschrift anhand ihres Börsentickers (z.B. NESN, UBSG, ROG). "
                    + "Der Ticker muss exakt übereinstimmen."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Wertschrift gefunden",
                    content = @Content(schema = @Schema(implementation = SecurityDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Wertschrift mit diesem Ticker nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<SecurityDto> getSecurityByTicker(
            @Parameter(description = "Börsenticker der Wertschrift", example = "NESN")
            @RequestParam String ticker) {
        return getSecurities.getSecurityByTicker(ticker)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Security", "ticker", ticker));
    }
}
