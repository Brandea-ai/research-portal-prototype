package com.research.portal.application.service;

import com.research.portal.adapter.in.web.dto.SearchResponseDto;
import com.research.portal.adapter.in.web.dto.SearchResultDto;
import com.research.portal.adapter.out.persistence.entity.AnalystEntity;
import com.research.portal.adapter.out.persistence.entity.ResearchReportEntity;
import com.research.portal.adapter.out.persistence.entity.SecurityEntity;
import com.research.portal.adapter.out.persistence.repository.JpaAnalystRepository;
import com.research.portal.adapter.out.persistence.repository.JpaReportRepository;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service fuer die globale Suche ueber alle Entitaeten.
 * Durchsucht Reports, Analysten und Wertschriften gleichzeitig
 * und berechnet einen Relevanz-Score fuer jedes Ergebnis.
 *
 * <p>Relevanz-Berechnung:
 * <ul>
 *   <li>1.0 = Exakter Match (Titel/Name/Ticker stimmt exakt ueberein)</li>
 *   <li>0.8 = Titel-/Name-Match (Suchbegriff ist im Primaerfeld enthalten)</li>
 *   <li>0.5 = Sekundaerfeld-Match (Suchbegriff in anderen Feldern gefunden)</li>
 * </ul>
 */
@Service
public class SearchService {

    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 20;

    private final JpaReportRepository reportRepository;
    private final JpaAnalystRepository analystRepository;
    private final JpaSecurityRepository securityRepository;

    public SearchService(JpaReportRepository reportRepository,
                         JpaAnalystRepository analystRepository,
                         JpaSecurityRepository securityRepository) {
        this.reportRepository = reportRepository;
        this.analystRepository = analystRepository;
        this.securityRepository = securityRepository;
    }

    /**
     * Fuehrt eine globale Suche ueber alle Entitaeten durch.
     *
     * @param query  Suchbegriff (case-insensitive)
     * @param type   Optionaler Typ-Filter: "REPORT", "ANALYST" oder "SECURITY" (null = alle)
     * @param limit  Maximale Anzahl Ergebnisse (1-100, Default 20)
     * @return SearchResponseDto mit Ergebnissen, sortiert nach Relevanz
     */
    public SearchResponseDto search(String query, String type, int limit) {
        if (query == null || query.isBlank()) {
            return new SearchResponseDto(query, 0, List.of(), Map.of());
        }

        int effectiveLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        String lowerQuery = query.toLowerCase();

        List<SearchResultDto> allResults = new ArrayList<>();

        if (type == null || "REPORT".equalsIgnoreCase(type)) {
            allResults.addAll(searchReports(lowerQuery));
        }
        if (type == null || "ANALYST".equalsIgnoreCase(type)) {
            allResults.addAll(searchAnalysts(lowerQuery));
        }
        if (type == null || "SECURITY".equalsIgnoreCase(type)) {
            allResults.addAll(searchSecurities(lowerQuery));
        }

        // Nach Relevanz absteigend sortieren
        allResults.sort(Comparator.comparingDouble(SearchResultDto::getRelevance).reversed());

        // Limit anwenden
        List<SearchResultDto> limitedResults = allResults.stream()
                .limit(effectiveLimit)
                .toList();

        // Ergebnisse nach Typ zaehlen (vor dem Limit, um die Gesamtverteilung zu zeigen)
        Map<String, Integer> resultsByType = allResults.stream()
                .collect(Collectors.groupingBy(SearchResultDto::getType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        return new SearchResponseDto(query, allResults.size(), limitedResults, resultsByType);
    }

    /**
     * Durchsucht alle Reports nach dem Suchbegriff.
     * Durchsuchte Felder: title (primaer), executiveSummary, rating, reportType.
     */
    private List<SearchResultDto> searchReports(String lowerQuery) {
        return reportRepository.findAll().stream()
                .map(report -> matchReport(report, lowerQuery))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Durchsucht alle Analysten nach dem Suchbegriff.
     * Durchsuchte Felder: name (primaer), title, department, email.
     */
    private List<SearchResultDto> searchAnalysts(String lowerQuery) {
        return analystRepository.findAll().stream()
                .map(analyst -> matchAnalyst(analyst, lowerQuery))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Durchsucht alle Wertschriften nach dem Suchbegriff.
     * Durchsuchte Felder: name (primaer), ticker (primaer), isin, sector, industry.
     */
    private List<SearchResultDto> searchSecurities(String lowerQuery) {
        return securityRepository.findAll().stream()
                .map(security -> matchSecurity(security, lowerQuery))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Prueft einen Report auf Uebereinstimmung mit dem Suchbegriff
     * und berechnet den Relevanz-Score.
     */
    private Optional<SearchResultDto> matchReport(ResearchReportEntity report, String lowerQuery) {
        String title = nullSafe(report.getTitle());
        String summary = nullSafe(report.getExecutiveSummary());
        String rating = nullSafe(report.getRating());
        String reportType = nullSafe(report.getReportType());

        double relevance = 0.0;
        String highlight = null;

        // Exakter Match auf Titel
        if (title.toLowerCase().equals(lowerQuery)) {
            relevance = 1.0;
            highlight = title;
        }
        // Titel enthaelt Suchbegriff
        else if (title.toLowerCase().contains(lowerQuery)) {
            relevance = 0.8;
            highlight = extractHighlight(title, lowerQuery);
        }
        // Sekundaere Felder
        else if (summary.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = extractHighlight(summary, lowerQuery);
        }
        else if (rating.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = rating;
        }
        else if (reportType.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = reportType;
        }

        if (relevance > 0.0) {
            String subtitle = rating + " | " + reportType;
            return Optional.of(new SearchResultDto("REPORT", report.getId(), title, subtitle, highlight, relevance));
        }
        return Optional.empty();
    }

    /**
     * Prueft einen Analysten auf Uebereinstimmung mit dem Suchbegriff
     * und berechnet den Relevanz-Score.
     */
    private Optional<SearchResultDto> matchAnalyst(AnalystEntity analyst, String lowerQuery) {
        String name = nullSafe(analyst.getName());
        String analystTitle = nullSafe(analyst.getTitle());
        String department = nullSafe(analyst.getDepartment());
        String email = nullSafe(analyst.getEmail());

        double relevance = 0.0;
        String highlight = null;

        // Exakter Match auf Name
        if (name.toLowerCase().equals(lowerQuery)) {
            relevance = 1.0;
            highlight = name;
        }
        // Name enthaelt Suchbegriff
        else if (name.toLowerCase().contains(lowerQuery)) {
            relevance = 0.8;
            highlight = extractHighlight(name, lowerQuery);
        }
        // Sekundaere Felder
        else if (analystTitle.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = extractHighlight(analystTitle, lowerQuery);
        }
        else if (department.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = extractHighlight(department, lowerQuery);
        }
        else if (email.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = email;
        }

        if (relevance > 0.0) {
            String subtitle = analystTitle + " | " + department;
            return Optional.of(new SearchResultDto("ANALYST", analyst.getId(), name, subtitle, highlight, relevance));
        }
        return Optional.empty();
    }

    /**
     * Prueft eine Wertschrift auf Uebereinstimmung mit dem Suchbegriff
     * und berechnet den Relevanz-Score.
     */
    private Optional<SearchResultDto> matchSecurity(SecurityEntity security, String lowerQuery) {
        String name = nullSafe(security.getName());
        String ticker = nullSafe(security.getTicker());
        String isin = nullSafe(security.getIsin());
        String sector = nullSafe(security.getSector());
        String industry = nullSafe(security.getIndustry());

        double relevance = 0.0;
        String highlight = null;

        // Exakter Match auf Ticker oder Name
        if (ticker.toLowerCase().equals(lowerQuery) || name.toLowerCase().equals(lowerQuery)) {
            relevance = 1.0;
            highlight = ticker.toLowerCase().equals(lowerQuery) ? ticker : name;
        }
        // Name oder Ticker enthaelt Suchbegriff
        else if (name.toLowerCase().contains(lowerQuery)) {
            relevance = 0.8;
            highlight = extractHighlight(name, lowerQuery);
        }
        else if (ticker.toLowerCase().contains(lowerQuery)) {
            relevance = 0.8;
            highlight = ticker;
        }
        // Sekundaere Felder
        else if (isin.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = isin;
        }
        else if (sector.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = extractHighlight(sector, lowerQuery);
        }
        else if (industry.toLowerCase().contains(lowerQuery)) {
            relevance = 0.5;
            highlight = extractHighlight(industry, lowerQuery);
        }

        if (relevance > 0.0) {
            String subtitle = ticker + " | " + sector;
            return Optional.of(new SearchResultDto("SECURITY", security.getId(), name, subtitle, highlight, relevance));
        }
        return Optional.empty();
    }

    /**
     * Extrahiert einen Textausschnitt rund um den gefundenen Suchbegriff.
     * Zeigt maximal 40 Zeichen vor und nach dem Treffer.
     */
    private String extractHighlight(String text, String lowerQuery) {
        int index = text.toLowerCase().indexOf(lowerQuery);
        if (index < 0) {
            return text;
        }
        int start = Math.max(0, index - 40);
        int end = Math.min(text.length(), index + lowerQuery.length() + 40);
        StringBuilder sb = new StringBuilder();
        if (start > 0) sb.append("...");
        sb.append(text, start, end);
        if (end < text.length()) sb.append("...");
        return sb.toString();
    }

    /**
     * Null-sichere String-Konvertierung.
     */
    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
