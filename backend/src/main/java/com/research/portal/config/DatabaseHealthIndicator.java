package com.research.portal.config;

import com.research.portal.adapter.out.persistence.repository.JpaAnalystRepository;
import com.research.portal.adapter.out.persistence.repository.JpaReportRepository;
import com.research.portal.adapter.out.persistence.repository.JpaSecurityRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Custom Health Indicator fuer die Datenbankverbindung.
 * Prueft ob die Datenbank erreichbar ist und liefert Statistiken.
 * Demonstriert DevOps-Kompetenz und Monitoring-Readiness.
 */
@Component("database")
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private final JpaReportRepository reportRepository;
    private final JpaAnalystRepository analystRepository;
    private final JpaSecurityRepository securityRepository;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate,
                                   JpaReportRepository reportRepository,
                                   JpaAnalystRepository analystRepository,
                                   JpaSecurityRepository securityRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.reportRepository = reportRepository;
        this.analystRepository = analystRepository;
        this.securityRepository = securityRepository;
    }

    @Override
    public Health health() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            long reportCount = reportRepository.count();
            long analystCount = analystRepository.count();
            long securityCount = securityRepository.count();

            return Health.up()
                    .withDetail("db.type", "H2")
                    .withDetail("db.status", "reachable")
                    .withDetail("report.count", reportCount)
                    .withDetail("analyst.count", analystCount)
                    .withDetail("security.count", securityCount)
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("db.status", "unreachable")
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
