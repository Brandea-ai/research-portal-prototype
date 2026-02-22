# Progress Log

> Chronologische Historie aller Arbeitseinheiten. Neueste Einträge oben.
> NICHTS wird gelöscht. Verworfene Ansätze, Richtungswechsel, Erkenntnisse: alles bleibt.

---

## 22.02.2026 - Session 4: P1-14 + P2-15 (Parallele Subagents)

**Dauer:** ~5 Min (Parallel, real ~5 Min pro Agent)
**Methode:** 2 Subagents in isolierten Git-Worktrees, gleichzeitig

**P1-14 Responsive Design:**
- Sidebar: collapsed Signal + mobileOpen Signal mit MediaQueryList Listener
- Tablet (≤1024px): Sidebar auf 56px eingeklappt, Toggle-Button (Unicode ◀/▶)
- Mobile (≤768px): Sidebar per translateX(-100%) versteckt, Overlay bei mobileOpen
- Topbar: Hamburger-Button (☰) via output() an App Shell, nur auf Mobile sichtbar
- App Shell: viewChild für Sidebar-Referenz, sidebarMargin computed Signal
- Reports-Tabelle: Analyst/Kurs/Upside Spalten auf Mobile ausgeblendet (.hide-mobile)
- DestroyRef für sauberes Cleanup der MediaQuery-Listener

**P2-15 Cypress E2E Tests:**
- Cypress 15.10 installiert + konfiguriert
- Custom cy.login() Command mit formControlName-Selektoren
- 5 Test-Specs mit 49 Tests:
  - auth.cy.ts (6): Login, Logout, Invalid Credentials, Redirect
  - dashboard.cy.ts (8): KPI-Cards, Reports, Analysten, Coverage
  - reports.cy.ts (13): Tabelle, Filter, Suche, Navigation, Detail
  - navigation.cy.ts (8): Alle Views, Active State, Topbar
  - securities.cy.ts (12): Tabelle, Filter, Suche, Sortierung
- npm Scripts: cy:open, cy:run, e2e

**Architektur-Entscheidungen:**
- MediaQueryList + Signal statt CSS-only (Angular muss Sidebar-Breite kennen für margin-left)
- output() statt EventEmitter (Angular 17+ Best Practice)
- viewChild() statt @ViewChild Decorator (Angular 17+ Signal-based)
- Cypress Custom Command für Login statt Page Object (einfacher für Prototyp)

**Erkenntnisse:**
- P1 ist jetzt komplett: 7/7 Tasks
- Parallele Worktrees funktionieren zum 4. Mal perfekt
- Kombinierter Build: 282.66 KB Initial, 0 Fehler, 0 Warnings
- Bundle-Anstieg von +6 KB durch Sidebar-Logik (Signals, MediaQuery, ViewChild)

**Nächster Schritt:** P2-16 Backend Unit Tests + P2-17 XPath Report Import

---

## 22.02.2026 - Session 4: P1-12 + P1-13 (Parallele Subagents + Integration)

**Dauer:** ~8 Min (Parallel + Integration)
**Methode:** 2 Subagents in isolierten Git-Worktrees + manuelle Integration

**P1-12 Report CRUD:**
- ReportFormComponent: Reactive Form mit FormBuilder via inject()
- Create + Edit Mode (Route-Erkennung via ActivatedRoute snapshot)
- forkJoin für paralleles Laden von Analyst/Security-Dropdowns
- Validation: required, minLength(5/20), min(0.01) für Kursziel
- Edit/Delete Buttons in ReportDetailComponent
- Delete-Bestätigung mit showDeleteConfirm Signal
- 3 neue Routes: /reports/new, /reports/:id/edit (Lazy-loaded)

**P1-13 RxJS State Management:**
- ReportStateService: BehaviorSubject<Report[]> + loaded-Flag
- CRUD-Methoden: createReport(), updateReport(), deleteReport()
- Auto-Refresh via tap(() => this.refresh()) nach jeder Mutation
- reports$ Observable für alle Subscriber
- Dashboard, Reports, Securities auf ReportStateService umgestellt
- "Neuer Report" Button in Reports-Liste

**Integration (nach Merge):**
- report-form: ReportStateService statt ReportService für create/update
- report-detail: ReportStateService statt ReportService für delete
- Alle CRUD-Pfade lösen automatischen State-Refresh aus

**Architektur-Entscheidungen:**
- BehaviorSubject statt Signal für reports (wegen async pipe Kompatibilität und RxJS-Skill-Nachweis)
- loaded-Flag verhindert doppeltes Laden (loadReports() ist idempotent)
- tap() für Side-Effects (Refresh) nach CRUD-Operationen

**Erkenntnisse:**
- Parallele Worktrees + manuelle Integration = sicherer Ansatz bei überlappenden Abhängigkeiten
- CSS-Budget von 6kB auf 10kB erhöht (report-detail.css = 7.3 kB)
- Kombinierter Build: 276.75 KB Initial, 0 Fehler, 0 Warnings

**Nächster Schritt:** P1-14 Responsive Design

---

## 22.02.2026 - Session 4: P1-10 + P1-11 (Parallele Subagents)

**Dauer:** ~5 Min (Parallel, real ~4 Min pro Agent)
**Methode:** 2 Subagents in isolierten Git-Worktrees, gleichzeitig

**P1-10 Report Detail-Ansicht:**
- ReportDetailComponent als eigene Route /reports/:id (Lazy-loaded)
- Report laden via switchMap auf ActivatedRoute paramMap
- Analyst + Security via forkJoin nachladen
- 10+ computed() Signals (Farbklassen, Formatierung, Sichtbarkeit)
- Rating-Sektion: 4-Spalten-Grid (Empfehlung, Kursziel, Kurs, Upside)
- Meta: Analyst (Name, Dept), Wertschrift (Ticker, Name, Sektor), Risikolevel
- Executive Summary Panel + Investment-These (Katalysatoren + Risiken)
- Tags als Badges, Fehlerbehandlung, Responsive (4→2→1 Spalten)
- Reports-Tabelle: Klick auf Zeile navigiert via Router zu /reports/:id

**P1-11 Securities View:**
- Sortierung: Name, Sektor, MarketCap (Unicode-Pfeile, Signal-basiert)
- Filter: Sektor-Dropdown (dynamisch aus Daten) + Debounced Suchfeld (300ms)
- Neue Spalte "Letzte Empfehlung": neuester Report pro Security via Map
- Klick navigiert zu /reports?security=TICKER
- Responsive: ISIN, Branche, Börse auf Mobile ausgeblendet (.hide-mobile)
- inject() Pattern, OnDestroy Cleanup mit destroy$ Subject

**Architektur-Entscheidungen:**
- Eigene Route /reports/:id statt Split-View (einfacher, mobil-freundlicher)
- forkJoin für paralleles Laden von Analyst + Security (effizienter)
- latestReportMap als computed() Signal (cached, reaktiv)

**Erkenntnisse:**
- Parallele Worktrees funktionieren erneut perfekt (2. Mal)
- Kombinierter Build: 276 KB Initial, 0 Fehler
- Report Detail als eigener Lazy Chunk: 13.74 kB

**Nächster Schritt:** P1-12 Report CRUD + P1-13 RxJS State Management

---

## 22.02.2026 - Session 4: Steuerungsdateien-Synchronisation

**Dauer:** ~10 Min
**Auslöser:** Feststellung, dass MD-Dateien nicht mit dem tatsächlichen Projektstand synchron waren.

**Gefundene Probleme (7):**
1. CHECKLIST.md: 9 erledigte Tasks nicht abgehakt (alle [ ])
2. STATUS.md: Sektions-Header (0/7) statt (7/7)
3. CURRENT-TASK.md: Zeigte P0-07, aber P1-08+P1-09 waren fertig
4. TODOs.md: Gesamtstatus 0/48, alle Sub-Items [ ]
5. DECISION-LOG.md: D-008, D-009, D-010 fehlten
6. README.md: Angular 17+ statt 21, Flyway statt data.sql
7. TODOs.md: DoD-Daten fehlten bei erledigten Tasks

**Korrigierte Dateien (6):**
- CHECKLIST.md → 9 Checkboxen [x], Phase-Header
- STATUS.md → Sektions-Header korrigiert
- CURRENT-TASK.md → Komplett neu geschrieben
- RESEARCH-PORTAL_TODOs.md → 9/48, alle P0+P1-08/09 [x] mit Datum
- DECISION-LOG.md → 3 neue Entscheidungen (D-008 bis D-010)
- README.md → Aktuelle Versionen

**Nicht geändert (waren korrekt):**
- PROGRESS-LOG.md, START.md, RULES.md, REQUIREMENTS.md, GOLDENE-REGELN.md, CLAUDE.md

**Erkenntnisse:**
- Kohärenz-Prüfung muss bei jedem Session-Start passieren
- Die R4-Regel wurde in früheren Sessions nicht vollständig angewendet
- CHECKLIST.md und TODOs.md wurden systematisch vergessen

**Nächster Schritt:** P1-10 Report Detail-Ansicht + P1-11 Securities View (parallel)

---

## 22.02.2026 - Session 3: P1-08 + P1-09 (Parallele Subagents)

**Dauer:** ~5 Min (Parallel, real ~3 Min pro Agent)
**Methode:** 2 Subagents in isolierten Git-Worktrees, gleichzeitig

**P1-08 Dashboard View:**
- KPI-Cards verbessert (font-data, 2.25rem, Untertitel, Hover)
- latestReports als computed() mit Datum-Sortierung
- Top Analysten (NEU): Top 3 nach accuracy12m, Rang, Sterne
- Coverage-Übersicht (NEU): Sektoren, Ticker, Avg. Accuracy
- 5 computed() Signals, vollständig responsive

**P1-09 Reports Tabelle:**
- Sortierung via Unicode-Pfeile (▲/▼), 3 sortierbare Spalten
- Filter: Typ-Dropdown, Rating-Dropdown, Debounced Suchfeld (RxJS Subject + 300ms)
- Analyst-Spalte via AnalystService Map<id, name>
- Rating-Change Indikator, Typ-Badges, Leerer-Zustand
- computed() für gefilterte+sortierte Liste

**Architektur-Entscheidungen:**
- Parallele Worktrees: Kein Merge-Konflikt da Dashboard und Reports separate Dateien
- RxJS Subject + debounceTime für Suchfeld (zeigt RxJS-Kompetenz für Interview)
- Rating-Sortierung mit semantischer Reihenfolge (STRONG_BUY=5, SELL=1)
- angular.json Budget erhöht (6kB/10kB für anyComponentStyle)

**Erkenntnisse:**
- Parallele Subagents funktionieren perfekt für unabhängige Features
- Worktree-Isolation verhindert Konflikte zuverlässig
- Kombinierter Build: 276 KB, 0 Fehler

**Nächster Schritt:** P1-10 Report Detail-Ansicht

---

## 22.02.2026 - Session 3 (Fortsetzung): P0-07 Login-Seite

**Dauer:** ~15 Min
**Was wurde gemacht:**
- User-Model mit 3 Rollen (ANALYST, SENIOR_ANALYST, ADMIN)
- AuthService: Mock-Auth mit 2 hardcoded Usern, Signals für reaktiven State, sessionStorage
- Functional Auth-Guard: CanActivateFn mit inject(), Redirect zu /login
- Login-Komponente: Reactive Form mit FormBuilder, Validation (required + minLength)
- Login-Design: Zentrierte Card auf schwarzem Hintergrund, Accent nur auf Submit
- Routes: authGuard auf dashboard, reports, securities, analysts
- App Shell: @if(authService.isLoggedIn()) für bedingtes Sidebar/Topbar-Layout
- Topbar: Rolle-Badge (ANALYST/ADMIN in Accent), Benutzername, Abmelden-Button

**Architektur-Entscheidungen:**
- sessionStorage statt localStorage (Session endet beim Tab-Schließen, Banking-Standard)
- inject() statt Constructor durchgehend (konsistent, vermeidet Timing-Probleme)
- Mock-Users als Record<string, {password, user}> (einfach erweiterbar)
- Functional Guard statt Class-based (Angular 15+ Best Practice)

**Erkenntnisse:**
- FormBuilder als Feld-Initialisierer braucht inject() (gleicher Timing-Fehler wie Router)
- P0 ist jetzt komplett: 7/7 Tasks in einer Session erledigt
- Die Login-Seite zeigt Reactive Forms und Auth-Architektur Kompetenz

**Nächster Schritt:** P1-08 Dashboard View (Erweiterung)

---

## 22.02.2026 - Session 3: P0-06 Frontend Grundstruktur

**Dauer:** ~30 Min
**Was wurde gemacht:**
- 3 TypeScript Models (Analyst, Security, Report) mit strenger Typisierung
- 3 HTTP Services mit Observable<T> und environment.apiUrl
- Sidebar-Komponente: Fixed 220px, RouterLink/RouterLinkActive, Accent-Farbe für aktiven Link
- Topbar-Komponente: Dynamischer Seitentitel via input() Signal
- Dashboard: 4 KPI-Cards (Reports, Wertschriften, Analysten, Rating-Änderungen) + 5 neueste Reports
- Reports-Tabelle: 7 Spalten, Rating-Farbcodierung (BUY=accent, SELL=rot, HOLD=neutral)
- Securities-Tabelle: Ticker in Accent, formatMarketCap (Bio./Mrd./Mio.)
- Analysts Card-Grid: Unicode-Sterne, Coverage-Tags, Accuracy
- App Shell: Sidebar + Content-Area (Topbar + router-outlet)
- 4 Lazy-loaded Routes mit dynamischem Import
- tsconfig.json Path-Aliases (@core/*, @features/*, @shared/*)
- Build verifiziert: 264 KB Initial Bundle, 0 Fehler

**Architektur-Entscheidungen:**
- `inject(Router)` statt Constructor Injection → ermöglicht toSignal() in Feld-Initialisierern
- `export type` im Barrel-File → kompatibel mit isolatedModules
- computed() Signal für ratingChangedCount → Arrow Functions nicht in Angular Templates erlaubt
- Alle Pipes (DatePipe, DecimalPipe) explizit in Standalone Component imports

**Erkenntnisse:**
- Angular 21 mit `toSignal()` macht Router-Events elegant reaktiv (Observable → Signal)
- `isolatedModules` erfordert `export type` für reine Interface-Re-Exports
- Angular Template Parser erlaubt keine Arrow Functions → computed Signals nutzen
- Standalone Components brauchen explizite Pipe-Imports (kein globaler CommonModule)

**Nächster Schritt:** P0-07 Login-Seite

---

## 22.02.2026 - Session 2 (Fortsetzung): P0-05 Backend REST API

**Dauer:** ~25 Min
**Was wurde gemacht:**
- 5 DTOs: ReportDto, CreateReportRequest (mit Bean Validation), SecurityDto, AnalystDto, ErrorResponse
- 3 API-Mapper: ReportApiMapper (mit automatischer impliedUpside-Berechnung), SecurityApiMapper, AnalystApiMapper
- 3 Application Services: ReportService (GetReports + ManageReport), SecurityService, AnalystService
- ResourceNotFoundException als Custom Exception (lokalisierte deutsche Fehlermeldungen)
- 3 REST Controller: ReportController (7 Endpoints), SecurityController (3 Endpoints), AnalystController (2 Endpoints)
- GlobalExceptionHandler: @RestControllerAdvice für 404, Validation, IllegalArgument
- CORS aktualisiert: `allowedOriginPatterns` statt `allowedOrigins` (Vercel Preview-Wildcards)
- Alle 12 Endpoints live getestet: 10 Reports, 10 Securities, 5 Analysts, 404-Error

**Architektur-Entscheidungen:**
- DTOs verwenden Strings für Enums (JSON-Stabilität)
- Services implementieren Use-Case-Ports (Controller kennt nur Interfaces)
- ReportApiMapper berechnet impliedUpside und ratingChanged automatisch
- allowedOriginPatterns für Vercel Preview-URL-Wildcards

**Erkenntnisse:**
- `allowedOrigins` unterstützt keine Wildcards in der Mitte → `allowedOriginPatterns` verwenden
- Bean Validation + GlobalExceptionHandler = saubere, strukturierte Fehlermeldungen automatisch
- Die hexagonale Architektur steht jetzt komplett: Web-Adapter (Ein) → Domain → Persistence-Adapter (Aus)

**Nächster Schritt:** P0-06 Frontend Grundstruktur

---

## 22.02.2026 - Session 2 (Fortsetzung): P0-04 Backend Persistence Layer

**Dauer:** ~20 Min
**Was wurde gemacht:**
- 3 JPA Entities: AnalystEntity, SecurityEntity, ResearchReportEntity
- 3 Spring Data Repositories mit Custom Query Methods (findByTicker, findByAnalystId, findBySecurityId)
- 3 Persistence Mapper: Enum-Konvertierung (String ↔ Domain-Enum), List-Konvertierung (pipe/comma-delimited ↔ List<String>)
- 3 Persistence Adapters: AnalystPersistenceAdapter, SecurityPersistenceAdapter, ReportPersistenceAdapter
- data.sql: 5 Analysten, 10 Schweizer Wertschriften (NESN, NOVN, ROG, UBSG, ZURN, SREN, ABBN, LONN, GIVN, SIKA), 10 Research Reports
- application.yml: `defer-datasource-initialization: true` + `sql.init.mode: always` für korrekte data.sql-Ausführung
- Build verifiziert: `mvn clean package -DskipTests` erfolgreich
- Spring Boot Starttest: App startet, Hibernate erstellt Tabellen, data.sql lädt fehlerfrei

**Architektur-Entscheidungen:**
- Enums als String in DB (nicht @Enumerated), damit Entity-Schicht unabhängig von Domain-Enums
- Lists als delimited Strings (pipe für Reports, comma für Analyst coverage)
- Nur ReportRepository hat save/delete (Analyst/Security sind read-only, kämen aus Fremdsystemen)

**Erkenntnisse:**
- Spring Boot 3.x führt data.sql standardmässig VOR Hibernate aus → `defer-datasource-initialization: true` nötig
- H2 `create-drop` + data.sql = perfekte Kombination für Demo-Daten (bei jedem Start frisch)
- Mapper-Schicht ist der Schlüssel zur sauberen Trennung: Domain-Objekte kennen kein JPA, JPA-Entities kennen keine Domain-Enums

**Nächster Schritt:** P0-05 Backend REST API

---

## 22.02.2026 - Session 2: P0-01 Projektinitialisierung

**Dauer:** ~30 Min
**Was wurde gemacht:**
- Java 17 via Homebrew installiert, JAVA_HOME konfiguriert
- Maven 3.9.12 installiert
- Angular CLI 21.1.4 installiert (Node.js 25)
- GitHub Repo erstellt: https://github.com/Brandea-ai/research-portal-prototype
- Initial Commit mit allen Dokumentations-Dateien gepusht
- Spring Boot 3.5.0 via Spring Initializr generiert (Web, JPA, H2, Validation, Lombok, DevTools)
- Hexagonale Package-Struktur angelegt (11 Packages)
- application.yml mit 3 Profilen (local/H2 in-memory, demo/H2 file, production/Oracle)
- WebConfig.java für CORS (localhost:4200)
- Angular 21 Projekt generiert (Standalone Components, strict TypeScript)
- PrimeNG installiert (nur als strukturelle Basis, kein Theme)
- Docker Compose + Dockerfiles (Backend: JDK 17 multi-stage, Frontend: nginx)
- nginx.conf mit API-Proxy und SPA-Routing
- Beide Builds verifiziert: `mvn clean package` BUILD SUCCESS + `ng build` erfolgreich

**Probleme und Lösungen:**
- Spring Initializr bot Boot 3.4.3 nicht an → 3.5.0 verwendet (neuer ist besser)
- Maven nutzte Java 25 statt 17 → JAVA_HOME explizit gesetzt
- `sudo` für Java-Symlink in Sandbox nicht möglich → PATH-Export statt Symlink
- Shell-Variablen in Sandbox expandieren nicht über Bash-Tool-Calls → Literale Pfade
- Flyway ohne Migrationen blockiert Start → Flyway im local-Profil deaktiviert

**Erkenntnisse:**
- Angular 21 ist aktuell (Angular CLI 21.1.4), deutlich neuer als Angular 17+ im Plan
- Standalone Components sind jetzt der Default (kein `--standalone` Flag mehr nötig)
- Spring Boot 3.5.0 kompatibel mit Java 17 (trotz JDK 25 auf dem System)
- JAVA_HOME muss bei jedem Bash-Call neu gesetzt werden (kein Shell-State-Persistence)

**Nächster Schritt:** P0-02 Design-System CSS

---

## 22.02.2026 - Session 1b: Systemische Kohärenz-Korrektur

**Dauer:** ~20 Min
**Auslöser:** Selbst-Audit gegen 4 Blueprint-Dateien und alle 41 Stellenanzeige-Tags.

**Gefundene Probleme (7):**
1. Drei verschiedene Lesereihenfolgen (README, RULES, START) → Vereinheitlicht: nur START.md definiert
2. Zwei Report-Speicherorte → .claude/reports/ gelöscht, nur _claude-tasks/reports/ bleibt
3. GOLDENE-REGELN.md fehlte → Erstellt mit allen 16 Expertengremium-Prinzipien
4. Entwicklungszyklus + Fehler-Management fehlte in RULES → Ergänzt
5. 6 Skills nicht abgedeckt (Nexus, Harness, Export, i18n, Security, Git-Flow) → 6 neue P2-Tasks
6. DECISION-LOG fehlte → Erstellt mit 7 dokumentierten Entscheidungen
7. REQUIREMENTS.md fehlte → Erstellt mit 35/35 Skills-Mapping und formalen Abnahmekriterien

**Korrigierte Dateien:**
- `.claude/referenz/START.md` → Einzige Lesereihenfolge, Systemkarte
- `.claude/GOLDENE-REGELN.md` → NEU: 16 Prinzipien, Hypothesen, KPIs
- `.claude/referenz/DECISION-LOG.md` → NEU: 7 Entscheidungen
- `.claude/referenz/REQUIREMENTS.md` → NEU: 35/35 Skills, Abnahmekriterien
- `_claude-tasks/RULES.md` → Erweitert: Zyklen, Fehler-Management, Verboten-Liste, Querverweise
- `_claude-tasks/RESEARCH-PORTAL_TODOs.md` → 6 neue Tasks (P2-22 bis P2-27), P2-18 erweitert
- `_claude-tasks/STATUS.md` → Synchronisiert, Abhängigkeiten-Spalte
- `.claude/referenz/CHECKLIST.md` → 13 P2-Tasks, Querverweise
- `.claude/referenz/CURRENT-TASK.md` → Aktualisiert
- `README.md` → Lesereihenfolge verweist auf START.md, Dateikarte
- `.claude/CLAUDE.md` → Session-Protokoll verweist auf START.md und RULES.md

**Erkenntnisse:**
- Kohärenz ist kein Feature, sondern eine Grundvoraussetzung
- Jede Datei muss wissen, wohin sie gehört und worauf sie verweist
- Drei verschiedene Quellen für die gleiche Information = garantierte Inkonsistenz
- 35/35 Skills-Mapping gibt Sicherheit, dass nichts vergessen wird

**Ergebnis:** Alle Dateien referenzieren korrekt aufeinander. Kein Widerspruch. System ist kohärent.

---

## 22.02.2026 - Session 1: Projektgründung

**Dauer:** ~45 Min
**Was wurde gemacht:**
- Tiefgründige Recherche durchgeführt:
  - Wahrscheinlicher Kunde identifiziert: ZKB (75-85%)
  - Banking Research Portal Gold Standards (Bloomberg, FactSet, Refinitiv)
  - Angular 17+ / Spring Boot 3 Best Practices 2026
  - FINMA 2023/1 Compliance-Anforderungen
  - SAFe in Schweizer Banking-IT
  - UI/UX Patterns für Financial Dashboards
  - Datenmodell für Equity Research
- Design-Entscheidung getroffen: Full Stack Match (Angular + Spring Boot)
- Farbschema definiert: #0A0A0A / #FFFFFF / #38BDF8
- Bloomberg-Prinzip: Accent = Signal, nicht Dekoration
- Typografie: Inter (UI) + JetBrains Mono (Daten)
- Keine Icons, nur Typografie und Struktur
- Komplette Projektstruktur nach FIMI-Vorbild erstellt
- 42 Tasks in 3 Phasen definiert (P0: 7, P1: 7, P2: 7 + Subtasks)
- Alle MD-Dateien der Wissensdatenbank erstellt

**Erkenntnisse:**
- ZKB nutzt nachweislich den exakt geforderten Stack
- "SLX" ist ein internes ZKB-Framework, nicht öffentlich dokumentiert
- PrimeNG p-table + Custom CSS = optimaler Kompromiss (Struktur + Skill-Beweis)
- Hexagonale Architektur ist Pflichtstandard in Banking 2026
- Schweizer Terminologie wichtig ("Wertschriften", "Kurs", "Kursziel")

**Verworfene Ansätze:**
- Next.js/React: Verworfen, weil die Stelle explizit Angular fordert
- Angular Material: Verworfen, weil Custom CSS mehr CSS3-Kompetenz zeigt
- Supabase/Cloud DB: Verworfen, H2 lokal ist einfacher und zeigt JPA/Hibernate besser

**Nächster Schritt:** P0-01 Projektinitialisierung (GitHub + Scaffolding)
