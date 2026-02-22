package com.research.portal.application.service;

import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.model.*;
import com.research.portal.domain.port.out.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für den ReportService.
 * Testet alle Use-Case-Methoden mit gemocktem ReportRepository.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(reportRepository);
    }

    // Hilfsmethode: Erstellt einen Test-Report
    private ResearchReport createTestReport(Long id, String title) {
        ResearchReport report = new ResearchReport();
        report.setId(id);
        report.setAnalystId(1L);
        report.setSecurityId(1L);
        report.setPublishedAt(LocalDateTime.of(2026, 2, 22, 10, 0));
        report.setReportType(ReportType.UPDATE);
        report.setTitle(title);
        report.setExecutiveSummary("Zusammenfassung für " + title);
        report.setFullText("Volltext für " + title);
        report.setRating(Rating.BUY);
        report.setPreviousRating(Rating.HOLD);
        report.setRatingChanged(true);
        report.setTargetPrice(new BigDecimal("120.50"));
        report.setPreviousTarget(new BigDecimal("100.00"));
        report.setCurrentPrice(new BigDecimal("105.00"));
        report.setImpliedUpside(new BigDecimal("14.76"));
        report.setRiskLevel(RiskLevel.MEDIUM);
        report.setInvestmentCatalysts(List.of("Starkes Q4", "Neue Märkte"));
        report.setKeyRisks(List.of("Regulierung", "Währungsrisiko"));
        report.setTags(List.of("Healthcare", "Swiss"));
        return report;
    }

    @Nested
    @DisplayName("getAllReports()")
    class GetAllReports {

        @Test
        @DisplayName("Gibt alle Reports zurück")
        void shouldReturnAllReports() {
            // Arrange
            var reports = List.of(
                    createTestReport(1L, "Nestlé Analyse"),
                    createTestReport(2L, "Novartis Update")
            );
            when(reportRepository.findAll()).thenReturn(reports);

            // Act
            var result = reportService.getAllReports();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("Nestlé Analyse");
            assertThat(result.get(1).getTitle()).isEqualTo("Novartis Update");
            verify(reportRepository).findAll();
        }

        @Test
        @DisplayName("Gibt leere Liste zurück wenn keine Reports existieren")
        void shouldReturnEmptyListWhenNoReports() {
            when(reportRepository.findAll()).thenReturn(List.of());

            var result = reportService.getAllReports();

            assertThat(result).isEmpty();
            verify(reportRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getReportById()")
    class GetReportById {

        @Test
        @DisplayName("Gibt Report zurück wenn ID existiert")
        void shouldReturnReportWhenFound() {
            var report = createTestReport(1L, "Nestlé Analyse");
            when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

            var result = reportService.getReportById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("Nestlé Analyse");
            assertThat(result.get().getRating()).isEqualTo(Rating.BUY);
            verify(reportRepository).findById(1L);
        }

        @Test
        @DisplayName("Gibt leeres Optional zurück wenn ID nicht existiert")
        void shouldReturnEmptyWhenNotFound() {
            when(reportRepository.findById(999L)).thenReturn(Optional.empty());

            var result = reportService.getReportById(999L);

            assertThat(result).isEmpty();
            verify(reportRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("getReportsByAnalyst()")
    class GetReportsByAnalyst {

        @Test
        @DisplayName("Gibt Reports eines Analysten zurück")
        void shouldReturnReportsForAnalyst() {
            var reports = List.of(createTestReport(1L, "Report 1"));
            when(reportRepository.findByAnalystId(1L)).thenReturn(reports);

            var result = reportService.getReportsByAnalyst(1L);

            assertThat(result).hasSize(1);
            verify(reportRepository).findByAnalystId(1L);
        }
    }

    @Nested
    @DisplayName("getReportsBySecurity()")
    class GetReportsBySecurity {

        @Test
        @DisplayName("Gibt Reports einer Wertschrift zurück")
        void shouldReturnReportsForSecurity() {
            var reports = List.of(
                    createTestReport(1L, "Report 1"),
                    createTestReport(2L, "Report 2")
            );
            when(reportRepository.findBySecurityId(1L)).thenReturn(reports);

            var result = reportService.getReportsBySecurity(1L);

            assertThat(result).hasSize(2);
            verify(reportRepository).findBySecurityId(1L);
        }
    }

    @Nested
    @DisplayName("createReport()")
    class CreateReport {

        @Test
        @DisplayName("Erstellt einen neuen Report")
        void shouldCreateReport() {
            var inputReport = createTestReport(null, "Neuer Report");
            var savedReport = createTestReport(1L, "Neuer Report");
            when(reportRepository.save(any(ResearchReport.class))).thenReturn(savedReport);

            var result = reportService.createReport(inputReport);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Neuer Report");
            verify(reportRepository).save(inputReport);
        }
    }

    @Nested
    @DisplayName("updateReport()")
    class UpdateReport {

        @Test
        @DisplayName("Aktualisiert einen existierenden Report")
        void shouldUpdateExistingReport() {
            var existingReport = createTestReport(1L, "Alt");
            var updatedReport = createTestReport(1L, "Aktualisiert");
            when(reportRepository.findById(1L)).thenReturn(Optional.of(existingReport));
            when(reportRepository.save(any(ResearchReport.class))).thenReturn(updatedReport);

            var input = createTestReport(null, "Aktualisiert");
            var result = reportService.updateReport(1L, input);

            assertThat(result.getTitle()).isEqualTo("Aktualisiert");
            verify(reportRepository).findById(1L);
            verify(reportRepository).save(input);
        }

        @Test
        @DisplayName("Wirft Exception wenn Report nicht existiert")
        void shouldThrowWhenReportNotFound() {
            when(reportRepository.findById(999L)).thenReturn(Optional.empty());

            var input = createTestReport(null, "Irrelevant");

            assertThatThrownBy(() -> reportService.updateReport(999L, input))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Report")
                    .hasMessageContaining("999");

            verify(reportRepository).findById(999L);
            verify(reportRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteReport()")
    class DeleteReport {

        @Test
        @DisplayName("Löscht einen existierenden Report")
        void shouldDeleteExistingReport() {
            var report = createTestReport(1L, "Zu löschen");
            when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

            reportService.deleteReport(1L);

            verify(reportRepository).findById(1L);
            verify(reportRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Wirft Exception wenn Report nicht existiert")
        void shouldThrowWhenReportNotFound() {
            when(reportRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reportService.deleteReport(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Report")
                    .hasMessageContaining("999");

            verify(reportRepository).findById(999L);
            verify(reportRepository, never()).deleteById(any());
        }
    }
}
