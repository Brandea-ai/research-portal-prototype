package com.research.portal.adapter.in.web.dto;

/**
 * DTO fuer Applikations-Statistiken.
 * Wird von GET /api/info/stats zurueckgegeben.
 */
public class AppStatsDto {

    private final long reportCount;
    private final long analystCount;
    private final long securityCount;

    public AppStatsDto(long reportCount, long analystCount, long securityCount) {
        this.reportCount = reportCount;
        this.analystCount = analystCount;
        this.securityCount = securityCount;
    }

    public long getReportCount() {
        return reportCount;
    }

    public long getAnalystCount() {
        return analystCount;
    }

    public long getSecurityCount() {
        return securityCount;
    }
}
