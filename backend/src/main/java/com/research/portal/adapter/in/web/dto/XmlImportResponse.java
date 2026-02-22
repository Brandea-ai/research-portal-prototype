package com.research.portal.adapter.in.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Antwort-DTO für den XML-Import-Vorgang.
 * Enthält Statusinformationen und die importierten Reports.
 */
public class XmlImportResponse {

    private int importedCount;
    private String status;
    private LocalDateTime timestamp;
    private List<ReportDto> reports;

    public XmlImportResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public XmlImportResponse(int importedCount, String status, List<ReportDto> reports) {
        this();
        this.importedCount = importedCount;
        this.status = status;
        this.reports = reports;
    }

    public int getImportedCount() { return importedCount; }
    public void setImportedCount(int importedCount) { this.importedCount = importedCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<ReportDto> getReports() { return reports; }
    public void setReports(List<ReportDto> reports) { this.reports = reports; }
}
