# Research Portal Prototype: Alle Tasks

> DER HEILIGE GRAL. Alle Tasks mit Prioritäten, Abhängigkeiten und Definition of Done.
> Dynamisches Dokument: Tasks können sich ändern, aber Änderungen werden mit Datum dokumentiert.

**Erstellt:** 22.02.2026
**Letzte Aktualisierung:** 22.02.2026
**Gesamtstatus:** 9/48 (18%)

---

## P0: Fundament (MUST HAVE, vor allem anderen)

> Ohne diese Tasks existiert kein lauffähiger Prototyp.

### P0-01: Projektinitialisierung ✓
- [x] GitHub Repository erstellen (öffentlich)
- [x] Angular 17+ Projekt scaffolden (Standalone, strict)
- [x] Spring Boot 3 + Java 17 Projekt scaffolden (Maven)
- [x] Docker Compose für Frontend + Backend
- [x] .gitignore, .editorconfig, prettier config
**DoD:** `ng serve` + `mvn spring-boot:run` laufen beide ohne Fehler — **ERREICHT 22.02.2026**

### P0-02: Design-System CSS Grundlage ✓
- [x] CSS Custom Properties definieren (Farben, Fonts, Spacing)
- [x] Inter + JetBrains Mono Fonts einbinden
- [x] Global Reset/Normalize
- [x] Responsive Breakpoints (Desktop > 1024, Tablet 768-1024, Mobile < 768)
- [x] Keine Icons, nur Typografie und Struktur
**DoD:** Design-System Variablen in einer CSS-Datei, dokumentiert — **ERREICHT 22.02.2026**

### P0-03: Backend Domain-Modell ✓
- [x] Domain Entities: Analyst, Security, ResearchReport, RatingHistory, FinancialEstimates
- [x] Value Objects: Rating (Enum), ReportType (Enum), AssetClass (Enum), RiskLevel (Enum)
- [x] Ports definieren (Use-Case Interfaces + Repository Interfaces)
- [x] Keine Spring-Annotationen in Domain-Schicht
**DoD:** Domain-Package kompiliert ohne Framework-Imports — **ERREICHT 22.02.2026**

### P0-04: Backend Persistence Layer ✓
- [x] JPA Entities (Adapter-Schicht, NICHT Domain)
- [x] JPA Repositories
- [x] Mapper: JPA Entity <-> Domain Model
- [x] H2 Konfiguration (application-local.yml)
- [x] Flyway Migrations (Schema) → data.sql statt Flyway (defer-datasource-init)
- [x] Demo-Daten (data.sql mit Schweizer Wertschriften)
**DoD:** `mvn test` grün, H2 Console zeigt Daten — **ERREICHT 22.02.2026**

### P0-05: Backend REST API ✓
- [x] ResearchReportController (GET all, GET by id, POST, PUT, DELETE)
- [x] SecurityController (GET all, GET by id)
- [x] AnalystController (GET all, GET by id)
- [x] DTOs für alle Responses
- [x] CORS Konfiguration für Angular Dev Server
- [x] Global Exception Handler
**DoD:** Alle Endpoints via curl/Postman testbar, korrekte HTTP Status Codes — **ERREICHT 22.02.2026**

### P0-06: Frontend Grundstruktur ✓
- [x] App Shell: Sidebar + Main + Detail Panel (3-Zonen-Layout)
- [x] Routing: /login, /dashboard, /research, /securities
- [x] Lazy-loaded Feature Modules
- [x] Auth Guard (Basis, JWT-Konzept) → Functional Guard mit CanActivateFn
- [x] HTTP Interceptor für API Base URL → environment.apiUrl in Services
**DoD:** Navigation zwischen Views funktioniert, Layout responsive — **ERREICHT 22.02.2026**

### P0-07: Login-Seite ✓
- [x] Minimalistisches Login-Formular (Reactive Form)
- [x] Validation (required, min-length)
- [x] Mock-Authentication (hardcoded Token) → sessionStorage
- [x] Redirect nach Dashboard
- [x] Design: Schwarz/Weiß, Accent nur auf Submit-Button
**DoD:** Login funktioniert, Token wird gespeichert, Redirect klappt — **ERREICHT 22.02.2026**

---

## P1: Kernfeatures (SHOULD HAVE, Prototyp-Substanz)

> Diese Tasks machen den Prototyp beeindruckend.

### P1-08: Dashboard View ✓
- [x] KPI-Cards: Reports heute, offene Analysen, Coverage-Quote
- [x] Letzte Reports als Liste (5 aktuellste) → computed() mit Datum-Sortierung
- [x] RxJS Observables für simulierte Live-Updates → Signals + computed()
- [x] Custom CSS Cards (kein Material/PrimeNG Theme)
- [x] Top Analysten: Top 3 nach accuracy12m (NEU)
- [x] Coverage-Übersicht: Sektoren, Ticker, Avg. Accuracy (NEU)
**DoD:** Dashboard zeigt reale Daten aus dem Backend, Updates sichtbar — **ERREICHT 22.02.2026**

### P1-09: Research Reports Tabelle ✓
- [x] PrimeNG p-table mit Custom CSS Override → Native HTML-Tabelle mit Custom CSS
- [x] Spalten: Date, Ticker, Company, Type, Rating, Target, Upside, Analyst
- [x] Sortierung (Multi-Column, Unicode-Pfeile)
- [x] Filter (Asset Class, Rating, Report Type)
- [x] Suchfeld (Volltext über Titel + Ticker) → Debounced via RxJS Subject (300ms)
- [ ] Virtual Scrolling für große Datenmengen → Nicht nötig bei 10 Demo-Datensätzen
- [ ] Frozen erste Spalte (Datum/Ticker) → Nicht nötig bei aktueller Breite
**DoD:** Tabelle zeigt alle Reports, Filter/Sort/Suche funktionieren — **ERREICHT 22.02.2026**

### P1-10: Report Detail-Ansicht
- [ ] Split-View: Tabelle links, Detail rechts
- [ ] Rating mit Farbcodierung (BUY=Accent, SELL=Rot, HOLD=Grau)
- [ ] Kursziel + Upside-Berechnung
- [ ] Executive Summary
- [ ] Investmentthese (Catalysts, Key Risks)
- [ ] Financial Estimates Tabelle
**DoD:** Klick auf Report-Zeile zeigt Detail im rechten Panel

### P1-11: Securities/Wertschriften View
- [ ] Datenintensive Tabelle (alle Schweizer Wertschriften)
- [ ] Spalten: Ticker, Name, Sektor, Kurs, MarketCap, letzte Empfehlung
- [ ] Pagination (25 pro Seite)
- [ ] Klick auf Wertschrift zeigt zugehörige Reports
**DoD:** Wertschriften-Tabelle mit Verlinkung zu Reports

### P1-12: Research Report CRUD
- [ ] Neuen Report erstellen (Reactive Form)
- [ ] Report bearbeiten
- [ ] Report löschen (mit Bestätigung)
- [ ] Analyst + Security Auswahl (Dropdowns)
- [ ] Rating + Kursziel Eingabe
- [ ] Validation aller Felder
**DoD:** Vollständiger CRUD-Zyklus funktioniert end-to-end

### P1-13: RxJS State Management
- [ ] ReportStateService mit BehaviorSubject
- [ ] Automatische Aktualisierung nach CRUD
- [ ] Filter-State reaktiv
- [ ] Debounced Suchfeld
- [ ] Error Handling mit catchError
**DoD:** State-Änderungen propagieren korrekt durch die App

### P1-14: Responsive Design
- [ ] Sidebar: Collapsible auf Tablet, Hidden auf Mobile
- [ ] Detail-Panel: Full-Screen Modal auf Mobile
- [ ] Tabelle: Horizontal scrollbar auf Mobile
- [ ] Touch-freundliche Interaktionen
- [ ] Breakpoints: 1024px, 768px
**DoD:** Alle Views auf Desktop, Tablet, Mobile funktional

---

## P2: Professionalität (NICE TO HAVE, zeigt Seniorität)

> Diese Tasks heben den Prototyp von "gut" auf "beeindruckend".

### P2-15: Cypress E2E Tests
- [ ] Login Flow Test
- [ ] Report CRUD Test
- [ ] Filter + Sortierung Test
- [ ] Navigation Test
- [ ] Responsive Test (Viewport-Wechsel)
**DoD:** 5+ E2E Tests laufen grün

### P2-16: Backend Unit Tests (TDD)
- [ ] Service Layer Tests (JUnit 5 + Mockito)
- [ ] Controller Tests (MockMvc)
- [ ] Repository Tests (DataJpaTest)
- [ ] Domain Model Tests (reine Logik)
**DoD:** >70% Code Coverage Backend

### P2-17: XPath Report Import
- [ ] XML-Parser Service
- [ ] Beispiel-XML Research Report
- [ ] XPath Queries für Datenextraktion
- [ ] Import-Endpoint
**DoD:** XML-Datei wird korrekt in ResearchReport gemappt

### P2-18: CI/CD Pipeline (Jenkins, GitLab, Harness, Nexus)
- [ ] Jenkinsfile (Build, Test, Deploy Stages)
- [ ] .gitlab-ci.yml Alternative
- [ ] Harness Pipeline Config (harness.yaml) vorbereitet
- [ ] Maven settings.xml mit Nexus Repository Manager Config
- [ ] Dockerfile Frontend (nginx)
- [ ] Dockerfile Backend (JDK 17)
- [ ] docker-compose.yml (beide Services)
**DoD:** Pipeline-Dateien syntaktisch korrekt, Docker Build funktioniert, Nexus-Config dokumentiert

### P2-19: Audit Trail Konzept
- [ ] Activity Log Entity
- [ ] AOP-basiertes Logging (Spring AOP)
- [ ] Wer hat wann welchen Report erstellt/geändert/publiziert
- [ ] FINMA-Compliance Kommentar im Code
**DoD:** Änderungen an Reports werden in Audit-Log gespeichert

### P2-20: API Dokumentation
- [ ] Springdoc OpenAPI (Swagger UI)
- [ ] Alle Endpoints dokumentiert
- [ ] Request/Response Beispiele
- [ ] Error Responses
**DoD:** Swagger UI unter /swagger-ui.html erreichbar

### P2-21: Performance Optimierung
- [ ] Angular Lazy Loading verifizieren
- [ ] OnPush Change Detection überall
- [ ] Bundle Size Analyse (ng build --stats-json)
- [ ] Backend: Connection Pool Konfiguration
**DoD:** Bundle Size dokumentiert, Lazy Routes bestätigt

### P2-22: Datenexport (CSV/Excel)
- [ ] Export-Button in Report-Tabelle
- [ ] CSV-Download (Frontend-seitig)
- [ ] Excel-Export (Backend: Apache POI oder Frontend: SheetJS)
- [ ] Export beinhaltet aktive Filter
**DoD:** Gefilterte Tabelle lässt sich als CSV/Excel herunterladen

### P2-23: PDF Report Export
- [ ] Report-Detail als PDF generieren
- [ ] Formatierung: Rating, Kursziel, Summary, Financial Estimates
- [ ] Download-Button im Detail-Panel
**DoD:** Einzelner Research Report als PDF exportierbar

### P2-24: Session Timeout und Security
- [ ] Auto-Logout nach Inaktivität (konfigurierbar, Default: 15 Min)
- [ ] Warnung 2 Minuten vor Timeout
- [ ] Redirect zu Login nach Timeout
- [ ] Security Headers im Backend (CSP, HSTS, X-Frame-Options)
**DoD:** Session läuft nach Inaktivität aus, Security Headers gesetzt

### P2-25: Keyboard Shortcuts
- [ ] `/` für Suchfeld-Fokus
- [ ] `j`/`k` für Tabellenzeilen Navigation (wie Bloomberg)
- [ ] `Enter` für Report öffnen
- [ ] `Esc` für Detail-Panel schließen
- [ ] Shortcut-Übersicht via `?`
**DoD:** 5 Shortcuts funktionieren, Übersicht aufrufbar

### P2-26: i18n Vorbereitung
- [ ] Angular i18n oder ngx-translate Grundstruktur
- [ ] Deutsche Texte als Default-Locale
- [ ] Mindestens ein Label in DE/FR/EN als Proof-of-Concept
- [ ] Locale-Switch Konzept vorbereitet (nicht voll implementiert)
**DoD:** Architektur für Mehrsprachigkeit steht, ein Beispiel funktioniert

### P2-27: Git-Flow Branching Strategie
- [ ] Dokumentierte Branch-Konventionen (main, develop, feature/*, release/*, hotfix/*)
- [ ] CONTRIBUTING.md mit Branch-Anleitung
- [ ] Pre-commit Hook Konzept (.husky oder .githooks)
- [ ] Merge-Request Template (.gitlab/merge_request_templates/)
**DoD:** Branch-Strategie dokumentiert und im Repo verankert

---

## Änderungs-Log

| Datum | Task | Änderung | Begründung |
|-------|------|----------|------------|
| 22.02.2026 | P2-22 bis P2-27 | 6 neue Tasks hinzugefügt | Selbst-Audit: Fehlende Skills (Nexus, Harness, Export, i18n, Security, Git-Flow) |
| 22.02.2026 | P2-18 | Harness + Nexus ergänzt | Selbst-Audit: Beide in Stellenanzeige gefordert |
| 22.02.2026 | Alle | Initiale Erstellung | Projektstart |
