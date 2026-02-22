package com.research.portal.domain.model;

/**
 * Aufzählung aller auditierbaren Aktionen im System.
 *
 * Jede Benutzeraktion, die regulatorisch relevant ist (FINMA-Compliance),
 * wird über einen dieser Aktionstypen protokolliert.
 */
public enum AuditAction {

    /** Neue Entität erstellt (z.B. neuer Research Report) */
    CREATE,

    /** Bestehende Entität aktualisiert (z.B. Rating-Änderung) */
    UPDATE,

    /** Entität gelöscht */
    DELETE,

    /** Entität angesehen (z.B. Report-Detail aufgerufen) */
    VIEW,

    /** Daten exportiert (z.B. PDF-Export) */
    EXPORT,

    /** Benutzer hat sich angemeldet */
    LOGIN,

    /** Benutzer hat sich abgemeldet */
    LOGOUT,

    /** Daten importiert (z.B. XML-Import) */
    IMPORT
}
