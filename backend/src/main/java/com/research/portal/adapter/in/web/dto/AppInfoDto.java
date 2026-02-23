package com.research.portal.adapter.in.web.dto;

/**
 * DTO fuer Applikations-Informationen.
 * Wird von GET /api/info zurueckgegeben.
 */
public class AppInfoDto {

    private final String name;
    private final String version;
    private final String description;
    private final String status;
    private final long uptimeSeconds;
    private final String javaVersion;

    public AppInfoDto(String name,
                      String version,
                      String description,
                      String status,
                      long uptimeSeconds,
                      String javaVersion) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.status = status;
        this.uptimeSeconds = uptimeSeconds;
        this.javaVersion = javaVersion;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public String getJavaVersion() {
        return javaVersion;
    }
}
