package com.research.portal.config;

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
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit-Tests fuer {@link DatabaseHealthIndicator}.
 * Prueft Health-Status basierend auf Datenbankverbindung.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DatabaseHealthIndicator")
class DatabaseHealthIndicatorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private JpaReportRepository reportRepository;

    @Mock
    private JpaAnalystRepository analystRepository;

    @Mock
    private JpaSecurityRepository securityRepository;

    private DatabaseHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        healthIndicator = new DatabaseHealthIndicator(
                jdbcTemplate,
                reportRepository,
                analystRepository,
                securityRepository
        );
    }

    @Nested
    @DisplayName("health() gibt UP zurueck wenn DB erreichbar ist")
    class WhenDatabaseIsReachable {

        @Test
        @DisplayName("Status ist UP wenn SELECT 1 erfolgreich")
        void shouldReturnHealthUp_whenDatabaseReachable() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);
            when(reportRepository.count()).thenReturn(10L);
            when(analystRepository.count()).thenReturn(5L);
            when(securityRepository.count()).thenReturn(8L);

            Health health = healthIndicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.UP);
        }

        @Test
        @DisplayName("Details enthalten db.status reachable")
        void shouldIncludeDbStatusReachable_whenUp() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);
            when(reportRepository.count()).thenReturn(10L);
            when(analystRepository.count()).thenReturn(5L);
            when(securityRepository.count()).thenReturn(8L);

            Health health = healthIndicator.health();

            assertThat(health.getDetails()).containsEntry("db.status", "reachable");
        }

        @Test
        @DisplayName("Details enthalten korrekten report.count")
        void shouldIncludeReportCount_whenUp() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);
            when(reportRepository.count()).thenReturn(42L);
            when(analystRepository.count()).thenReturn(5L);
            when(securityRepository.count()).thenReturn(8L);

            Health health = healthIndicator.health();

            assertThat(health.getDetails()).containsEntry("report.count", 42L);
        }

        @Test
        @DisplayName("Details enthalten analyst.count und security.count")
        void shouldIncludeAnalystAndSecurityCount_whenUp() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);
            when(reportRepository.count()).thenReturn(10L);
            when(analystRepository.count()).thenReturn(3L);
            when(securityRepository.count()).thenReturn(7L);

            Health health = healthIndicator.health();

            assertThat(health.getDetails()).containsEntry("analyst.count", 3L);
            assertThat(health.getDetails()).containsEntry("security.count", 7L);
        }

        @Test
        @DisplayName("Details enthalten db.type H2")
        void shouldIncludeDbType_whenUp() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);
            when(reportRepository.count()).thenReturn(0L);
            when(analystRepository.count()).thenReturn(0L);
            when(securityRepository.count()).thenReturn(0L);

            Health health = healthIndicator.health();

            assertThat(health.getDetails()).containsEntry("db.type", "H2");
        }
    }

    @Nested
    @DisplayName("health() gibt DOWN zurueck wenn DB nicht erreichbar ist")
    class WhenDatabaseIsUnreachable {

        @Test
        @DisplayName("Status ist DOWN wenn jdbcTemplate eine Exception wirft")
        void shouldReturnHealthDown_whenDatabaseThrowsException() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                    .thenThrow(new RuntimeException("Verbindung zur Datenbank fehlgeschlagen"));

            Health health = healthIndicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        }

        @Test
        @DisplayName("Details enthalten db.status unreachable")
        void shouldIncludeDbStatusUnreachable_whenDown() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                    .thenThrow(new RuntimeException("Connection refused"));

            Health health = healthIndicator.health();

            assertThat(health.getDetails()).containsEntry("db.status", "unreachable");
        }

        @Test
        @DisplayName("Details enthalten Fehlermeldung")
        void shouldIncludeErrorMessage_whenDown() {
            String errorMessage = "Verbindung zur Datenbank fehlgeschlagen";
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                    .thenThrow(new RuntimeException(errorMessage));

            Health health = healthIndicator.health();

            assertThat(health.getDetails()).containsEntry("error", errorMessage);
        }
    }
}
