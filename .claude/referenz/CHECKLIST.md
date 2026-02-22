# Phasen-Checkliste

> Überblick über den Fortschritt pro Phase. Referenz: `_claude-tasks/RESEARCH-PORTAL_TODOs.md` für Details.

---

## Phase 0: Fundament (7 Tasks) — ABGESCHLOSSEN
- [x] P0-01: Projektinitialisierung (GitHub, Angular, Spring Boot, Docker)
- [x] P0-02: Design-System CSS (Farben, Fonts, Reset, Breakpoints)
- [x] P0-03: Backend Domain-Modell (Entities, Enums, Ports)
- [x] P0-04: Backend Persistence (JPA, H2, Flyway, Demo-Daten)
- [x] P0-05: Backend REST API (Controllers, DTOs, CORS, Error Handler)
- [x] P0-06: Frontend Grundstruktur (Shell, Routing, Guards, Interceptors)
- [x] P0-07: Login-Seite (Form, Validation, Mock-Auth, Redirect)

**Gate:** `ng build` + `mvn package` fehlerfrei, Login funktioniert

---

## Phase 1: Kernfeatures (7 Tasks) — IN ARBEIT (4/7)
- [x] P1-08: Dashboard View (KPIs, letzte Reports, Live-Updates)
- [x] P1-09: Research Reports Tabelle (PrimeNG, Filter, Sort, Suche)
- [x] P1-10: Report Detail-Ansicht (Split-View, Rating, Finanzdaten)
- [x] P1-11: Securities View (Wertschriften-Tabelle, Verlinkung)
- [ ] P1-12: Report CRUD (Create, Edit, Delete, Validation)
- [ ] P1-13: RxJS State Management (BehaviorSubject, reaktive Filter)
- [ ] P1-14: Responsive Design (Sidebar, Tabelle, Detail, Mobile)

**Gate:** Alle Features funktional, responsive, keine Konsolen-Fehler

---

## Phase 2: Professionalität (13 Tasks)
- [ ] P2-15: Cypress E2E Tests (5+ Tests grün)
- [ ] P2-16: Backend Unit Tests (>70% Coverage)
- [ ] P2-17: XPath Report Import (XML Parser, Beispiel-XML)
- [ ] P2-18: CI/CD Pipeline (Jenkins, GitLab, Harness, Nexus)
- [ ] P2-19: Audit Trail (Activity Log, AOP Logging)
- [ ] P2-20: API Dokumentation (Springdoc, Swagger UI)
- [ ] P2-21: Performance Optimierung (Lazy Load, OnPush, Bundle Size)
- [ ] P2-22: Datenexport (CSV/Excel)
- [ ] P2-23: PDF Report Export
- [ ] P2-24: Session Timeout + Security Headers
- [ ] P2-25: Keyboard Shortcuts (Bloomberg-Style)
- [ ] P2-26: i18n Vorbereitung (DE/FR/EN)
- [ ] P2-27: Git-Flow Branching Strategie

**Gate:** Tests grün, Pipeline valide, Swagger erreichbar, Exports funktionieren

---

## Querverweise

- Task-Details: `_claude-tasks/RESEARCH-PORTAL_TODOs.md`
- Fortschritt: `_claude-tasks/STATUS.md`
- Anforderungen: `.claude/referenz/REQUIREMENTS.md`
- Entscheidungen: `.claude/referenz/DECISION-LOG.md`
