package com.research.portal.application.aspect;

import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.domain.model.AuditAction;
import com.research.portal.domain.port.in.AuditLogUseCase;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

/**
 * Unit Tests für den AuditAspect.
 *
 * Verifiziert, dass der AOP-Aspekt die korrekten Audit-Einträge
 * für Report-Operationen erstellt.
 */
@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @Mock
    private AuditLogUseCase auditLogUseCase;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    private AuditAspect auditAspect;

    @BeforeEach
    void setUp() {
        auditAspect = new AuditAspect(auditLogUseCase);
    }

    /**
     * Erstellt ein Test-ReportDto.
     */
    private ReportDto createTestReportDto(Long id, String title) {
        ReportDto dto = new ReportDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setReportType("UPDATE");
        dto.setRating("BUY");
        return dto;
    }

    @Nested
    @DisplayName("auditReportCreate()")
    class AuditReportCreate {

        @Test
        @DisplayName("Protokolliert die Erstellung eines Reports via ResponseEntity")
        void shouldLogReportCreationFromResponseEntity() {
            // Arrange
            ReportDto dto = createTestReportDto(5L, "Nestlé Update");
            ResponseEntity<ReportDto> response = ResponseEntity.status(HttpStatus.CREATED).body(dto);

            // Act
            auditAspect.auditReportCreate(joinPoint, response);

            // Assert
            verify(auditLogUseCase).log(
                    eq(AuditAction.CREATE),
                    eq("REPORT"),
                    eq(5L),
                    contains("Nestlé Update")
            );
        }

        @Test
        @DisplayName("Protokolliert die Erstellung eines Reports als direktes DTO")
        void shouldLogReportCreationFromDto() {
            // Arrange
            ReportDto dto = createTestReportDto(3L, "Roche Flash");

            // Act
            auditAspect.auditReportCreate(joinPoint, dto);

            // Assert
            verify(auditLogUseCase).log(
                    eq(AuditAction.CREATE),
                    eq("REPORT"),
                    eq(3L),
                    contains("Roche Flash")
            );
        }

        @Test
        @DisplayName("Keine Ausnahme bei unerwartetem Rückgabetyp")
        void shouldNotThrowOnUnexpectedReturnType() {
            // Act & Assert (kein Fehler erwartet)
            auditAspect.auditReportCreate(joinPoint, "unexpected");

            verify(auditLogUseCase, never()).log(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("auditReportUpdate()")
    class AuditReportUpdate {

        @Test
        @DisplayName("Protokolliert die Aktualisierung eines Reports")
        void shouldLogReportUpdate() {
            // Arrange
            ReportDto dto = createTestReportDto(1L, "UBS Rating Change");

            // Act
            auditAspect.auditReportUpdate(joinPoint, dto);

            // Assert
            verify(auditLogUseCase).log(
                    eq(AuditAction.UPDATE),
                    eq("REPORT"),
                    eq(1L),
                    contains("UBS Rating Change")
            );
        }
    }

    @Nested
    @DisplayName("auditReportDelete()")
    class AuditReportDelete {

        @Test
        @DisplayName("Protokolliert das Löschen eines Reports")
        void shouldLogReportDeletion() {
            // Arrange
            when(joinPoint.getArgs()).thenReturn(new Object[]{7L});

            // Act
            auditAspect.auditReportDelete(joinPoint, ResponseEntity.noContent().build());

            // Assert
            verify(auditLogUseCase).log(
                    eq(AuditAction.DELETE),
                    eq("REPORT"),
                    eq(7L),
                    contains("7")
            );
        }
    }

    @Nested
    @DisplayName("auditReportView()")
    class AuditReportView {

        @Test
        @DisplayName("Protokolliert den Abruf eines Reports")
        void shouldLogReportView() {
            // Arrange
            when(joinPoint.getArgs()).thenReturn(new Object[]{3L});

            // Act
            auditAspect.auditReportView(joinPoint, ResponseEntity.ok(createTestReportDto(3L, "Test")));

            // Assert
            verify(auditLogUseCase).log(
                    eq(AuditAction.VIEW),
                    eq("REPORT"),
                    eq(3L),
                    contains("3")
            );
        }
    }

    @Nested
    @DisplayName("auditAnnotatedMethod()")
    class AuditAnnotatedMethod {

        @Test
        @DisplayName("Protokolliert @Audited-annotierte Methoden")
        void shouldLogAnnotatedMethod() {
            // Arrange
            Audited audited = mock(Audited.class);
            when(audited.action()).thenReturn(AuditAction.IMPORT);
            when(audited.entityType()).thenReturn("REPORT");
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getName()).thenReturn("importXmlReport");

            // Act
            auditAspect.auditAnnotatedMethod(joinPoint, audited, "result");

            // Assert
            verify(auditLogUseCase).log(
                    eq(AuditAction.IMPORT),
                    eq("REPORT"),
                    isNull(),
                    contains("importXmlReport")
            );
        }
    }
}
