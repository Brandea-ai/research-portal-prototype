package com.research.portal.domain.model;

/**
 * Art des Research Reports.
 * INITIATION = erste Analyse einer Wertschrift.
 * FLASH = kurzfristiges Update bei Marktereignis.
 * DEEP_DIVE = umfassende Analyse mit Finanzmodell.
 */
public enum ReportType {
    INITIATION,
    UPDATE,
    QUARTERLY,
    FLASH,
    DEEP_DIVE,
    CREDIT
}
