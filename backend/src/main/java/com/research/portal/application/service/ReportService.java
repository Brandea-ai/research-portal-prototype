package com.research.portal.application.service;

import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.model.ResearchReport;
import com.research.portal.domain.port.in.GetReportsUseCase;
import com.research.portal.domain.port.in.ManageReportUseCase;
import com.research.portal.domain.port.out.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService implements GetReportsUseCase, ManageReportUseCase {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<ResearchReport> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Optional<ResearchReport> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    public List<ResearchReport> getReportsByAnalyst(Long analystId) {
        return reportRepository.findByAnalystId(analystId);
    }

    @Override
    public List<ResearchReport> getReportsBySecurity(Long securityId) {
        return reportRepository.findBySecurityId(securityId);
    }

    @Override
    public ResearchReport createReport(ResearchReport report) {
        return reportRepository.save(report);
    }

    @Override
    public ResearchReport updateReport(Long id, ResearchReport report) {
        if (reportRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Report", id);
        }
        report.setId(id);
        return reportRepository.save(report);
    }

    @Override
    public void deleteReport(Long id) {
        if (reportRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Report", id);
        }
        reportRepository.deleteById(id);
    }
}
