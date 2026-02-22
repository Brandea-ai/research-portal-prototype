package com.research.portal.domain.port.in;

import com.research.portal.domain.model.ResearchReport;

/**
 * Use Case: Reports erstellen, aktualisieren, löschen.
 * Trennung von Lese- und Schreib-Operationen (CQRS-Prinzip).
 */
public interface ManageReportUseCase {

    ResearchReport createReport(ResearchReport report);

    ResearchReport updateReport(Long id, ResearchReport report);

    void deleteReport(Long id);
}
