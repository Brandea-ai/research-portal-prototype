package com.research.portal.application.service;

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

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

/**
 * Unit Tests fuer den ScheduledTaskService.
 * Testet die Datenvalidierungslogik mit gemockten Repositories.
 *
 * Prueft alle vier Validierungskategorien:
 * - Verwaiste Reports (ohne gueltigen Analyst)
 * - Ungueltige Security-Referenzen
 * - Negative MarketCaps
 * - Ungueltige Accuracy-Werte
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduledTaskService")
class ScheduledTaskServiceTest {

    @Mock
    private JpaReportRepository reportRepository;

    @Mock
    private JpaAnalystRepository analystRepository;

    @Mock
    private JpaSecurityRepository securityRepository;

    private ScheduledTaskService scheduledTaskService;

    @BeforeEach
    void setUp() {
        scheduledTaskService = new ScheduledTaskService(
                reportRepository, analystRepository, securityRepository
        );
    }

    /**
     * Erstellt einen Test-Report mit den angegebenen IDs.
     */
    private ResearchReportEntity createReport(Long id, Long analystId, Long securityId) {
        ResearchReportEntity report = new ResearchReportEntity();
        report.setId(id);
        report.setAnalystId(analystId);
        report.setSecurityId(securityId);
        report.setTitle("Test Report " + id);
        report.setReportType("UPDATE");
        report.setRating("BUY");
        return report;
    }

    /**
     * Erstellt einen Test-Analysten mit der angegebenen Accuracy.
     */
    private AnalystEntity createAnalyst(Long id, String name, double accuracy) {
        AnalystEntity analyst = new AnalystEntity();
        analyst.setId(id);
        analyst.setName(name);
        analyst.setEmail(name.toLowerCase().replace(" ", ".") + "@bank.ch");
        analyst.setAccuracy12m(accuracy);
        return analyst;
    }

    /**
     * Erstellt eine Test-Wertschrift mit der angegebenen MarketCap.
     */
    private SecurityEntity createSecurity(Long id, String ticker, BigDecimal marketCap) {
        SecurityEntity security = new SecurityEntity();
        security.setId(id);
        security.setTicker(ticker);
        security.setName("Test Security " + ticker);
        security.setAssetClass("EQUITY");
        security.setMarketCap(marketCap);
        return security;
    }

    @Nested
    @DisplayName("runValidation()")
    class RunValidation {

        @Test
        @DisplayName("Erkennt Reports ohne gueltigen Analyst")
        void shouldDetectOrphanedReports() {
            // Arrange: Report verweist auf Analyst-ID 99, der nicht existiert
            when(reportRepository.findAll()).thenReturn(List.of(
                    createReport(1L, 99L, 1L)
            ));
            when(analystRepository.findAll()).thenReturn(List.of(
                    createAnalyst(1L, "Sarah Mueller", 85.0)
            ));
            when(securityRepository.findAll()).thenReturn(List.of(
                    createSecurity(1L, "NESN", new BigDecimal("300000000000"))
            ));

            // Act
            DataValidationResult result = scheduledTaskService.runValidation();

            // Assert
            assertThat(result.getOrphanedReports()).isEqualTo(1);
        }

        @Test
        @DisplayName("Erkennt Reports ohne gueltige Security-Referenz")
        void shouldDetectInvalidSecurityRefs() {
            // Arrange: Report verweist auf Security-ID 99, die nicht existiert
            when(reportRepository.findAll()).thenReturn(List.of(
                    createReport(1L, 1L, 99L)
            ));
            when(analystRepository.findAll()).thenReturn(List.of(
                    createAnalyst(1L, "Thomas Braun", 78.5)
            ));
            when(securityRepository.findAll()).thenReturn(List.of(
                    createSecurity(1L, "NOVN", new BigDecimal("200000000000"))
            ));

            // Act
            DataValidationResult result = scheduledTaskService.runValidation();

            // Assert
            assertThat(result.getInvalidSecurityRefs()).isEqualTo(1);
        }

        @Test
        @DisplayName("Erkennt negative MarketCaps")
        void shouldDetectNegativeMarketCaps() {
            // Arrange
            when(reportRepository.findAll()).thenReturn(List.of());
            when(analystRepository.findAll()).thenReturn(List.of());
            when(securityRepository.findAll()).thenReturn(List.of(
                    createSecurity(1L, "NESN", new BigDecimal("300000000000")),
                    createSecurity(2L, "BAD1", new BigDecimal("-5000000")),
                    createSecurity(3L, "BAD2", new BigDecimal("-1"))
            ));

            // Act
            DataValidationResult result = scheduledTaskService.runValidation();

            // Assert
            assertThat(result.getNegativeMarketCaps()).isEqualTo(2);
        }

        @Test
        @DisplayName("Erkennt ungueltige Accuracy-Werte")
        void shouldDetectInvalidAccuracies() {
            // Arrange
            when(reportRepository.findAll()).thenReturn(List.of());
            when(analystRepository.findAll()).thenReturn(List.of(
                    createAnalyst(1L, "Max Mustermann", 85.0),     // gueltig
                    createAnalyst(2L, "Negativ Analyst", -5.0),    // ungueltig
                    createAnalyst(3L, "Ueber Hundert", 105.0)      // ungueltig
            ));
            when(securityRepository.findAll()).thenReturn(List.of());

            // Act
            DataValidationResult result = scheduledTaskService.runValidation();

            // Assert
            assertThat(result.getInvalidAccuracies()).isEqualTo(2);
        }

        @Test
        @DisplayName("Gibt 'keine Probleme' bei korrekten Daten zurueck")
        void shouldReturnNoIssuesForValidData() {
            // Arrange: Alle Referenzen sind gueltig
            when(reportRepository.findAll()).thenReturn(List.of(
                    createReport(1L, 1L, 1L),
                    createReport(2L, 2L, 2L)
            ));
            when(analystRepository.findAll()).thenReturn(List.of(
                    createAnalyst(1L, "Anna Weber", 92.0),
                    createAnalyst(2L, "Peter Fischer", 88.5)
            ));
            when(securityRepository.findAll()).thenReturn(List.of(
                    createSecurity(1L, "UBSG", new BigDecimal("75000000000")),
                    createSecurity(2L, "ZURN", new BigDecimal("50000000000"))
            ));

            // Act
            DataValidationResult result = scheduledTaskService.runValidation();

            // Assert
            assertThat(result.getOrphanedReports()).isZero();
            assertThat(result.getInvalidSecurityRefs()).isZero();
            assertThat(result.getNegativeMarketCaps()).isZero();
            assertThat(result.getInvalidAccuracies()).isZero();
            assertThat(result.hasIssues()).isFalse();
        }
    }

    @Nested
    @DisplayName("DataValidationResult")
    class ValidationResultTests {

        @Test
        @DisplayName("hasIssues() ist true wenn Probleme existieren")
        void hasIssuesShouldBeTrueWhenProblemsExist() {
            // Arrange: Report ohne gueltigen Analyst
            when(reportRepository.findAll()).thenReturn(List.of(
                    createReport(1L, 99L, 1L)
            ));
            when(analystRepository.findAll()).thenReturn(List.of(
                    createAnalyst(1L, "Lisa Keller", 90.0)
            ));
            when(securityRepository.findAll()).thenReturn(List.of(
                    createSecurity(1L, "ROG", new BigDecimal("240000000000"))
            ));

            // Act
            DataValidationResult result = scheduledTaskService.runValidation();

            // Assert
            assertThat(result.hasIssues()).isTrue();
        }

        @Test
        @DisplayName("totalIssues() summiert alle Probleme korrekt")
        void totalIssuesShouldSumAllProblems() {
            // Arrange: 1 verwaister Report + 1 ungueltige Security-Ref + 1 negative MarketCap + 1 ungueltige Accuracy
            when(reportRepository.findAll()).thenReturn(List.of(
                    createReport(1L, 99L, 1L),  // verwaist (Analyst 99 existiert nicht)
                    createReport(2L, 1L, 99L)   // ungueltige Security-Ref (Security 99 existiert nicht)
            ));
            when(analystRepository.findAll()).thenReturn(List.of(
                    createAnalyst(1L, "Kai Brunner", -10.0)  // ungueltige Accuracy
            ));
            when(securityRepository.findAll()).thenReturn(List.of(
                    createSecurity(1L, "SREN", new BigDecimal("-999"))  // negative MarketCap
            ));

            // Act
            DataValidationResult result = scheduledTaskService.runValidation();

            // Assert: 1 orphaned + 1 invalid security ref + 1 negative marketCap + 1 invalid accuracy = 4
            assertThat(result.totalIssues()).isEqualTo(4);
            assertThat(result.getOrphanedReports()).isEqualTo(1);
            assertThat(result.getInvalidSecurityRefs()).isEqualTo(1);
            assertThat(result.getNegativeMarketCaps()).isEqualTo(1);
            assertThat(result.getInvalidAccuracies()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("validateData()")
    class ValidateData {

        @Test
        @DisplayName("lastValidationRun wird gesetzt nach validateData()")
        void shouldSetLastValidationRunAfterValidation() {
            // Arrange
            when(reportRepository.findAll()).thenReturn(List.of());
            when(analystRepository.findAll()).thenReturn(List.of());
            when(securityRepository.findAll()).thenReturn(List.of());

            assertThat(scheduledTaskService.getLastValidationRun()).isNull();

            // Act
            scheduledTaskService.validateData();

            // Assert
            assertThat(scheduledTaskService.getLastValidationRun()).isNotNull();
            assertThat(scheduledTaskService.getLastValidationResult()).isNotNull();
        }
    }

    @Nested
    @DisplayName("logSystemStats()")
    class LogSystemStats {

        @Test
        @DisplayName("Wirft keine Exception")
        void shouldNotThrowException() {
            // Arrange
            when(reportRepository.count()).thenReturn(25L);
            when(analystRepository.count()).thenReturn(8L);
            when(securityRepository.count()).thenReturn(10L);

            // Act & Assert
            assertThatCode(() -> scheduledTaskService.logSystemStats())
                    .doesNotThrowAnyException();
        }
    }
}
