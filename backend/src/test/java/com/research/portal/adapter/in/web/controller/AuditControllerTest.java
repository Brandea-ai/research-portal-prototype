package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.mapper.AuditLogApiMapper;
import com.research.portal.domain.model.AuditAction;
import com.research.portal.domain.model.AuditLog;
import com.research.portal.domain.port.in.AuditLogUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-Tests für den AuditController.
 * Testet die REST-Endpunkte für den Audit-Trail.
 */
@WebMvcTest(AuditController.class)
@Import(AuditLogApiMapper.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditLogUseCase auditLogUseCase;

    /**
     * Erstellt einen Test-AuditLog-Eintrag.
     */
    private AuditLog createTestAuditLog(Long id, AuditAction action, String entityType,
                                         Long entityId, String details) {
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
        log.setIpAddress("10.0.1.42");
        return log;
    }

    @Nested
    @DisplayName("GET /api/audit")
    class GetAuditLogs {

        @Test
        @DisplayName("Gibt die letzten Audit-Einträge zurück")
        void shouldReturnRecentAuditLogs() throws Exception {
            // Arrange
            var logs = List.of(
                    createTestAuditLog(2L, AuditAction.UPDATE, "REPORT", 1L, "Report aktualisiert"),
                    createTestAuditLog(1L, AuditAction.CREATE, "REPORT", 1L, "Report erstellt")
            );
            when(auditLogUseCase.getRecentLogs(50)).thenReturn(logs);

            // Act & Assert
            mockMvc.perform(get("/api/audit"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(2))
                    .andExpect(jsonPath("$[0].action").value("UPDATE"))
                    .andExpect(jsonPath("$[0].entityType").value("REPORT"))
                    .andExpect(jsonPath("$[0].details").value("Report aktualisiert"))
                    .andExpect(jsonPath("$[1].id").value(1))
                    .andExpect(jsonPath("$[1].action").value("CREATE"));

            verify(auditLogUseCase).getRecentLogs(50);
        }

        @Test
        @DisplayName("Unterstützt benutzerdefiniertes Limit")
        void shouldSupportCustomLimit() throws Exception {
            // Arrange
            when(auditLogUseCase.getRecentLogs(10)).thenReturn(List.of());

            // Act & Assert
            mockMvc.perform(get("/api/audit").param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(auditLogUseCase).getRecentLogs(10);
        }

        @Test
        @DisplayName("Filtert nach Entitätstyp und ID")
        void shouldFilterByEntityTypeAndId() throws Exception {
            // Arrange
            var logs = List.of(
                    createTestAuditLog(1L, AuditAction.CREATE, "REPORT", 5L, "Report erstellt")
            );
            when(auditLogUseCase.getLogsByEntity("REPORT", 5L)).thenReturn(logs);

            // Act & Assert
            mockMvc.perform(get("/api/audit")
                            .param("entityType", "REPORT")
                            .param("entityId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].entityType").value("REPORT"))
                    .andExpect(jsonPath("$[0].entityId").value(5));

            verify(auditLogUseCase).getLogsByEntity("REPORT", 5L);
            verify(auditLogUseCase, never()).getRecentLogs(anyInt());
        }
    }

    @Nested
    @DisplayName("GET /api/audit/report/{id}")
    class GetAuditTrailForReport {

        @Test
        @DisplayName("Gibt den Audit-Trail für einen Report zurück")
        void shouldReturnAuditTrailForReport() throws Exception {
            // Arrange
            var logs = List.of(
                    createTestAuditLog(3L, AuditAction.UPDATE, "REPORT", 1L, "Rating geändert"),
                    createTestAuditLog(2L, AuditAction.VIEW, "REPORT", 1L, "Report angesehen"),
                    createTestAuditLog(1L, AuditAction.CREATE, "REPORT", 1L, "Report erstellt")
            );
            when(auditLogUseCase.getLogsByEntity("REPORT", 1L)).thenReturn(logs);

            // Act & Assert
            mockMvc.perform(get("/api/audit/report/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].action").value("UPDATE"))
                    .andExpect(jsonPath("$[1].action").value("VIEW"))
                    .andExpect(jsonPath("$[2].action").value("CREATE"));

            verify(auditLogUseCase).getLogsByEntity("REPORT", 1L);
        }

        @Test
        @DisplayName("Gibt leere Liste zurück wenn kein Trail existiert")
        void shouldReturnEmptyListWhenNoTrail() throws Exception {
            // Arrange
            when(auditLogUseCase.getLogsByEntity("REPORT", 999L)).thenReturn(List.of());

            // Act & Assert
            mockMvc.perform(get("/api/audit/report/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(auditLogUseCase).getLogsByEntity("REPORT", 999L);
        }
    }
}
