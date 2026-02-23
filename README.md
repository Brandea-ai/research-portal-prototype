# Research Portal — Prototype 1.0

> Full-Stack Prototyp für ein Banking Research Portal
> Angular 21 · Spring Boot 3.5 · Hexagonale Architektur

**Veröffentlicht:** 20.02.2026
**Status:** Vollständig implementiert (P0/P1/P2 abgeschlossen)
**Auftraggeber-Kontext:** Hays AG, Projekt-ID 2970720, Zürich

---

## Projekt-Übersicht

Dieses Repository ist ein Full-Stack Prototyp eines Banking Research Portals für Equity- und Fixed-Income-Analysten einer renommierten Schweizer Grossbank (wahrscheinlich ZKB). Der Prototyp demonstriert alle 35 geforderten Skills der Stellenanzeige durch produktionsnahen, nachweisbaren Code.

| Dimension | Inhalt |
|-----------|--------|
| Was | Research Portal für Equity/Fixed-Income-Analysten |
| Warum | Nachweis von Full-Stack-Kompetenz im Banking-Umfeld |
| Für wen | DevOps-Team "Research" einer Schweizer Grossbank |
| Endkunde (Hypothese) | Zürcher Kantonalbank (ZKB), research.zkb.ch |
| Methodik | SAFe + Scrum, Hexagonale Architektur, DDD, TDD |

---

## Implementierte Features

- [x] Dashboard mit KPIs, Charts (Chart.js: Doughnut, Bar), Top-Analysten-Rangliste
- [x] Research Reports Tabelle mit Filter, Sortierung und Volltextsuche
- [x] Report Detail-Ansicht mit Rating, Kursziel, Investment-These, Katalysatoren
- [x] Report CRUD (Erstellen, Bearbeiten, Löschen mit reaktiver Zustandsverwaltung)
- [x] Wertschriften-Verwaltung (Schweizer Aktien: NESN, NOVN, UBSG, ZURN, ROG, SREN, ABBN, LONN)
- [x] Datenexport: CSV (Semikolon, UTF-8 BOM), Excel (Apache POI), PDF (OpenPDF 2.0.3)
- [x] XML Report Import via XPath (5 XPath-Ausdrücke, 28 Unit Tests)
- [x] Audit Trail (Spring AOP, @Aspect, FINMA-Compliance-Bewusstsein)
- [x] Session Timeout (30 Minuten) + 8 Security Headers (OWASP)
- [x] Keyboard Shortcuts (vim-style Kombinationen, RxJS-gesteuert, Hilfe-Overlay)
- [x] i18n DE/FR/EN via ngx-translate v17, LanguageSwitcher-Komponente
- [x] Dark/Light/System Theme (Signal-basierter ThemeService, CSS Custom Properties)
- [x] Responsive Design (Desktop, Tablet, Mobile, Sidebar collapsible)
- [x] Swagger UI / OpenAPI 3.0 unter `/swagger-ui.html`
- [x] Login-Seite mit AuthGuard und Rollen-Badge

---

## Tech-Stack

### Frontend

| Technologie | Version | Verwendung |
|-------------|---------|------------|
| Angular | 21.1 | Standalone Components, Signals, OnPush, Lazy Routes |
| TypeScript | 5.x | Strict Mode, keine `any`-Typen, Interfaces |
| RxJS | 7.x | HTTP-Calls, State Management (BehaviorSubject), Debounced Search |
| Chart.js | 4.5 | Doughnut-Chart (Ratings), Bar-Chart (Coverage) |
| PrimeNG | 21.1 | Nur als strukturelle Basis für p-table, kein Theme |
| ngx-translate | 17.x | i18n (DE/FR/EN), TranslateLoader |
| Cypress | 15.10 | E2E Tests (5 Specs: auth, dashboard, navigation, reports, securities) |
| HTML5/CSS3 | - | 100% Custom CSS, kein Bootstrap, kein Tailwind |

### Backend

| Technologie | Version | Verwendung |
|-------------|---------|------------|
| Java | 17 | LTS, Records, Text Blocks |
| Spring Boot | 3.5.0 | Auto-Configuration, Web, Validation, AOP |
| Hibernate / JPA | 6.x | Entity Mapping, JPQL, Relationships |
| H2 Database | - | Lokal und Demo; Oracle-Profil vorbereitet |
| Flyway | - | Datenbank-Migrationen |
| Apache POI | - | Excel-Export (.xlsx) |
| OpenPDF | 2.0.3 | PDF Report Export |
| Springdoc OpenAPI | - | Swagger UI, 12 dokumentierte Endpoints |
| Maven | 3.x | Build, Dependency Management |

### DevOps und Qualität

| Technologie | Datei/Tool | Nachweis |
|-------------|-----------|----------|
| Jenkins | `Jenkinsfile` | Declarative Pipeline (Build, Test, Deploy) |
| GitLab CI | `.gitlab-ci.yml` | Parallelisierte Stages |
| Harness | `harness-pipeline.yml` | Moderne Deployment-Pipeline |
| Nexus | `settings.xml` | Repository-Manager-Konfiguration |
| SonarQube | `sonar-project.properties` | Code Quality Gate |
| Docker | `Dockerfile` (Frontend + Backend) | Container-Images |
| Docker Compose | `docker-compose.yml` | Lokales Gesamtsystem |
| Git-Flow | `docs/git-flow.md` | Feature/Release/Hotfix-Branches |

---

## Tech-Stack vollständig: 35/35 geforderte Skills

| # | Geforderte Skill | Version/Tool | Nachweis im Prototyp |
|---|-----------------|--------------|----------------------|
| 01 | Java | 17 | Backend, hexagonale Schichten, Domain-Modell |
| 02 | Spring Framework | Spring 6 | DI, AOP, Web MVC, Security Headers |
| 03 | Spring Boot | 3.5.0 | Auto-Configuration, Profil-System |
| 04 | Hibernate | 6.x (via JPA) | Entity Mapping, JPQL-Queries, Lazy-Loading |
| 05 | Java Persistence API | JPA | Repositories, @OneToMany, @ManyToOne |
| 06 | SQL | H2 SQL | `data.sql` Demo-Daten, Custom Queries |
| 07 | Oracle Financials | Oracle-Profil | `application-production.yml`, Oracle-SQL-Syntax |
| 08 | XPath | javax.xml | `XmlReportImportService`, 5 XPath-Ausdrücke, 28 Tests |
| 09 | Angular | 21.1 | Standalone Components, Signals, inject() |
| 10 | TypeScript | 5.x Strict | Strict Mode, Typed Forms, keine `any` |
| 11 | RxJS | 7.x | BehaviorSubject, Operators, Debounce, takeUntil |
| 12 | Node.js | 20.x | Angular CLI, Dev Server, Build-System |
| 13 | NPM | 10.x | Package Management, `package.json` |
| 14 | HTML5 | - | Semantisches Markup, Accessibility |
| 15 | CSS | CSS Custom Properties | 100% Custom, Theme-System, Responsive |
| 16 | Apache Maven | 3.x | `pom.xml`, Multi-Dependency Build |
| 17 | Nexus | - | `settings.xml`, Repository-Konfiguration |
| 18 | Git | Git-Flow | Feature/Release/Hotfix-Branching |
| 19 | GitLab | `.gitlab-ci.yml` | Pipeline: Build, Test, Deploy, SonarQube |
| 20 | Jenkins | `Jenkinsfile` | Declarative Pipeline mit Stages |
| 21 | Cypress | 15.10 | 5 Specs, E2E Login/CRUD/Filter/Navigation |
| 22 | Ant | - | Erwähnt als Legacy-Build-Erfahrung in CONTRIBUTING.md |
| 23 | Harness | `harness-pipeline.yml` | Modernes CD-Tool, Pipeline-Konfiguration |
| 24 | Scrum | - | Sprint-basierte Phasen P0/P1/P2 |
| 25 | Scaled Agile Framework | SAFe | PI-Konzept, ART, Team-Dokumentation |
| 26 | Domain Driven Design | - | Bounded Contexts, Aggregates, Value Objects |
| 27 | Test-Driven Development | JUnit 5 | Red-Green-Refactor, 163 Tests gesamt |
| 28 | Continuous Integration | - | Automatisierter Build + Test in Jenkins/GitLab |
| 29 | DevOps | Docker | "You build it, you run it", Container-Deployment |
| 30 | Software Architecture | Hexagonal | Ports & Adapters, dokumentiert in ARCHITEKTUR.md |
| 31 | Requirements Management | - | `REQUIREMENTS.md`, 35/35 Skills nachverfolgt |
| 32 | Softwareanforderungsanalyse | MECE | Formale Anforderungsanalyse mit Prioritäten |
| 33 | Test Automation | Cypress + JUnit | E2E + Unit-Tests, CI-Integration |
| 34 | Web Applikationen | SPA + REST | Angular SPA + Spring Boot REST API |
| 35 | Agile Methodologie | SAFe + Scrum | Phasenplanung, Sprint-Reviews, Retrospektiven |

---

## Schnellstart

### Voraussetzungen

- Java 17+
- Maven 3.8+
- Node.js 20+
- Docker (optional)

### Backend starten

```bash
cd backend
mvn spring-boot:run
# Läuft auf http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Frontend starten

```bash
cd frontend
npm install
ng serve
# Läuft auf http://localhost:4200
```

### Mit Docker Compose (alles zusammen)

```bash
docker compose up
# Frontend: http://localhost:4200
# Backend:  http://localhost:8080
```

### Tests ausführen

```bash
# Backend Unit Tests (163 Tests)
cd backend && mvn test

# Frontend E2E Tests (Cypress, 5 Specs)
cd frontend && npx cypress run

# Frontend E2E Tests (interaktiv)
cd frontend && npx cypress open
```

### Login-Daten (Demo)

```
Benutzername: analyst
Passwort:     password
```

---

## Architektur

Detaillierte Dokumentation: [`docs/ARCHITEKTUR.md`](docs/ARCHITEKTUR.md)

Das Backend folgt der hexagonalen Architektur (Ports & Adapters) nach Alistair Cockburn. Die Domain-Schicht enthält keine Framework-Abhängigkeiten.

```
Frontend (Angular 21 SPA)
    |
    |  REST API (JSON, Port 8080)
    v
+-----------------------------------+
|  Adapter: REST (IN)               |
|  ResearchReportController         |
|  SecurityController               |
|  AnalystController                |
+----------------+------------------+
                 |
+----------------v------------------+
|  Application: Use Cases           |
|  PublishReportService             |
|  SearchReportsService             |
|  ExportService / AuditAspect      |
+----------------+------------------+
                 |
+----------------v------------------+
|  DOMAIN (keine Spring-Imports)    |
|  Models: ResearchReport,          |
|          Security (Wertschrift),  |
|          Analyst, RatingHistory   |
|  Ports IN:  PublishReportUseCase  |
|  Ports OUT: ReportRepository      |
+----------------+------------------+
                 |
+----------------v------------------+
|  Adapter: Persistence (OUT)       |
|  JPA Entities (von Domain getrennt)|
|  H2 (lokal) / Oracle (Prod)      |
+-----------------------------------+
```

### Frontend-Struktur

```
src/app/
    core/           # Services, Guards, Models
    features/       # Dashboard, Reports, Securities, Analysts, Login
    layout/         # Sidebar, Topbar
    shared/         # LanguageSwitcher, SessionWarning, ShortcutHelp
```

---

## Tests

### Backend (163 JUnit 5 Tests)

```bash
cd backend && mvn test
```

Abgedeckte Bereiche:

| Bereich | Tests | Werkzeuge |
|---------|-------|-----------|
| Adapter (REST + Persistence) | 70 | JUnit 5, Mockito, MockMvc |
| XPath Report Import | 28 | JUnit 5, XML-Testdaten |
| Audit Trail (AOP) | 21 | JUnit 5, Spring AOP |
| Session Timeout + Security | 21 | JUnit 5, MockMvc |
| Datenexport CSV/Excel | 13 | JUnit 5, Apache POI |
| PDF Export | 10 | JUnit 5, OpenPDF |

### Frontend (Cypress E2E)

```bash
cd frontend && npx cypress run
```

| Spec | Inhalt |
|------|--------|
| `auth.cy.ts` | Login, Logout, AuthGuard |
| `dashboard.cy.ts` | KPIs, Charts, Navigation |
| `reports.cy.ts` | Tabelle, Filter, CRUD |
| `securities.cy.ts` | Wertschriften-Liste, Sortierung |
| `navigation.cy.ts` | Routing, Keyboard Shortcuts |

---

## Deployment

### Frontend (Vercel)

Konfiguration: [`vercel.json`](vercel.json)

```bash
vercel --prod
```

### Backend (Fly.io)

```bash
fly deploy
```

### CI/CD Pipelines

| Pipeline | Datei | Stages |
|----------|-------|--------|
| Jenkins | `Jenkinsfile` | Checkout, Build, Test, SonarQube, Deploy |
| GitLab CI | `.gitlab-ci.yml` | Build, Test, SonarQube, Deploy |
| Harness | `harness-pipeline.yml` | Deploy mit Rollback |

---

## Projektstruktur

```
research-portal-prototype/
    backend/
        src/main/java/com/research/portal/
            domain/             # Domain-Modell (keine Framework-Imports)
                model/          # ResearchReport, Security, Analyst
                port/in/        # Use Case Interfaces
                port/out/       # Repository Interfaces
            application/
                service/        # Business-Logik
                aspect/         # AuditTrailAspect (Spring AOP)
                exception/      # GlobalExceptionHandler
            adapter/in/rest/    # REST Controller + DTOs
            adapter/out/        # JPA Entities, Repositories, Mapper
            config/             # SecurityConfig, SwaggerConfig
        src/test/               # 163 JUnit 5 Tests
        pom.xml
    frontend/
        src/app/
            core/               # AuthService, ThemeService, Guards, Models
            features/
                dashboard/      # KPIs, Charts (Chart.js)
                reports/        # Tabelle, Detail, CRUD, Export
                securities/     # Wertschriften-Verwaltung
                analysts/       # Analysten-Übersicht
                login/          # Login-Seite
            layout/             # Sidebar, Topbar
            shared/             # LanguageSwitcher, SessionWarning, ShortcutHelp
        cypress/e2e/            # 5 E2E Test Specs
        package.json
    docs/
        git-flow.md             # Git-Flow Strategie
    docker-compose.yml
    Jenkinsfile
    .gitlab-ci.yml
    sonar-project.properties
    CONTRIBUTING.md
    STANDARDS.md
```

---

## Dokumentation

| Dokument | Inhalt |
|----------|--------|
| [`docs/git-flow.md`](docs/git-flow.md) | Git-Flow Branching-Strategie (Feature/Release/Hotfix) |
| [`CONTRIBUTING.md`](CONTRIBUTING.md) | Commit-Konventionen, PR-Prozess, Code Style |
| [`STANDARDS.md`](STANDARDS.md) | Coding Standards, Design-System-Referenz |
| `http://localhost:8080/swagger-ui.html` | Swagger UI, 12 Endpoints, OpenAPI 3.0 |
| [`.claude/planung/ARCHITEKTUR.md`](.claude/planung/ARCHITEKTUR.md) | Architektur-Entscheidungen, UI-Layout |
| [`.claude/referenz/REQUIREMENTS.md`](.claude/referenz/REQUIREMENTS.md) | Vollständige Anforderungsanalyse, 35/35 Skills |

---

## Schweizer Banking-Kontext

### Demo-Wertschriften

| Ticker | Name | Sektor |
|--------|------|--------|
| NESN | Nestlé SA | Consumer Staples |
| NOVN | Novartis AG | Healthcare |
| ROG | Roche Holding AG | Healthcare |
| UBSG | UBS Group AG | Financials |
| ZURN | Zurich Insurance Group | Financials |
| SREN | Swiss Re AG | Financials |
| ABBN | ABB Ltd | Industrials |
| LONN | Lonza Group AG | Healthcare |

### FINMA-Compliance-Bewusstsein

Der Prototyp demonstriert ein Verständnis der regulatorischen Anforderungen:

- Audit Trail via Spring AOP: Jede Publikation und Änderung wird geloggt (wer, wann, was)
- Session Timeout: 30 Minuten Inaktivität, danach automatischer Logout
- Security Headers: 8 OWASP-empfohlene HTTP-Header
- Compliance-Flag: `isRestricted` an Reports für Price-Sensitive-Informationen
- Demo-Daten: Keine realen Kundendaten (Schweizer Bankgeheimnis)

---

## Entwicklungshistorie

| Phase | Tasks | Zeitraum | Status |
|-------|-------|----------|--------|
| P0: Fundament (Architektur, Domain, API, UI) | 7/7 | 22.02.2026 | Abgeschlossen |
| P1: Kernfeatures (Dashboard, Reports, CRUD, Responsive) | 7/7 | 22.02.2026 | Abgeschlossen |
| P2: Professionalität (Tests, Export, Audit, i18n, CI/CD) | 13/13 | 22.-23.02.2026 | Abgeschlossen |

**Gesamt:** 27/27 Tasks, 163 Backend-Tests, 5 Cypress-Specs, 330 KB Bundle (gzip).

---

## Lizenz und Kontakt

Lizenz: MIT

Entwickelt als Prototyp für **Hays AG Projekt 2970720** (Zürich).
Zeigt Full-Stack-Kompetenz im Bereich Angular + Spring Boot für den Einsatz im Schweizer Banking-Umfeld.
