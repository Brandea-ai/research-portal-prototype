package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.AppInfoDto;
import com.research.portal.adapter.in.web.dto.AppStatsDto;
import com.research.portal.adapter.out.persistence.repository.JpaAnalystRepository;
import com.research.portal.adapter.out.persistence.repository.JpaReportRepository;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;

/**
 * Controller fuer Applikations-Informationen und Statistiken.
 * Demonstriert DevOps-Kompetenz: Monitoring-Readiness und Observability.
 *
 * Endpunkte:
 *   GET /api/info       — App-Name, Version, Status, Uptime
 *   GET /api/info/stats — Report-, Analyst- und Wertschriften-Zaehler
 */
@RestController
@RequestMapping("/api/info")
@Tag(name = "Info", description = "Applikations-Informationen und Betriebsstatistiken")
public class InfoController {

    private final JpaReportRepository reportRepository;
    private final JpaAnalystRepository analystRepository;
    private final JpaSecurityRepository securityRepository;

    @Value("${info.app.name:Research Portal API}")
    private String appName;

    @Value("${info.app.version:1.0.0}")
    private String appVersion;

    @Value("${info.app.description:Banking Research Portal Prototype fuer Schweizer Grossbank}")
    private String appDescription;

    public InfoController(JpaReportRepository reportRepository,
                          JpaAnalystRepository analystRepository,
                          JpaSecurityRepository securityRepository) {
        this.reportRepository = reportRepository;
        this.analystRepository = analystRepository;
        this.securityRepository = securityRepository;
    }

    @GetMapping
    @Operation(
            summary = "Applikations-Informationen abrufen",
            description = "Liefert grundlegende Informationen ueber die laufende Applikation: "
                    + "Name, Version, Status und Laufzeit seit dem letzten Start."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Applikations-Informationen erfolgreich geladen",
            content = @Content(schema = @Schema(implementation = AppInfoDto.class))
    )
    public ResponseEntity<AppInfoDto> getAppInfo() {
        long uptimeSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        String javaVersion = System.getProperty("java.version", "unknown");

        AppInfoDto info = new AppInfoDto(
                appName,
                appVersion,
                appDescription,
                "UP",
                uptimeSeconds,
                javaVersion
        );
        return ResponseEntity.ok(info);
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Betriebsstatistiken abrufen",
            description = "Liefert aktuelle Zaehler fuer Research Reports, Analysten und Wertschriften im System."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Statistiken erfolgreich geladen",
            content = @Content(schema = @Schema(implementation = AppStatsDto.class))
    )
    public ResponseEntity<AppStatsDto> getStats() {
        long reportCount = reportRepository.count();
        long analystCount = analystRepository.count();
        long securityCount = securityRepository.count();

        AppStatsDto stats = new AppStatsDto(reportCount, analystCount, securityCount);
        return ResponseEntity.ok(stats);
    }
}
