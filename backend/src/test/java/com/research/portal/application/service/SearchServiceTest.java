package com.research.portal.application.service;

import com.research.portal.adapter.in.web.dto.SearchResponseDto;
import com.research.portal.adapter.in.web.dto.SearchResultDto;
import com.research.portal.adapter.out.persistence.entity.AnalystEntity;
import com.research.portal.adapter.out.persistence.entity.ResearchReportEntity;
import com.research.portal.adapter.out.persistence.entity.SecurityEntity;
import com.research.portal.adapter.out.persistence.repository.JpaAnalystRepository;
import com.research.portal.adapter.out.persistence.repository.JpaReportRepository;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit Tests fuer den SearchService.
 * Testet die globale Suche ueber Reports, Analysten und Wertschriften
 * mit gemockten JPA-Repositories.
 */
@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private JpaReportRepository reportRepository;

    @Mock
    private JpaAnalystRepository analystRepository;

    @Mock
    private JpaSecurityRepository securityRepository;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(reportRepository, analystRepository, securityRepository);
    }

    // === Hilfsmethoden ===

    private ResearchReportEntity createReportEntity(Long id, String title, String summary, String rating, String type) {
        ResearchReportEntity entity = new ResearchReportEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setExecutiveSummary(summary);
        entity.setRating(rating);
        entity.setReportType(type);
        entity.setAnalystId(1L);
        entity.setSecurityId(1L);
        return entity;
    }

    private AnalystEntity createAnalystEntity(Long id, String name, String title, String department, String email) {
        AnalystEntity entity = new AnalystEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setTitle(title);
        entity.setDepartment(department);
        entity.setEmail(email);
        return entity;
    }

    private SecurityEntity createSecurityEntity(Long id, String ticker, String name, String isin, String sector, String industry) {
        SecurityEntity entity = new SecurityEntity();
        entity.setId(id);
        entity.setTicker(ticker);
        entity.setName(name);
        entity.setIsin(isin);
        entity.setSector(sector);
        entity.setIndustry(industry);
        return entity;
    }

    private void setupDefaultMocks() {
        when(reportRepository.findAll()).thenReturn(List.of(
                createReportEntity(1L, "Nestlé SA - Initiation of Coverage", "Wir initiieren Coverage mit BUY", "BUY", "INITIATION"),
                createReportEntity(2L, "Novartis AG - Q4 Update", "Starkes Quartal für Novartis", "HOLD", "UPDATE"),
                createReportEntity(3L, "UBS Group - Flash Note", "UBS mit Gewinnwarnung", "SELL", "FLASH")
        ));
        when(analystRepository.findAll()).thenReturn(List.of(
                createAnalystEntity(1L, "Dr. Elena Fischer", "Senior Equity Analyst", "Equity Research", "elena.fischer@zkb.ch"),
                createAnalystEntity(2L, "Thomas Müller", "Junior Analyst", "Fixed Income Research", "thomas.mueller@zkb.ch")
        ));
        when(securityRepository.findAll()).thenReturn(List.of(
                createSecurityEntity(1L, "NESN", "Nestlé SA", "CH0038863350", "Consumer Staples", "Food Products"),
                createSecurityEntity(2L, "NOVN", "Novartis AG", "CH0012005267", "Healthcare", "Pharmaceuticals"),
                createSecurityEntity(3L, "UBSG", "UBS Group AG", "CH0244767585", "Financials", "Banks")
        ));
    }

    @Nested
    @DisplayName("Suche findet Reports")
    class SearchReports {

        @Test
        @DisplayName("Suche findet Reports nach Titel")
        void shouldFindReportsByTitle() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("Nestlé", null, 20);

            assertThat(result.getResults())
                    .extracting(SearchResultDto::getType)
                    .contains("REPORT");
            assertThat(result.getResults())
                    .anyMatch(r -> r.getType().equals("REPORT") && r.getTitle().contains("Nestlé"));
        }

        @Test
        @DisplayName("Suche findet Reports nach Executive Summary")
        void shouldFindReportsByExecutiveSummary() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("Gewinnwarnung", null, 20);

            assertThat(result.getResults())
                    .anyMatch(r -> r.getType().equals("REPORT") && r.getTitle().contains("UBS"));
        }

        @Test
        @DisplayName("Report-Titel-Match hat Relevanz 0.8")
        void shouldHaveRelevance08ForTitleContains() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("Novartis", null, 20);

            SearchResultDto reportResult = result.getResults().stream()
                    .filter(r -> r.getType().equals("REPORT") && r.getTitle().contains("Novartis"))
                    .findFirst()
                    .orElseThrow();

            assertThat(reportResult.getRelevance()).isEqualTo(0.8);
        }
    }

    @Nested
    @DisplayName("Suche findet Analysten")
    class SearchAnalysts {

        @Test
        @DisplayName("Suche findet Analysten nach Name")
        void shouldFindAnalystsByName() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("Fischer", null, 20);

            assertThat(result.getResults())
                    .anyMatch(r -> r.getType().equals("ANALYST") && r.getTitle().contains("Fischer"));
        }

        @Test
        @DisplayName("Suche findet Analysten nach Department")
        void shouldFindAnalystsByDepartment() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("Fixed Income", null, 20);

            assertThat(result.getResults())
                    .anyMatch(r -> r.getType().equals("ANALYST") && r.getTitle().contains("Müller"));
        }
    }

    @Nested
    @DisplayName("Suche findet Wertschriften")
    class SearchSecurities {

        @Test
        @DisplayName("Suche findet Wertschriften nach Ticker")
        void shouldFindSecuritiesByTicker() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("NESN", null, 20);

            assertThat(result.getResults())
                    .anyMatch(r -> r.getType().equals("SECURITY") && r.getTitle().equals("Nestlé SA"));
        }

        @Test
        @DisplayName("Exakter Ticker-Match hat Relevanz 1.0")
        void shouldHaveRelevance10ForExactTickerMatch() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("NESN", null, 20);

            SearchResultDto securityResult = result.getResults().stream()
                    .filter(r -> r.getType().equals("SECURITY") && r.getTitle().equals("Nestlé SA"))
                    .findFirst()
                    .orElseThrow();

            assertThat(securityResult.getRelevance()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("Suche findet Wertschriften nach Sektor")
        void shouldFindSecuritiesBySector() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("Healthcare", null, 20);

            assertThat(result.getResults())
                    .anyMatch(r -> r.getType().equals("SECURITY") && r.getTitle().equals("Novartis AG"));
        }
    }

    @Nested
    @DisplayName("Typ-Filter")
    class TypeFilter {

        @Test
        @DisplayName("Suche mit Type-Filter REPORT gibt nur Reports zurueck")
        void shouldReturnOnlyReportsWhenTypeIsReport() {
            when(reportRepository.findAll()).thenReturn(List.of(
                    createReportEntity(1L, "Nestlé SA - Initiation of Coverage", "Wir initiieren Coverage mit BUY", "BUY", "INITIATION")
            ));

            SearchResponseDto result = searchService.search("Nestlé", "REPORT", 20);

            assertThat(result.getResults())
                    .allMatch(r -> r.getType().equals("REPORT"));
        }

        @Test
        @DisplayName("Suche mit Type-Filter ANALYST gibt nur Analysten zurueck")
        void shouldReturnOnlyAnalystsWhenTypeIsAnalyst() {
            when(analystRepository.findAll()).thenReturn(List.of(
                    createAnalystEntity(1L, "Dr. Elena Fischer", "Senior Equity Analyst", "Equity Research", "elena.fischer@zkb.ch")
            ));

            SearchResponseDto result = searchService.search("Fischer", "ANALYST", 20);

            assertThat(result.getResults())
                    .allMatch(r -> r.getType().equals("ANALYST"));
        }

        @Test
        @DisplayName("Suche mit Type-Filter SECURITY gibt nur Wertschriften zurueck")
        void shouldReturnOnlySecuritiesWhenTypeIsSecurity() {
            when(securityRepository.findAll()).thenReturn(List.of(
                    createSecurityEntity(1L, "NESN", "Nestlé SA", "CH0038863350", "Consumer Staples", "Food Products")
            ));

            SearchResponseDto result = searchService.search("Nestlé", "SECURITY", 20);

            assertThat(result.getResults())
                    .allMatch(r -> r.getType().equals("SECURITY"));
        }
    }

    @Nested
    @DisplayName("Leere und ungueltige Suche")
    class EmptySearch {

        @Test
        @DisplayName("Leerer Query gibt leere Ergebnisse zurueck")
        void shouldReturnEmptyResultsForEmptyQuery() {
            SearchResponseDto result = searchService.search("", null, 20);

            assertThat(result.getTotalResults()).isZero();
            assertThat(result.getResults()).isEmpty();
        }

        @Test
        @DisplayName("Null-Query gibt leere Ergebnisse zurueck")
        void shouldReturnEmptyResultsForNullQuery() {
            SearchResponseDto result = searchService.search(null, null, 20);

            assertThat(result.getTotalResults()).isZero();
            assertThat(result.getResults()).isEmpty();
        }

        @Test
        @DisplayName("Whitespace-Query gibt leere Ergebnisse zurueck")
        void shouldReturnEmptyResultsForWhitespaceQuery() {
            SearchResponseDto result = searchService.search("   ", null, 20);

            assertThat(result.getTotalResults()).isZero();
            assertThat(result.getResults()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Limit-Verhalten")
    class LimitBehavior {

        @Test
        @DisplayName("Limit wird respektiert")
        void shouldRespectLimit() {
            setupDefaultMocks();

            // "a" sollte in vielen Eintraegen vorkommen
            SearchResponseDto result = searchService.search("a", null, 2);

            assertThat(result.getResults()).hasSizeLessThanOrEqualTo(2);
        }

        @Test
        @DisplayName("totalResults zeigt die Gesamtanzahl vor dem Limit")
        void shouldShowTotalResultsBeforeLimit() {
            setupDefaultMocks();

            SearchResponseDto resultAll = searchService.search("a", null, 100);
            SearchResponseDto resultLimited = searchService.search("a", null, 1);

            assertThat(resultLimited.getTotalResults()).isEqualTo(resultAll.getTotalResults());
            assertThat(resultLimited.getResults()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Sortierung und Relevanz")
    class SortingAndRelevance {

        @Test
        @DisplayName("Ergebnisse sind nach Relevanz absteigend sortiert")
        void shouldSortByRelevanceDescending() {
            setupDefaultMocks();

            // "Nestlé" matcht exakt auf Security-Name (1.0) und im Report-Titel (0.8)
            SearchResponseDto result = searchService.search("Nestlé", null, 20);

            List<Double> relevances = result.getResults().stream()
                    .map(SearchResultDto::getRelevance)
                    .toList();

            for (int i = 0; i < relevances.size() - 1; i++) {
                assertThat(relevances.get(i)).isGreaterThanOrEqualTo(relevances.get(i + 1));
            }
        }

        @Test
        @DisplayName("Exakter Match hat hoechste Relevanz")
        void shouldHaveHighestRelevanceForExactMatch() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("UBSG", null, 20);

            assertThat(result.getResults()).isNotEmpty();
            assertThat(result.getResults().get(0).getRelevance()).isEqualTo(1.0);
        }
    }

    @Nested
    @DisplayName("resultsByType Map")
    class ResultsByType {

        @Test
        @DisplayName("resultsByType Map ist korrekt")
        void shouldHaveCorrectResultsByType() {
            setupDefaultMocks();

            // "Nestlé" matcht: Report (1x Titel), Security (1x Name) => min 2 Typen
            SearchResponseDto result = searchService.search("Nestlé", null, 20);

            assertThat(result.getResultsByType()).containsKey("REPORT");
            assertThat(result.getResultsByType()).containsKey("SECURITY");
            assertThat(result.getResultsByType().get("REPORT")).isGreaterThanOrEqualTo(1);
            assertThat(result.getResultsByType().get("SECURITY")).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("resultsByType summiert sich zu totalResults")
        void shouldSumToTotalResults() {
            setupDefaultMocks();

            SearchResponseDto result = searchService.search("Nestlé", null, 20);

            int sumByType = result.getResultsByType().values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            assertThat(sumByType).isEqualTo(result.getTotalResults());
        }
    }

    @Nested
    @DisplayName("Case-Insensitivity")
    class CaseInsensitivity {

        @Test
        @DisplayName("Suche ist case-insensitive")
        void shouldBeCaseInsensitive() {
            setupDefaultMocks();

            SearchResponseDto resultLower = searchService.search("nestlé", null, 20);
            SearchResponseDto resultUpper = searchService.search("NESTLÉ", null, 20);

            assertThat(resultLower.getTotalResults()).isEqualTo(resultUpper.getTotalResults());
        }
    }
}
