package com.research.portal.domain.port.out;

import com.research.portal.domain.model.ResearchReport;

import java.util.List;
import java.util.Optional;

/**
 * Port (Ausgangs-Schnittstelle) für Research Reports.
 * Die Domain definiert WAS sie braucht.
 * Der Persistence-Adapter entscheidet WIE er es liefert.
 */
public interface ReportRepository {

    List<ResearchReport> findAll();

    Optional<ResearchReport> findById(Long id);

    List<ResearchReport> findByAnalystId(Long analystId);

    List<ResearchReport> findBySecurityId(Long securityId);

    ResearchReport save(ResearchReport report);

    void deleteById(Long id);
}
