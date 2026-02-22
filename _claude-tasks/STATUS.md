# Status: Research Portal Prototype 1.0

> Dynamischer Tracker. Wird nach jeder Arbeitseinheit aktualisiert.

**Letzte Aktualisierung:** 22.02.2026
**Gesamtfortschritt:** 7/48 Tasks (14%)

---

## Phasen-Übersicht

| Phase | Tasks | Erledigt | Status |
|-------|-------|----------|--------|
| P0: Fundament | 7 | 7/7 | [x] Abgeschlossen |
| P1: Kernfeatures | 7 | 0/7 | [ ] Wartet auf P0 |
| P2: Professionalität | 13 | 0/13 | [ ] Wartet auf P1 |

---

## P0: Fundament (0/7)

| Nr | Task | Status | Abhängig von | Bemerkung |
|----|------|--------|-------------|-----------|
| P0-01 | Projektinitialisierung | [x] | Nichts | Erledigt 22.02.2026 |
| P0-02 | Design-System CSS | [x] | P0-01 | Erledigt 22.02.2026 |
| P0-03 | Backend Domain-Modell | [x] | P0-01 | Erledigt 22.02.2026 |
| P0-04 | Backend Persistence Layer | [x] | P0-03 | Erledigt 22.02.2026 |
| P0-05 | Backend REST API | [x] | P0-04 | Erledigt 22.02.2026 |
| P0-06 | Frontend Grundstruktur | [x] | P0-01, P0-02 | Erledigt 22.02.2026 |
| P0-07 | Login-Seite | [x] | P0-06 | Erledigt 22.02.2026 |

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
| 22.02.2026 | P0-07 abgeschlossen: Login-Seite, Auth-Service, Guard, Logout, Rollen-Badge | Build OK, P0 komplett |
| 22.02.2026 | P0-06 abgeschlossen: 3 Models, 3 Services, 2 Layout-Komp., 4 Feature-Komp., App Shell, 4 Lazy Routes, 264 KB Bundle | Build OK, 0 Fehler |
| 22.02.2026 | P0-05 abgeschlossen: 5 DTOs, 3 Mapper, 3 Services, 3 Controller, Exception Handler, 12 Endpoints live | Alle Tests bestanden |
| 22.02.2026 | P0-04 abgeschlossen: 3 JPA Entities, 3 Repos, 3 Mapper, 3 Adapters, 25 Demo-Datensätze | BUILD SUCCESS, App startet |
| 22.02.2026 | P0-03 abgeschlossen: 5 Entities, 4 Enums, 7 Ports, 0 Spring-Imports in Domain | BUILD SUCCESS, 18 Files |
| 22.02.2026 | P0-02 abgeschlossen: Design-System CSS (12 Farben, Fonts, Spacing, Buttons, Forms) | Build verifiziert, 5.12 KB CSS |
| 22.02.2026 | P0-01 abgeschlossen: Angular 21 + Spring Boot 3.5 + Docker | Builds verifiziert, GitHub gepusht |
| 22.02.2026 | 6 neue P2-Tasks (P2-22 bis P2-27), P2-18 erweitert | Selbst-Audit: Skills-Lücken geschlossen |
| 22.02.2026 | Abhängigkeiten-Spalte hinzugefügt | Systemdenken: interdependente Tasks sichtbar machen |
| 22.02.2026 | Initiale Erstellung, 42 Tasks definiert | Projektstart |
