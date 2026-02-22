package com.research.portal.application.aspect;

import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.domain.model.AuditAction;
import com.research.portal.domain.port.in.AuditLogUseCase;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * AOP-Aspekt für automatisches Audit-Logging.
 *
 * Fängt Methodenaufrufe auf dem ReportController ab und protokolliert
 * CREATE, UPDATE und DELETE Aktionen automatisch im Audit-Trail.
 * Dies demonstriert AOP-Kompetenz und FINMA-Compliance-Bewusstsein.
 *
 * Der Aspekt arbeitet mit {@code @AfterReturning}, sodass nur
 * erfolgreiche Aktionen protokolliert werden.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditLogUseCase auditLogUseCase;

    public AuditAspect(AuditLogUseCase auditLogUseCase) {
        this.auditLogUseCase = auditLogUseCase;
    }

    /**
     * Pointcut für alle Methoden im ReportController.
     */
    @Pointcut("execution(* com.research.portal.adapter.in.web.controller.ReportController.*(..))")
    public void reportControllerMethods() {
        // Pointcut-Definition
    }

    /**
     * Protokolliert die Erstellung eines neuen Reports.
     *
     * @param joinPoint der abgefangene Methodenaufruf
     * @param result    das Ergebnis der Methode (ResponseEntity mit ReportDto)
     */
    @AfterReturning(
            pointcut = "execution(* com.research.portal.adapter.in.web.controller.ReportController.createReport(..))",
            returning = "result"
    )
    public void auditReportCreate(JoinPoint joinPoint, Object result) {
        try {
            ReportDto report = extractReportDto(result);
            if (report != null) {
                auditLogUseCase.log(
                        AuditAction.CREATE,
                        "REPORT",
                        report.getId(),
                        "Report '" + report.getTitle() + "' erstellt (Typ: " + report.getReportType() + ")"
                );
            }
        } catch (Exception e) {
            log.warn("Fehler beim Audit-Logging für createReport: {}", e.getMessage());
        }
    }

    /**
     * Protokolliert die Aktualisierung eines bestehenden Reports.
     *
     * @param joinPoint der abgefangene Methodenaufruf
     * @param result    das Ergebnis der Methode (ReportDto)
     */
    @AfterReturning(
            pointcut = "execution(* com.research.portal.adapter.in.web.controller.ReportController.updateReport(..))",
            returning = "result"
    )
    public void auditReportUpdate(JoinPoint joinPoint, Object result) {
        try {
            if (result instanceof ReportDto report) {
                auditLogUseCase.log(
                        AuditAction.UPDATE,
                        "REPORT",
                        report.getId(),
                        "Report '" + report.getTitle() + "' aktualisiert (Rating: " + report.getRating() + ")"
                );
            }
        } catch (Exception e) {
            log.warn("Fehler beim Audit-Logging für updateReport: {}", e.getMessage());
        }
    }

    /**
     * Protokolliert das Löschen eines Reports.
     *
     * @param joinPoint der abgefangene Methodenaufruf
     * @param result    das Ergebnis der Methode (ResponseEntity<Void>)
     */
    @AfterReturning(
            pointcut = "execution(* com.research.portal.adapter.in.web.controller.ReportController.deleteReport(..))",
            returning = "result"
    )
    public void auditReportDelete(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Long reportId) {
                auditLogUseCase.log(
                        AuditAction.DELETE,
                        "REPORT",
                        reportId,
                        "Report mit ID " + reportId + " gelöscht"
                );
            }
        } catch (Exception e) {
            log.warn("Fehler beim Audit-Logging für deleteReport: {}", e.getMessage());
        }
    }

    /**
     * Protokolliert den Abruf eines einzelnen Reports (VIEW).
     *
     * @param joinPoint der abgefangene Methodenaufruf
     * @param result    das Ergebnis der Methode
     */
    @AfterReturning(
            pointcut = "execution(* com.research.portal.adapter.in.web.controller.ReportController.getReportById(..))",
            returning = "result"
    )
    public void auditReportView(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Long reportId) {
                auditLogUseCase.log(
                        AuditAction.VIEW,
                        "REPORT",
                        reportId,
                        "Report mit ID " + reportId + " angesehen"
                );
            }
        } catch (Exception e) {
            log.warn("Fehler beim Audit-Logging für getReportById: {}", e.getMessage());
        }
    }

    /**
     * Protokolliert Methoden, die mit @Audited annotiert sind.
     *
     * @param joinPoint der abgefangene Methodenaufruf
     * @param audited   die Audited-Annotation mit Metadaten
     * @param result    das Ergebnis der Methode
     */
    @AfterReturning(
            pointcut = "@annotation(audited)",
            returning = "result"
    )
    public void auditAnnotatedMethod(JoinPoint joinPoint, Audited audited, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            auditLogUseCase.log(
                    audited.action(),
                    audited.entityType(),
                    null,
                    "Methode '" + methodName + "' ausgeführt"
            );
        } catch (Exception e) {
            log.warn("Fehler beim Audit-Logging für @Audited Methode: {}", e.getMessage());
        }
    }

    /**
     * Extrahiert ein ReportDto aus dem Rückgabewert einer Controller-Methode.
     * Unterstützt sowohl direkte ReportDto-Rückgaben als auch ResponseEntity-Wrapper.
     */
    private ReportDto extractReportDto(Object result) {
        if (result instanceof ReportDto dto) {
            return dto;
        }
        if (result instanceof ResponseEntity<?> response && response.getBody() instanceof ReportDto dto) {
            return dto;
        }
        return null;
    }
}
