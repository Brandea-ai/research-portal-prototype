package com.research.portal.application.aspect;

import com.research.portal.domain.model.AuditAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Markiert eine Methode als auditierbar.
 *
 * Methoden mit dieser Annotation werden automatisch vom
 * {@link AuditAspect} protokolliert. Die Annotation definiert
 * den Aktionstyp und den Entitätstyp für den Audit-Eintrag.
 *
 * Beispiel:
 * <pre>
 * {@literal @}Audited(action = AuditAction.CREATE, entityType = "REPORT")
 * public ReportDto createReport(CreateReportRequest request) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

    /**
     * Die Art der Aktion, die protokolliert wird.
     */
    AuditAction action();

    /**
     * Der Typ der betroffenen Entität (z.B. "REPORT", "SECURITY", "ANALYST").
     */
    String entityType();
}
