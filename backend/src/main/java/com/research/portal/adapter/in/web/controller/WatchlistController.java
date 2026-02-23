package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.AddToWatchlistRequest;
import com.research.portal.adapter.in.web.dto.ErrorResponse;
import com.research.portal.adapter.in.web.dto.WatchlistEntryDto;
import com.research.portal.adapter.out.persistence.entity.SecurityEntity;
import com.research.portal.adapter.out.persistence.entity.WatchlistEntity;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import com.research.portal.application.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST-Controller fuer die Analysten-Watchlist.
 *
 * Ermoeglicht das Verwalten einer persoenlichen Beobachtungsliste
 * fuer Wertschriften mit Notizen und Alert-Einstellungen.
 */
@RestController
@RequestMapping("/api/watchlist")
@Tag(name = "Watchlist", description = "Wertschriften-Watchlist fuer Analysten")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final JpaSecurityRepository securityRepository;

    /**
     * Erstellt eine neue WatchlistController-Instanz.
     *
     * @param watchlistService   der Watchlist-Service
     * @param securityRepository das Wertschrift-Repository fuer Stammdaten-Anreicherung
     */
    public WatchlistController(WatchlistService watchlistService,
                                JpaSecurityRepository securityRepository) {
        this.watchlistService = watchlistService;
        this.securityRepository = securityRepository;
    }

    /**
     * Gibt alle Watchlist-Eintraege des aktuellen Benutzers zurueck.
     *
     * @param userId die Benutzer-ID aus dem X-User-Id Header
     * @return Liste der Watchlist-Eintraege mit Wertschrift-Details
     */
    @GetMapping
    @Operation(
            summary = "Alle Watchlist-Eintraege abrufen",
            description = "Liefert alle Wertschriften auf der persoenlichen Watchlist des Analysten, "
                    + "angereichert mit Wertschrift-Stammdaten (Ticker, Name)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Watchlist erfolgreich geladen",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = WatchlistEntryDto.class)))
    )
    public ResponseEntity<List<WatchlistEntryDto>> getWatchlist(
            @Parameter(description = "Benutzer-ID des Analysten", example = "demo-user")
            @RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId) {

        List<WatchlistEntity> entries = watchlistService.getWatchlist(userId);
        List<WatchlistEntryDto> dtos = entries.stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Fuegt eine Wertschrift zur Watchlist hinzu.
     *
     * @param userId  die Benutzer-ID aus dem X-User-Id Header
     * @param request die Anfrage mit Wertschrift-ID und optionalen Parametern
     * @return der erstellte Watchlist-Eintrag
     */
    @PostMapping
    @Operation(
            summary = "Wertschrift zur Watchlist hinzufuegen",
            description = "Fuegt eine Wertschrift zur persoenlichen Watchlist hinzu. "
                    + "Optional koennen Notizen und Alert-Einstellungen angegeben werden."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Wertschrift erfolgreich zur Watchlist hinzugefuegt",
                    content = @Content(schema = @Schema(implementation = WatchlistEntryDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ungueltige Anfrage (Wertschrift existiert nicht oder bereits auf Watchlist)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<WatchlistEntryDto> addToWatchlist(
            @Parameter(description = "Benutzer-ID des Analysten", example = "demo-user")
            @RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
            @Valid @RequestBody AddToWatchlistRequest request) {

        WatchlistEntity entity = watchlistService.addToWatchlist(
                userId, request.getSecurityId(),
                request.getNotes(), request.isAlertOnNewReport());

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(entity));
    }

    /**
     * Aktualisiert einen bestehenden Watchlist-Eintrag.
     *
     * @param userId     die Benutzer-ID aus dem X-User-Id Header
     * @param securityId die Wertschrift-ID
     * @param request    die Anfrage mit aktualisierten Parametern
     * @return der aktualisierte Watchlist-Eintrag
     */
    @PutMapping("/{securityId}")
    @Operation(
            summary = "Watchlist-Eintrag aktualisieren",
            description = "Aktualisiert Notizen und Alert-Einstellungen eines bestehenden Watchlist-Eintrags."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eintrag erfolgreich aktualisiert",
                    content = @Content(schema = @Schema(implementation = WatchlistEntryDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Eintrag nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<WatchlistEntryDto> updateEntry(
            @Parameter(description = "Benutzer-ID des Analysten", example = "demo-user")
            @RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
            @Parameter(description = "ID der Wertschrift auf der Watchlist", example = "1")
            @PathVariable Long securityId,
            @Valid @RequestBody AddToWatchlistRequest request) {

        WatchlistEntity entity = watchlistService.updateEntry(
                userId, securityId,
                request.getNotes(), request.isAlertOnNewReport());

        return ResponseEntity.ok(toDto(entity));
    }

    /**
     * Entfernt eine Wertschrift von der Watchlist.
     *
     * @param userId     die Benutzer-ID aus dem X-User-Id Header
     * @param securityId die Wertschrift-ID
     * @return 204 No Content bei Erfolg
     */
    @DeleteMapping("/{securityId}")
    @Operation(
            summary = "Wertschrift von der Watchlist entfernen",
            description = "Entfernt eine Wertschrift von der persoenlichen Watchlist."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Wertschrift erfolgreich entfernt"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Eintrag nicht gefunden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> removeFromWatchlist(
            @Parameter(description = "Benutzer-ID des Analysten", example = "demo-user")
            @RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
            @Parameter(description = "ID der zu entfernenden Wertschrift", example = "1")
            @PathVariable Long securityId) {

        watchlistService.removeFromWatchlist(userId, securityId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Prueft ob eine Wertschrift auf der Watchlist ist.
     *
     * @param userId     die Benutzer-ID aus dem X-User-Id Header
     * @param securityId die Wertschrift-ID
     * @return JSON-Objekt mit dem Ergebnis der Pruefung
     */
    @GetMapping("/{securityId}/check")
    @Operation(
            summary = "Watchlist-Status einer Wertschrift pruefen",
            description = "Prueft ob eine bestimmte Wertschrift auf der Watchlist des Analysten ist."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Status erfolgreich abgefragt"
    )
    public ResponseEntity<Map<String, Boolean>> checkWatchlist(
            @Parameter(description = "Benutzer-ID des Analysten", example = "demo-user")
            @RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
            @Parameter(description = "ID der zu pruefenden Wertschrift", example = "1")
            @PathVariable Long securityId) {

        boolean onWatchlist = watchlistService.isOnWatchlist(userId, securityId);
        return ResponseEntity.ok(Map.of("onWatchlist", onWatchlist));
    }

    /**
     * Gibt die Anzahl der Watchlist-Eintraege zurueck.
     *
     * @param userId die Benutzer-ID aus dem X-User-Id Header
     * @return JSON-Objekt mit der Anzahl
     */
    @GetMapping("/count")
    @Operation(
            summary = "Anzahl der Watchlist-Eintraege",
            description = "Gibt die Gesamtanzahl der Wertschriften auf der Watchlist des Analysten zurueck."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Anzahl erfolgreich ermittelt"
    )
    public ResponseEntity<Map<String, Long>> getWatchlistCount(
            @Parameter(description = "Benutzer-ID des Analysten", example = "demo-user")
            @RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId) {

        long count = watchlistService.getWatchlistCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Konvertiert eine WatchlistEntity in ein WatchlistEntryDto.
     * Reichert das DTO mit Wertschrift-Stammdaten (Ticker, Name) an.
     *
     * @param entity die Watchlist-Entity
     * @return das angereicherte DTO
     */
    private WatchlistEntryDto toDto(WatchlistEntity entity) {
        WatchlistEntryDto dto = new WatchlistEntryDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setSecurityId(entity.getSecurityId());
        dto.setAddedAt(entity.getAddedAt());
        dto.setNotes(entity.getNotes());
        dto.setAlertOnNewReport(entity.isAlertOnNewReport());

        securityRepository.findById(entity.getSecurityId())
                .ifPresent(security -> {
                    dto.setSecurityTicker(security.getTicker());
                    dto.setSecurityName(security.getName());
                });

        return dto;
    }
}
