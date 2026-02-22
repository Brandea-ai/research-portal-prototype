# Status: Research Portal Prototype 1.0

> Dynamischer Tracker. Wird nach jeder Arbeitseinheit aktualisiert.

**Letzte Aktualisierung:** 22.02.2026
**Gesamtfortschritt:** 0/48 Tasks (0%)

---

## Phasen-Übersicht

| Phase | Tasks | Erledigt | Status |
|-------|-------|----------|--------|
| P0: Fundament | 7 | 0/7 | [ ] Offen |
| P1: Kernfeatures | 7 | 0/7 | [ ] Wartet auf P0 |
| P2: Professionalität | 13 | 0/13 | [ ] Wartet auf P1 |

---

## P0: Fundament (0/7)

| Nr | Task | Status | Abhängig von | Bemerkung |
|----|------|--------|-------------|-----------|
| P0-01 | Projektinitialisierung | [ ] | Nichts | Nächster Schritt |
| P0-02 | Design-System CSS | [ ] | P0-01 | |
| P0-03 | Backend Domain-Modell | [ ] | P0-01 | |
| P0-04 | Backend Persistence Layer | [ ] | P0-03 | |
| P0-05 | Backend REST API | [ ] | P0-04 | |
| P0-06 | Frontend Grundstruktur | [ ] | P0-01, P0-02 | |
| P0-07 | Login-Seite | [ ] | P0-06 | |

## P1: Kernfeatures (0/7)

| Nr | Task | Status | Abhängig von | Bemerkung |
|----|------|--------|-------------|-----------|
| P1-08 | Dashboard View | [ ] | P0-05, P0-06 | |
| P1-09 | Research Reports Tabelle | [ ] | P0-05, P0-06 | |
| P1-10 | Report Detail-Ansicht | [ ] | P1-09 | |
| P1-11 | Securities View | [ ] | P0-05, P0-06 | |
| P1-12 | Report CRUD | [ ] | P1-09 | |
| P1-13 | RxJS State Management | [ ] | P1-09 | |
| P1-14 | Responsive Design | [ ] | P1-08 bis P1-12 | |

## P2: Professionalität (0/13)

| Nr | Task | Status | Abhängig von | Bemerkung |
|----|------|--------|-------------|-----------|
| P2-15 | Cypress E2E Tests | [ ] | P1 komplett | |
| P2-16 | Backend Unit Tests | [ ] | P0-05 | Kann parallel zu P1 |
| P2-17 | XPath Report Import | [ ] | P0-05 | |
| P2-18 | CI/CD Pipeline (Jenkins, GitLab, Harness, Nexus) | [ ] | P0-01 | Kann parallel |
| P2-19 | Audit Trail | [ ] | P1-12 | |
| P2-20 | API Dokumentation | [ ] | P0-05 | |
| P2-21 | Performance Optimierung | [ ] | P1 komplett | |
| P2-22 | Datenexport (CSV/Excel) | [ ] | P1-09 | Neu: Selbst-Audit |
| P2-23 | PDF Report Export | [ ] | P1-10 | Neu: Selbst-Audit |
| P2-24 | Session Timeout + Security | [ ] | P0-07 | Neu: Selbst-Audit |
| P2-25 | Keyboard Shortcuts | [ ] | P1-09 | Neu: Selbst-Audit |
| P2-26 | i18n Vorbereitung | [ ] | P0-06 | Neu: Selbst-Audit |
| P2-27 | Git-Flow Branching | [ ] | P0-01 | Neu: Selbst-Audit |

---

## Änderungs-Log

| Datum | Änderung | Begründung |
|-------|----------|------------|
| 22.02.2026 | 6 neue P2-Tasks (P2-22 bis P2-27), P2-18 erweitert | Selbst-Audit: Skills-Lücken geschlossen |
| 22.02.2026 | Abhängigkeiten-Spalte hinzugefügt | Systemdenken: interdependente Tasks sichtbar machen |
| 22.02.2026 | Initiale Erstellung, 42 Tasks definiert | Projektstart |
