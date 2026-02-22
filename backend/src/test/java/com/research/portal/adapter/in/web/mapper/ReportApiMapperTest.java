package com.research.portal.adapter.in.web.mapper;

import com.research.portal.adapter.in.web.dto.CreateReportRequest;
import com.research.portal.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests für den ReportApiMapper.
 * Prüft die korrekte Konvertierung zwischen Domain und DTO.
 */
class ReportApiMapperTest {

    private ReportApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ReportApiMapper();
    }

    @Nested
    @DisplayName("toDto()")
    class ToDto {

        @Test
        @DisplayName("Konvertiert alle Felder korrekt von Domain zu DTO")
        void shouldMapAllFieldsCorrectly() {
            // Arrange
            ResearchReport domain = new ResearchReport();
            domain.setId(1L);
            domain.setAnalystId(2L);
            domain.setSecurityId(3L);
            domain.setPublishedAt(LocalDateTime.of(2026, 2, 22, 10, 30));
            domain.setReportType(ReportType.DEEP_DIVE);
            domain.setTitle("Nestlé Deep Dive");
            domain.setExecutiveSummary("Detaillierte Analyse");
            domain.setFullText("Vollständiger Text...");
            domain.setRating(Rating.STRONG_BUY);
            domain.setPreviousRating(Rating.BUY);
            domain.setRatingChanged(true);
            domain.setTargetPrice(new BigDecimal("130.00"));
            domain.setPreviousTarget(new BigDecimal("120.00"));
            domain.setCurrentPrice(new BigDecimal("110.00"));
            domain.setImpliedUpside(new BigDecimal("18.18"));
            domain.setRiskLevel(RiskLevel.LOW);
            domain.setInvestmentCatalysts(List.of("Wachstum", "Innovation"));
            domain.setKeyRisks(List.of("Konkurrenz"));
            domain.setTags(List.of("Healthcare", "Blue Chip"));

            // Act
            var dto = mapper.toDto(domain);

            // Assert
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getAnalystId()).isEqualTo(2L);
            assertThat(dto.getSecurityId()).isEqualTo(3L);
            assertThat(dto.getPublishedAt()).isEqualTo(LocalDateTime.of(2026, 2, 22, 10, 30));
            assertThat(dto.getReportType()).isEqualTo("DEEP_DIVE");
            assertThat(dto.getTitle()).isEqualTo("Nestlé Deep Dive");
            assertThat(dto.getExecutiveSummary()).isEqualTo("Detaillierte Analyse");
            assertThat(dto.getFullText()).isEqualTo("Vollständiger Text...");
            assertThat(dto.getRating()).isEqualTo("STRONG_BUY");
            assertThat(dto.getPreviousRating()).isEqualTo("BUY");
            assertThat(dto.isRatingChanged()).isTrue();
            assertThat(dto.getTargetPrice()).isEqualByComparingTo(new BigDecimal("130.00"));
            assertThat(dto.getPreviousTarget()).isEqualByComparingTo(new BigDecimal("120.00"));
            assertThat(dto.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("110.00"));
            assertThat(dto.getImpliedUpside()).isEqualByComparingTo(new BigDecimal("18.18"));
            assertThat(dto.getRiskLevel()).isEqualTo("LOW");
            assertThat(dto.getInvestmentCatalysts()).containsExactly("Wachstum", "Innovation");
            assertThat(dto.getKeyRisks()).containsExactly("Konkurrenz");
            assertThat(dto.getTags()).containsExactly("Healthcare", "Blue Chip");
        }

        @Test
        @DisplayName("Konvertiert null previousRating korrekt")
        void shouldHandleNullPreviousRating() {
            ResearchReport domain = new ResearchReport();
            domain.setId(1L);
            domain.setReportType(ReportType.INITIATION);
            domain.setRating(Rating.BUY);
            domain.setPreviousRating(null);
            domain.setRiskLevel(null);

            var dto = mapper.toDto(domain);

            assertThat(dto.getPreviousRating()).isNull();
            assertThat(dto.getRiskLevel()).isNull();
        }

        @Test
        @DisplayName("Konvertiert Enums korrekt zu Strings")
        void shouldMapEnumsToStrings() {
            ResearchReport domain = new ResearchReport();
            domain.setReportType(ReportType.FLASH);
            domain.setRating(Rating.SELL);
            domain.setRiskLevel(RiskLevel.SPECULATIVE);

            var dto = mapper.toDto(domain);

            assertThat(dto.getReportType()).isEqualTo("FLASH");
            assertThat(dto.getRating()).isEqualTo("SELL");
            assertThat(dto.getRiskLevel()).isEqualTo("SPECULATIVE");
        }
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("Konvertiert CreateReportRequest korrekt zu Domain")
        void shouldMapRequestToDomain() {
            CreateReportRequest request = new CreateReportRequest();
            request.setAnalystId(1L);
            request.setSecurityId(2L);
            request.setReportType("UPDATE");
            request.setTitle("Novartis Q4");
            request.setExecutiveSummary("Gutes Quartal");
            request.setFullText("Details...");
            request.setRating("BUY");
            request.setPreviousRating("HOLD");
            request.setTargetPrice(new BigDecimal("95.00"));
            request.setPreviousTarget(new BigDecimal("85.00"));
            request.setCurrentPrice(new BigDecimal("80.00"));
            request.setRiskLevel("MEDIUM");
            request.setInvestmentCatalysts(List.of("Pipeline"));
            request.setKeyRisks(List.of("Patente"));
            request.setTags(List.of("Pharma"));

            var domain = mapper.toDomain(request);

            assertThat(domain.getAnalystId()).isEqualTo(1L);
            assertThat(domain.getSecurityId()).isEqualTo(2L);
            assertThat(domain.getReportType()).isEqualTo(ReportType.UPDATE);
            assertThat(domain.getTitle()).isEqualTo("Novartis Q4");
            assertThat(domain.getRating()).isEqualTo(Rating.BUY);
            assertThat(domain.getPreviousRating()).isEqualTo(Rating.HOLD);
            assertThat(domain.isRatingChanged()).isTrue(); // BUY != HOLD
            assertThat(domain.getTargetPrice()).isEqualByComparingTo(new BigDecimal("95.00"));
            assertThat(domain.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
            assertThat(domain.getPublishedAt()).isNotNull(); // Wird auf LocalDateTime.now() gesetzt
        }

        @Test
        @DisplayName("Berechnet impliedUpside korrekt")
        void shouldCalculateImpliedUpside() {
            CreateReportRequest request = new CreateReportRequest();
            request.setAnalystId(1L);
            request.setSecurityId(1L);
            request.setReportType("UPDATE");
            request.setTitle("Test");
            request.setExecutiveSummary("Test");
            request.setRating("BUY");
            request.setTargetPrice(new BigDecimal("120.00"));
            request.setCurrentPrice(new BigDecimal("100.00"));

            var domain = mapper.toDomain(request);

            // (120 - 100) * 100 / 100 = 20.00%
            assertThat(domain.getImpliedUpside()).isEqualByComparingTo(new BigDecimal("20.00"));
        }

        @Test
        @DisplayName("Setzt ratingChanged auf false wenn kein previousRating")
        void shouldSetRatingChangedFalseWhenNoPrevious() {
            CreateReportRequest request = new CreateReportRequest();
            request.setAnalystId(1L);
            request.setSecurityId(1L);
            request.setReportType("INITIATION");
            request.setTitle("Test");
            request.setExecutiveSummary("Test");
            request.setRating("BUY");
            request.setPreviousRating(null);
            request.setTargetPrice(new BigDecimal("100.00"));

            var domain = mapper.toDomain(request);

            assertThat(domain.isRatingChanged()).isFalse();
            assertThat(domain.getPreviousRating()).isNull();
        }

        @Test
        @DisplayName("Setzt ratingChanged auf false wenn Rating gleich bleibt")
        void shouldSetRatingChangedFalseWhenSameRating() {
            CreateReportRequest request = new CreateReportRequest();
            request.setAnalystId(1L);
            request.setSecurityId(1L);
            request.setReportType("UPDATE");
            request.setTitle("Test");
            request.setExecutiveSummary("Test");
            request.setRating("BUY");
            request.setPreviousRating("BUY");
            request.setTargetPrice(new BigDecimal("100.00"));

            var domain = mapper.toDomain(request);

            assertThat(domain.isRatingChanged()).isFalse();
        }

        @Test
        @DisplayName("Handhabt null riskLevel korrekt")
        void shouldHandleNullRiskLevel() {
            CreateReportRequest request = new CreateReportRequest();
            request.setAnalystId(1L);
            request.setSecurityId(1L);
            request.setReportType("UPDATE");
            request.setTitle("Test");
            request.setExecutiveSummary("Test");
            request.setRating("BUY");
            request.setTargetPrice(new BigDecimal("100.00"));
            request.setRiskLevel(null);

            var domain = mapper.toDomain(request);

            assertThat(domain.getRiskLevel()).isNull();
        }
    }
}
