package com.research.portal.application.service;

import com.research.portal.adapter.out.persistence.entity.AnalystEntity;
import com.research.portal.adapter.out.persistence.entity.ResearchReportEntity;
import com.research.portal.adapter.out.persistence.entity.SecurityEntity;
import com.research.portal.adapter.out.persistence.repository.JpaAnalystRepository;
import com.research.portal.adapter.out.persistence.repository.JpaReportRepository;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scheduled Task Service fuer periodische Datenvalidierung und System-Wartung.
 *
 * <p>Fuehrt automatisierte Integritaetspruefungen auf allen Kern-Entitaeten durch:
 * Reports, Analysten und Wertschriften. Ergebnisse werden geloggt und koennen
 * ueber den ValidationController abgerufen werden.
 *
 * <p>Demonstriert Spring @Scheduled fuer Enterprise-Wartbarkeit
 * und proaktive Datenqualitaetssicherung im Banking-Kontext.
 */
@Service
public class ScheduledTaskService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskService.class);

    private final JpaReportRepository reportRepository;
    private final JpaAnalystRepository analystRepository;
    private final JpaSecurityRepository securityRepository;

    /** Zeitpunkt des letzten Validierungslaufs. Thread-safe durch volatile. */
    private volatile LocalDateTime lastValidationRun;

    /** Ergebnis des letzten Validierungslaufs. Thread-safe durch volatile. */
    private volatile DataValidationResult lastValidationResult;

    /**
     * Constructor Injection aller benoetigten Repositories.
     *
     * @param reportRepository   Repository fuer Research Reports
     * @param analystRepository  Repository fuer Analysten
     * @param securityRepository Repository fuer Wertschriften
     */
    public ScheduledTaskService(JpaReportRepository reportRepository,
                                JpaAnalystRepository analystRepository,
                                JpaSecurityRepository securityRepository) {
        this.reportRepository = reportRepository;
        this.analystRepository = analystRepository;
        this.securityRepository = securityRepository;
    }

    /**
     * Datenvalidierung: Laeuft alle 6 Stunden.
     * Prueft die Datenintegritaet aller Kern-Entitaeten:
     * <ul>
     *   <li>Reports ohne gueltige Analyst-Referenz (verwaiste Reports)</li>
     *   <li>Reports ohne gueltige Security-Referenz</li>
     *   <li>Wertschriften mit negativer MarketCap</li>
     *   <li>Analysten mit Accuracy ausserhalb 0-100%</li>
     * </ul>
     */
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000, initialDelay = 60000)
    public void validateData() {
        log.info("Starte planmaessige Datenvalidierung...");

        DataValidationResult result = runValidation();
        this.lastValidationResult = result;
        this.lastValidationRun = LocalDateTime.now();

        if (result.hasIssues()) {
            log.warn("Datenvalidierung: {} Probleme gefunden", result.totalIssues());
        } else {
            log.info("Datenvalidierung abgeschlossen: Keine Probleme");
        }
    }

    /**
     * System-Statistik-Log: Laeuft jede Stunde.
     * Loggt aktuelle Entitaets-Zaehler fuer Monitoring und Observability.
     */
    @Scheduled(fixedRate = 60 * 60 * 1000, initialDelay = 30000)
    public void logSystemStats() {
        long reports = reportRepository.count();
        long analysts = analystRepository.count();
        long securities = securityRepository.count();
        log.info("System-Statistik: {} Reports, {} Analysten, {} Wertschriften",
                reports, analysts, securities);
    }

    /**
     * Fuehrt die Datenvalidierung durch. Kann manuell oder per Schedule aufgerufen werden.
     *
     * @return Ergebnis der Validierung mit Anzahl der Probleme pro Kategorie
     */
    public DataValidationResult runValidation() {
        int orphanedReports = countOrphanedReports();
        int invalidSecurityRefs = countInvalidSecurityRefs();
        int negativeMarketCaps = countNegativeMarketCaps();
        int invalidAccuracies = countInvalidAccuracies();

        return new DataValidationResult(
                orphanedReports,
                invalidSecurityRefs,
                negativeMarketCaps,
                invalidAccuracies,
                LocalDateTime.now()
        );
    }

    /**
     * Gibt den Zeitpunkt des letzten Validierungslaufs zurueck.
     *
     * @return Zeitpunkt oder null wenn noch kein Lauf stattfand
     */
    public LocalDateTime getLastValidationRun() {
        return lastValidationRun;
    }

    /**
     * Gibt das Ergebnis des letzten Validierungslaufs zurueck.
     *
     * @return Ergebnis oder null wenn noch kein Lauf stattfand
     */
    public DataValidationResult getLastValidationResult() {
        return lastValidationResult;
    }

    /**
     * Zaehlt Reports, deren analystId auf keinen existierenden Analysten verweist.
     */
    private int countOrphanedReports() {
        List<ResearchReportEntity> allReports = reportRepository.findAll();
        Set<Long> analystIds = analystRepository.findAll().stream()
                .map(AnalystEntity::getId)
                .collect(Collectors.toSet());

        return (int) allReports.stream()
                .filter(report -> report.getAnalystId() == null
                        || !analystIds.contains(report.getAnalystId()))
                .count();
    }

    /**
     * Zaehlt Reports, deren securityId auf keine existierende Wertschrift verweist.
     */
    private int countInvalidSecurityRefs() {
        List<ResearchReportEntity> allReports = reportRepository.findAll();
        Set<Long> securityIds = securityRepository.findAll().stream()
                .map(SecurityEntity::getId)
                .collect(Collectors.toSet());

        return (int) allReports.stream()
                .filter(report -> report.getSecurityId() == null
                        || !securityIds.contains(report.getSecurityId()))
                .count();
    }

    /**
     * Zaehlt Wertschriften mit negativer MarketCap.
     */
    private int countNegativeMarketCaps() {
        return (int) securityRepository.findAll().stream()
                .filter(security -> security.getMarketCap() != null
                        && security.getMarketCap().compareTo(BigDecimal.ZERO) < 0)
                .count();
    }

    /**
     * Zaehlt Analysten mit einer Accuracy ausserhalb des gueltigen Bereichs 0-100%.
     */
    private int countInvalidAccuracies() {
        return (int) analystRepository.findAll().stream()
                .filter(analyst -> analyst.getAccuracy12m() < 0.0
                        || analyst.getAccuracy12m() > 100.0)
                .count();
    }
}
