package com.research.portal.domain.port.in;

import com.research.portal.domain.model.ResearchReport;

import java.util.List;
import java.util.Optional;

/**
 * Use Case: Reports abrufen.
 * Der Controller ruft dieses Interface auf,
 * der Service implementiert die Logik.
 */
public interface GetReportsUseCase {

    List<ResearchReport> getAllReports();

    Optional<ResearchReport> getReportById(Long id);

    List<ResearchReport> getReportsByAnalyst(Long analystId);

    List<ResearchReport> getReportsBySecurity(Long securityId);
}
