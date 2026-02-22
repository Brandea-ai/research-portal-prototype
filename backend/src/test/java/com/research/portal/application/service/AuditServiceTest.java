package com.research.portal.application.service;

import com.research.portal.domain.model.AuditAction;
import com.research.portal.domain.model.AuditLog;
import com.research.portal.domain.port.out.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für den AuditService.
 * Testet alle Use-Case-Methoden mit gemocktem AuditLogRepository.
 *
 * Verifiziert die FINMA-konforme Protokollierung aller Aktionen.
 */
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService(auditLogRepository);
    }

    /**
     * Erstellt einen Test-AuditLog-Eintrag.
     */
    private AuditLog createTestAuditLog(Long id, AuditAction action, String entityType, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setId(id);
        log.setTimestamp(LocalDateTime.of(2026, 2, 23, 10, 0));
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setUserId("system");
        log.setUserName("System Administrator");
        log.setUserRole("ADMIN");
        log.setDetails(details);
        return log;
    }

    @Nested
    @DisplayName("log()")
    class Log {

        @Test
        @DisplayName("Erstellt einen neuen Audit-Eintrag mit korrekten Werten")
        void shouldCreateAuditLogEntry() {
            // Arrange
            var savedLog = createTestAuditLog(1L, AuditAction.CREATE, "REPORT", 5L, "Report erstellt");
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedLog);

            // Act
            auditService.log(AuditAction.CREATE, "REPORT", 5L, "Report erstellt");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());

            AuditLog captured = captor.getValue();
            assertThat(captured.getAction()).isEqualTo(AuditAction.CREATE);
            assertThat(captured.getEntityType()).isEqualTo("REPORT");
            assertThat(captured.getEntityId()).isEqualTo(5L);
            assertThat(captured.getDetails()).isEqualTo("Report erstellt");
            assertThat(captured.getTimestamp()).isNotNull();
            assertThat(captured.getUserId()).isEqualTo("system");
            assertThat(captured.getUserName()).isEqualTo("System Administrator");
            assertThat(captured.getUserRole()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Setzt einen Zeitstempel beim Protokollieren")
        void shouldSetTimestamp() {
            // Arrange
            var savedLog = createTestAuditLog(1L, AuditAction.UPDATE, "REPORT", 1L, "Update");
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedLog);

            // Act
            auditService.log(AuditAction.UPDATE, "REPORT", 1L, "Update");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());
            assertThat(captor.getValue().getTimestamp()).isNotNull();
            assertThat(captor.getValue().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Unterstützt null als entityId (z.B. bei LOGIN)")
        void shouldHandleNullEntityId() {
            // Arrange
            var savedLog = createTestAuditLog(1L, AuditAction.LOGIN, "SESSION", null, "Benutzer angemeldet");
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(savedLog);

            // Act
            auditService.log(AuditAction.LOGIN, "SESSION", null, "Benutzer angemeldet");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());
            assertThat(captor.getValue().getEntityId()).isNull();
        }
    }

    @Nested
    @DisplayName("getRecentLogs()")
    class GetRecentLogs {

        @Test
        @DisplayName("Gibt die letzten Audit-Einträge zurück")
        void shouldReturnRecentLogs() {
            // Arrange
            var logs = List.of(
                    createTestAuditLog(2L, AuditAction.CREATE, "REPORT", 2L, "Report 2 erstellt"),
                    createTestAuditLog(1L, AuditAction.CREATE, "REPORT", 1L, "Report 1 erstellt")
            );
            when(auditLogRepository.findRecentLogs(50)).thenReturn(logs);

            // Act
            var result = auditService.getRecentLogs(50);

            // Assert
            assertThat(result).hasSize(2);
            verify(auditLogRepository).findRecentLogs(50);
        }

        @Test
        @DisplayName("Verwendet Standardwert 50 bei ungültigem Limit")
        void shouldUseDefaultLimitWhenInvalid() {
            // Arrange
            when(auditLogRepository.findRecentLogs(50)).thenReturn(List.of());

            // Act
            auditService.getRecentLogs(0);

            // Assert
            verify(auditLogRepository).findRecentLogs(50);
        }

        @Test
        @DisplayName("Verwendet Standardwert 50 bei negativem Limit")
        void shouldUseDefaultLimitWhenNegative() {
            // Arrange
            when(auditLogRepository.findRecentLogs(50)).thenReturn(List.of());

            // Act
            auditService.getRecentLogs(-5);

            // Assert
            verify(auditLogRepository).findRecentLogs(50);
        }

        @Test
        @DisplayName("Gibt leere Liste zurück wenn keine Einträge existieren")
        void shouldReturnEmptyListWhenNoLogs() {
            // Arrange
            when(auditLogRepository.findRecentLogs(anyInt())).thenReturn(List.of());

            // Act
            var result = auditService.getRecentLogs(10);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getLogsByEntity()")
    class GetLogsByEntity {

        @Test
        @DisplayName("Gibt Audit-Einträge für eine bestimmte Entität zurück")
        void shouldReturnLogsForEntity() {
            // Arrange
            var logs = List.of(
                    createTestAuditLog(3L, AuditAction.UPDATE, "REPORT", 1L, "Rating geändert"),
                    createTestAuditLog(1L, AuditAction.CREATE, "REPORT", 1L, "Report erstellt")
            );
            when(auditLogRepository.findByEntityTypeAndEntityId("REPORT", 1L)).thenReturn(logs);

            // Act
            var result = auditService.getLogsByEntity("REPORT", 1L);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(log -> log.getEntityType().equals("REPORT"));
            assertThat(result).allMatch(log -> log.getEntityId().equals(1L));
            verify(auditLogRepository).findByEntityTypeAndEntityId("REPORT", 1L);
        }

        @Test
        @DisplayName("Gibt leere Liste zurück wenn keine Einträge für die Entität existieren")
        void shouldReturnEmptyListWhenNoLogsForEntity() {
            // Arrange
            when(auditLogRepository.findByEntityTypeAndEntityId("SECURITY", 999L)).thenReturn(List.of());

            // Act
            var result = auditService.getLogsByEntity("SECURITY", 999L);

            // Assert
            assertThat(result).isEmpty();
            verify(auditLogRepository).findByEntityTypeAndEntityId("SECURITY", 999L);
        }
    }
}
