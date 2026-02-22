package com.research.portal.adapter.in.web.controller;

import com.research.portal.adapter.in.web.dto.CreateReportRequest;
import com.research.portal.adapter.in.web.dto.ReportDto;
import com.research.portal.adapter.in.web.mapper.ReportApiMapper;
import com.research.portal.application.exception.ResourceNotFoundException;
import com.research.portal.domain.port.in.GetReportsUseCase;
import com.research.portal.domain.port.in.ManageReportUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final GetReportsUseCase getReports;
    private final ManageReportUseCase manageReport;
    private final ReportApiMapper mapper;

    public ReportController(GetReportsUseCase getReports,
                            ManageReportUseCase manageReport,
                            ReportApiMapper mapper) {
        this.getReports = getReports;
        this.manageReport = manageReport;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ReportDto> getAllReports() {
        return getReports.getAllReports().stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReportById(@PathVariable Long id) {
        return getReports.getReportById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Report", id));
    }

    @GetMapping(params = "analystId")
    public List<ReportDto> getReportsByAnalyst(@RequestParam Long analystId) {
        return getReports.getReportsByAnalyst(analystId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping(params = "securityId")
    public List<ReportDto> getReportsBySecurity(@RequestParam Long securityId) {
        return getReports.getReportsBySecurity(securityId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ReportDto> createReport(@Valid @RequestBody CreateReportRequest request) {
        var domain = mapper.toDomain(request);
        var saved = manageReport.createReport(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ReportDto updateReport(@PathVariable Long id,
                                   @Valid @RequestBody CreateReportRequest request) {
        var domain = mapper.toDomain(request);
        var updated = manageReport.updateReport(id, domain);
        return mapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        manageReport.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
