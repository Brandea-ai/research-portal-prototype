package com.research.portal.application.service;

import com.research.portal.domain.model.Rating;
import com.research.portal.domain.model.ReportType;
import com.research.portal.domain.model.ResearchReport;
import com.research.portal.domain.model.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit Tests für den XmlReportParserService.
 *
 * Testet XPath-basiertes Parsen, Filtern und Validieren
 * von Research Reports aus XML-Dateien.
 */
class XmlReportParserServiceTest {

    private XmlReportParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new XmlReportParserService();
    }

    private InputStream loadTestXml() {
        return getClass().getClassLoader()
                .getResourceAsStream("sample-reports/test-reports.xml");
    }

    private InputStream loadInvalidXml() {
        return getClass().getClassLoader()
                .getResourceAsStream("sample-reports/invalid-reports.xml");
    }

    private InputStream loadXsd() {
        return getClass().getClassLoader()
                .getResourceAsStream("sample-reports/research-report.xsd");
    }

    @Nested
    @DisplayName("parseReportsFromXml()")
    class ParseReportsFromXml {

        @Test
        @DisplayName("Parst alle Reports aus der XML-Datei")
        void shouldParseAllReports() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            assertThat(reports).hasSize(3);
        }

        @Test
        @DisplayName("Parst Titel korrekt")
        void shouldParseTitlesCorrectly() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            assertThat(reports.get(0).getTitle()).contains("Nestl\u00e9");
            assertThat(reports.get(1).getTitle()).contains("UBS");
            assertThat(reports.get(2).getTitle()).contains("Roche");
        }

        @Test
        @DisplayName("Parst Analyst-Referenz korrekt")
        void shouldParseAnalystReference() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            assertThat(reports.get(0).getAnalystId()).isEqualTo(1L);
            assertThat(reports.get(1).getAnalystId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("Parst Security-Referenz korrekt")
        void shouldParseSecurityReference() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            assertThat(reports.get(0).getSecurityId()).isEqualTo(1L); // NESN
            assertThat(reports.get(1).getSecurityId()).isEqualTo(4L); // UBSG
            assertThat(reports.get(2).getSecurityId()).isEqualTo(3L); // ROG
        }

        @Test
        @DisplayName("Parst Rating und Previous Rating korrekt")
        void shouldParseRatings() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            ResearchReport nestle = reports.get(0);
            assertThat(nestle.getRating()).isEqualTo(Rating.BUY);
            assertThat(nestle.getPreviousRating()).isEqualTo(Rating.HOLD);
            assertThat(nestle.isRatingChanged()).isTrue();

            ResearchReport roche = reports.get(2);
            assertThat(roche.getRating()).isEqualTo(Rating.HOLD);
            assertThat(roche.getPreviousRating()).isEqualTo(Rating.HOLD);
            assertThat(roche.isRatingChanged()).isFalse();
        }

        @Test
        @DisplayName("Parst ReportType korrekt")
        void shouldParseReportType() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            assertThat(reports.get(0).getReportType()).isEqualTo(ReportType.UPDATE);
            assertThat(reports.get(1).getReportType()).isEqualTo(ReportType.QUARTERLY);
        }

        @Test
        @DisplayName("Parst Finanzdaten korrekt")
        void shouldParseFinancialData() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            ResearchReport nestle = reports.get(0);
            assertThat(nestle.getTargetPrice())
                    .isEqualByComparingTo(new BigDecimal("105.00"));
            assertThat(nestle.getCurrentPrice())
                    .isEqualByComparingTo(new BigDecimal("96.50"));
            assertThat(nestle.getPreviousTarget())
                    .isEqualByComparingTo(new BigDecimal("92.00"));
        }

        @Test
        @DisplayName("Berechnet Implied Upside korrekt")
        void shouldCalculateImpliedUpside() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            ResearchReport nestle = reports.get(0);
            // Upside = (105 - 96.50) / 96.50 * 100 = 8.81%
            assertThat(nestle.getImpliedUpside()).isNotNull();
            assertThat(nestle.getImpliedUpside())
                    .isEqualByComparingTo(new BigDecimal("8.81"));
        }

        @Test
        @DisplayName("Parst RiskLevel korrekt")
        void shouldParseRiskLevel() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            assertThat(reports.get(0).getRiskLevel()).isEqualTo(RiskLevel.LOW);
            assertThat(reports.get(1).getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        }

        @Test
        @DisplayName("Parst Zeitstempel korrekt")
        void shouldParseTimestamp() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            assertThat(reports.get(0).getPublishedAt())
                    .isEqualTo(LocalDateTime.of(2026, 2, 20, 9, 30, 0));
        }

        @Test
        @DisplayName("Parst verschachtelte Katalysatoren per XPath")
        void shouldParseCatalysts() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            ResearchReport nestle = reports.get(0);
            assertThat(nestle.getInvestmentCatalysts())
                    .hasSize(2)
                    .contains("Organisches Wachstum über 4%");
        }

        @Test
        @DisplayName("Parst verschachtelte Risiken per XPath")
        void shouldParseRisks() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            ResearchReport ubs = reports.get(1);
            assertThat(ubs.getKeyRisks())
                    .hasSize(2)
                    .contains("Regulatorische Risiken");
        }

        @Test
        @DisplayName("Parst verschachtelte Tags per XPath")
        void shouldParseTags() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            ResearchReport nestle = reports.get(0);
            assertThat(nestle.getTags())
                    .hasSize(2)
                    .contains("Consumer Staples", "Large Cap");
        }

        @Test
        @DisplayName("Parst Executive Summary und trimmt Whitespace")
        void shouldParseAndTrimSummary() {
            List<ResearchReport> reports = parserService.parseReportsFromXml(loadTestXml());

            assertThat(reports.get(0).getExecutiveSummary())
                    .isEqualTo("Test-Zusammenfassung f\u00fcr Nestl\u00e9");
        }
    }

    @Nested
    @DisplayName("parseReportByTicker()")
    class ParseReportByTicker {

        @Test
        @DisplayName("Findet Report per Ticker-Symbol (XPath-Filter)")
        void shouldFindReportByTicker() {
            Optional<ResearchReport> result =
                    parserService.parseReportByTicker(loadTestXml(), "NESN");

            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).contains("Nestl\u00e9");
            assertThat(result.get().getSecurityId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Findet UBS Report per Ticker UBSG")
        void shouldFindUbsReport() {
            Optional<ResearchReport> result =
                    parserService.parseReportByTicker(loadTestXml(), "UBSG");

            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).contains("UBS");
            assertThat(result.get().getRating()).isEqualTo(Rating.BUY);
        }

        @Test
        @DisplayName("Gibt leeres Optional für unbekannten Ticker zurück")
        void shouldReturnEmptyForUnknownTicker() {
            Optional<ResearchReport> result =
                    parserService.parseReportByTicker(loadTestXml(), "UNKNOWN");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("parseReportsByRating()")
    class ParseReportsByRating {

        @Test
        @DisplayName("Filtert Reports per BUY-Rating (XPath-Filter)")
        void shouldFilterByBuyRating() {
            List<ResearchReport> buyReports =
                    parserService.parseReportsByRating(loadTestXml(), Rating.BUY);

            assertThat(buyReports).hasSize(2); // NESN und UBSG
            assertThat(buyReports).allMatch(r -> r.getRating() == Rating.BUY);
        }

        @Test
        @DisplayName("Filtert Reports per HOLD-Rating")
        void shouldFilterByHoldRating() {
            List<ResearchReport> holdReports =
                    parserService.parseReportsByRating(loadTestXml(), Rating.HOLD);

            assertThat(holdReports).hasSize(1); // ROG
            assertThat(holdReports.get(0).getTitle()).contains("Roche");
        }

        @Test
        @DisplayName("Gibt leere Liste für nicht vorhandenes Rating zurück")
        void shouldReturnEmptyForNonExistentRating() {
            List<ResearchReport> sellReports =
                    parserService.parseReportsByRating(loadTestXml(), Rating.SELL);

            assertThat(sellReports).isEmpty();
        }
    }

    @Nested
    @DisplayName("parseReportsWithTargetAbove()")
    class ParseReportsWithTargetAbove {

        @Test
        @DisplayName("Filtert Reports mit Kursziel > 100 (XPath numerischer Vergleich)")
        void shouldFilterByTargetAbove100() {
            List<ResearchReport> reports = parserService
                    .parseReportsWithTargetAbove(loadTestXml(), new BigDecimal("100"));

            // NESN: 105, ROG: 260 (UBSG: 32.50 wird ausgeschlossen)
            assertThat(reports).hasSize(2);
            assertThat(reports).allMatch(
                    r -> r.getTargetPrice().compareTo(new BigDecimal("100")) > 0);
        }

        @Test
        @DisplayName("Filtert Reports mit Kursziel > 200")
        void shouldFilterByTargetAbove200() {
            List<ResearchReport> reports = parserService
                    .parseReportsWithTargetAbove(loadTestXml(), new BigDecimal("200"));

            // Nur ROG: 260
            assertThat(reports).hasSize(1);
            assertThat(reports.get(0).getTitle()).contains("Roche");
        }

        @Test
        @DisplayName("Gibt alle Reports zurück bei Schwellenwert 0")
        void shouldReturnAllWithThresholdZero() {
            List<ResearchReport> reports = parserService
                    .parseReportsWithTargetAbove(loadTestXml(), BigDecimal.ZERO);

            assertThat(reports).hasSize(3);
        }

        @Test
        @DisplayName("Gibt leere Liste bei sehr hohem Schwellenwert zurück")
        void shouldReturnEmptyForVeryHighThreshold() {
            List<ResearchReport> reports = parserService
                    .parseReportsWithTargetAbove(loadTestXml(), new BigDecimal("1000"));

            assertThat(reports).isEmpty();
        }
    }

    @Nested
    @DisplayName("validateXml()")
    class ValidateXml {

        @Test
        @DisplayName("Validiert gültiges XML gegen XSD-Schema")
        void shouldValidateCorrectXml() {
            boolean isValid = parserService.validateXml(loadTestXml(), loadXsd());

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Erkennt ungültiges XML als invalide")
        void shouldRejectInvalidXml() {
            boolean isValid = parserService.validateXml(loadInvalidXml(), loadXsd());

            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("Fehlerbehandlung")
    class ErrorHandling {

        @Test
        @DisplayName("Wirft XmlParseException bei kaputtem XML")
        void shouldThrowExceptionForMalformedXml() {
            InputStream brokenXml = new java.io.ByteArrayInputStream(
                    "<invalid><not-closed>".getBytes());

            assertThatThrownBy(() -> parserService.parseReportsFromXml(brokenXml))
                    .isInstanceOf(XmlReportParserService.XmlParseException.class)
                    .hasMessageContaining("Fehler beim Parsen");
        }

        @Test
        @DisplayName("Wirft XmlParseException bei Ticker-Suche in kaputtem XML")
        void shouldThrowExceptionForMalformedXmlOnTickerSearch() {
            InputStream brokenXml = new java.io.ByteArrayInputStream(
                    "no xml at all".getBytes());

            assertThatThrownBy(
                    () -> parserService.parseReportByTicker(brokenXml, "NESN"))
                    .isInstanceOf(XmlReportParserService.XmlParseException.class);
        }
    }
}
