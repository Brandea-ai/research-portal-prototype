# Architekturdokumentation: Research Portal Prototype

> Banking Research Portal für Equity/Fixed-Income-Analysten einer Schweizer Bank
> Stack: Angular 21 + Spring Boot 3.5 + H2/Oracle + Docker
> Skill-Nachweis: TS-30 "Software Architecture"

---

## 1. Architektur-Überblick

### Systemüberblick (3-Schichten)

```
┌─────────────────────────────────────────────────────────────────────┐
│                         BROWSER / CLIENT                            │
│                                                                     │
│   ┌─────────────────────────────────────────────────────────────┐  │
│   │              Angular 21 Single Page Application             │  │
│   │                                                             │  │
│   │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │  │
│   │  │Dashboard │  │ Reports  │  │Wertschr. │  │Analysten │  │  │
│   │  │Component │  │Component │  │Component │  │Component │  │  │
│   │  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │  │
│   │       │              │              │              │          │  │
│   │  ┌────▼──────────────▼──────────────▼──────────────▼─────┐  │  │
│   │  │           Core Services (ReportStateService, etc.)     │  │  │
│   │  └────────────────────────┬───────────────────────────────┘  │  │
│   │                           │ HttpClient / RxJS                 │  │
│   └───────────────────────────┼───────────────────────────────────┘  │
│                               │                                     │
└───────────────────────────────┼─────────────────────────────────────┘
                                │ HTTPS / REST / JSON
                                │ Port 8080
┌───────────────────────────────▼─────────────────────────────────────┐
│                        REST API LAYER                               │
│                                                                     │
│  GET/POST /api/reports      GET /api/analysts                       │
│  GET/PUT/DELETE /api/reports/{id}   GET /api/securities             │
│  GET /api/audit             GET /api/export/reports/csv             │
│  GET /api/export/reports/excel      GET /api/export/reports/{id}/pdf│
│  POST /api/import/xml       GET /api/session/status                 │
│  POST /api/session/keepalive                                        │
│                                                                     │
│  ┌────────────────────────────────────────────────────────────────┐ │
│  │          Spring Boot 3.5 (Java 17) + Hexagonale Architektur   │ │
│  └────────────────────────────────────────────────────────────────┘ │
│                                                                     │
└───────────────────────────────┬─────────────────────────────────────┘
                                │ JPA / Hibernate
                                │
┌───────────────────────────────▼─────────────────────────────────────┐
│                        PERSISTENCE LAYER                            │
│                                                                     │
│   ┌──────────────────────┐   ┌───────────────────────────────────┐  │
│   │ H2 In-Memory (local) │   │ H2 File-based (demo)              │  │
│   │ H2 File-based (demo) │   │ Oracle DB (production-ready)      │  │
│   └──────────────────────┘   └───────────────────────────────────┘  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Architekturstil

Das System folgt einer klassischen **3-Schichten-Architektur** mit modernen Mustern:

- **Präsentationsschicht:** Angular 21 SPA mit Standalone Components, Signal-basiertem State und OnPush Change Detection. Kein Server-Side Rendering, keine NgModules.
- **Anwendungsschicht:** Spring Boot 3.5 REST API nach dem Prinzip der **hexagonalen Architektur** (Ports & Adapters). Die Domänenlogik ist vollständig framework-unabhängig.
- **Datenschicht:** JPA/Hibernate mit H2 für Entwicklung und Demo. Die Oracle-Konfiguration ist produktionsbereit vorbereitet.

---

## 2. Hexagonale Architektur (Backend)

### Konzept: Ports & Adapters

```
                        ┌─────────────────────────────────────┐
                        │          INBOUND ADAPTER             │
                        │  (REST Controller, DTOs, Mapper)    │
                        └───────────────┬─────────────────────┘
                                        │ ruft auf
                                        ▼
                        ┌───────────────────────────────────────────────────────────┐
                        │                  DOMAIN (Kern)                            │
                        │                                                           │
                        │  ┌─────────────────────────────────────────────────┐     │
                        │  │               INBOUND PORTS                     │     │
                        │  │  GetReportsUseCase     ManageReportUseCase      │     │
                        │  │  GetAnalystsUseCase    GetSecuritiesUseCase     │     │
                        │  │  AuditLogUseCase                                │     │
                        │  └─────────────────────┬───────────────────────────┘     │
                        │                        │ implementiert von               │
                        │  ┌─────────────────────▼───────────────────────────┐     │
                        │  │               APPLICATION SERVICES               │     │
                        │  │  ReportService        AnalystService             │     │
                        │  │  SecurityService      AuditService               │     │
                        │  │  ExportService        PdfExportService           │     │
                        │  │  XmlReportParserService                         │     │
                        │  │  AuditAspect (@Aspect, AOP)                     │     │
                        │  └─────────────────────┬───────────────────────────┘     │
                        │                        │ ruft auf                       │
                        │  ┌─────────────────────▼───────────────────────────┐     │
                        │  │               OUTBOUND PORTS                    │     │
                        │  │  ReportRepository     AnalystRepository         │     │
                        │  │  SecurityRepository   AuditLogRepository        │     │
                        │  └─────────────────────────────────────────────────┘     │
                        │                                                           │
                        │  ENTITIES: Analyst, Security, ResearchReport, AuditLog  │
                        │  ENUMS: Rating, ReportType, AssetClass,                 │
                        │         RiskLevel, AuditAction                          │
                        │  VALUE OBJECTS: FinancialEstimates, RatingHistory       │
                        └───────────────────────────────┬───────────────────────────┘
                                                        │ implementiert von
                                                        ▼
                        ┌─────────────────────────────────────────────────────────┐
                        │                  OUTBOUND ADAPTER                       │
                        │  (JPA Entities, JPA Repositories, Persistence Mapper,  │
                        │   Persistence Adapter)                                  │
                        └─────────────────────────────────────────────────────────┘
```

### Domain-Schicht

**Entities (reine Java-Objekte, keine Framework-Abhängigkeiten):**

| Entity | Felder | Beschreibung |
|--------|--------|--------------|
| `Analyst` | id, name, title, department, email, coverageUniverse, starRating, accuracy12m | Research-Analyst der Bank. Beobachtet ein Coverage Universe aus Wertschriften-Tickern. |
| `Security` | id, ticker, isin, name, assetClass, sector, industry, exchange, currency, marketCap | Wertschrift (Aktie, Anleihe, etc.). Beispiele: NESN, NOVN, UBSG. |
| `ResearchReport` | id, analystId, securityId, publishedAt, reportType, title, executiveSummary, fullText, rating, previousRating, ratingChanged, targetPrice, previousTarget, currentPrice, impliedUpside, riskLevel, investmentCatalysts, keyRisks, tags | Kernprodukt des Analysten: eine Empfehlung mit Kursziel. |
| `AuditLog` | id, timestamp, action, entityType, entityId, userId, userName, userRole, details, ipAddress | Unveränderlicher FINMA-konformer Audit-Trail-Eintrag. |
| `FinancialEstimates` | fiscalYear, revenue, revenueGrowth, ebitda, ebitdaMargin, eps, peRatio, evEbitda, dividendYield | Finanzkennzahlen-Schätzungen je Geschäftsjahr. |
| `RatingHistory` | id, securityId, analystId, date, rating, targetPrice, reportId | Historische Empfehlungsänderungen je Wertschrift. |

**Enums:**

| Enum | Werte | Bedeutung |
|------|-------|-----------|
| `Rating` | STRONG_BUY, BUY, HOLD, SELL, STRONG_SELL | Analystenempfehlung (Schweizer Terminologie: Empfehlung) |
| `ReportType` | INITIATION, UPDATE, QUARTERLY, FLASH, DEEP_DIVE, CREDIT | Art des Research-Dokuments |
| `AssetClass` | EQUITY, FIXED_INCOME, DERIVATIVES, MACRO | Anlageklasse der Wertschrift |
| `RiskLevel` | LOW, MEDIUM, HIGH, SPECULATIVE | Risikoklasse der Empfehlung |
| `AuditAction` | CREATE, UPDATE, DELETE, VIEW | Art der protokollierten Aktion |

**Inbound Ports (Use-Case Interfaces):**

```java
// Beispiel: Die Domain definiert WAS sie braucht
public interface GetReportsUseCase {
    List<ResearchReport> getAllReports();
    Optional<ResearchReport> getReportById(Long id);
    List<ResearchReport> getReportsByAnalyst(Long analystId);
    List<ResearchReport> getReportsBySecurity(Long securityId);
}
```

**Outbound Ports (Repository Interfaces):**

```java
// Beispiel: Die Domain definiert die Schnittstelle, der Adapter die Implementierung
public interface ReportRepository {
    List<ResearchReport> findAll();
    Optional<ResearchReport> findById(Long id);
    ResearchReport save(ResearchReport report);
    void deleteById(Long id);
}
```

### Application-Schicht

**Services:**

| Service | Use Case Interface | Aufgabe |
|---------|-------------------|---------|
| `ReportService` | GetReportsUseCase, ManageReportUseCase | CRUD für Research Reports |
| `AnalystService` | GetAnalystsUseCase | Abrufen von Analysten-Profilen |
| `SecurityService` | GetSecuritiesUseCase | Abrufen von Wertschriften-Stammdaten |
| `AuditService` | AuditLogUseCase | Schreiben und Lesen des Audit-Trails |
| `ExportService` | — (direkt genutzt) | CSV und Excel-Export via Apache POI |
| `PdfExportService` | — (direkt genutzt) | PDF-Export via OpenPDF/iText |
| `XmlReportParserService` | — (direkt genutzt) | XPath-basiertes XML-Parsing mit XSD-Validierung |

**AuditAspect (AOP):**

```java
@Aspect
@Component
public class AuditAspect {
    // @AfterReturning auf createReport, updateReport, deleteReport, getReportById
    // Automatisches Audit-Logging ohne Business-Logik-Änderungen
    // FINMA-konform: nur erfolgreiche Aktionen werden protokolliert
}
```

### Adapter-Schicht

**Inbound Adapters (REST):**

| Controller | Basis-Pfad | Aufgabe |
|------------|-----------|---------|
| `ReportController` | `/api/reports` | CRUD für Research Reports |
| `AnalystController` | `/api/analysts` | Abrufen von Analysten |
| `SecurityController` | `/api/securities` | Abrufen von Wertschriften |
| `AuditController` | `/api/audit` | Audit-Trail abfragen |
| `ExportController` | `/api/export` | CSV und Excel-Export |
| `PdfExportController` | `/api/export` | PDF-Export je Report |
| `SessionController` | `/api/session` | Session-Status und Keep-Alive |
| `XmlImportController` | `/api/import` | XML-Import mit XPath-Parsing |

**Outbound Adapters (Persistence):**

| Adapter | Interface | JPA Repository |
|---------|-----------|----------------|
| `ReportPersistenceAdapter` | `ReportRepository` | `JpaReportRepository` |
| `AnalystPersistenceAdapter` | `AnalystRepository` | `JpaAnalystRepository` |
| `SecurityPersistenceAdapter` | `SecurityRepository` | `JpaSecurityRepository` |
| `AuditLogPersistenceAdapter` | `AuditLogRepository` | `JpaAuditLogRepository` |

### Package-Struktur

```
com.research.portal
│
├── domain/                          <-- Kern: keine Framework-Abhängigkeiten
│   ├── model/
│   │   ├── Analyst.java
│   │   ├── Security.java
│   │   ├── ResearchReport.java
│   │   ├── AuditLog.java
│   │   ├── FinancialEstimates.java
│   │   ├── RatingHistory.java
│   │   ├── Rating.java              (enum)
│   │   ├── ReportType.java          (enum)
│   │   ├── AssetClass.java          (enum)
│   │   ├── RiskLevel.java           (enum)
│   │   └── AuditAction.java         (enum)
│   └── port/
│       ├── in/                      <-- Inbound Ports (Use-Case Interfaces)
│       │   ├── GetReportsUseCase.java
│       │   ├── ManageReportUseCase.java
│       │   ├── GetAnalystsUseCase.java
│       │   ├── GetSecuritiesUseCase.java
│       │   └── AuditLogUseCase.java
│       └── out/                     <-- Outbound Ports (Repository Interfaces)
│           ├── ReportRepository.java
│           ├── AnalystRepository.java
│           ├── SecurityRepository.java
│           └── AuditLogRepository.java
│
├── application/                     <-- Anwendungsschicht
│   ├── service/
│   │   ├── ReportService.java
│   │   ├── AnalystService.java
│   │   ├── SecurityService.java
│   │   ├── AuditService.java
│   │   ├── ExportService.java
│   │   ├── PdfExportService.java
│   │   └── XmlReportParserService.java
│   ├── aspect/
│   │   ├── AuditAspect.java
│   │   └── Audited.java             (Custom Annotation)
│   └── exception/
│       └── ResourceNotFoundException.java
│
├── adapter/                         <-- Adapter-Schicht
│   ├── in/
│   │   └── web/
│   │       ├── controller/
│   │       │   ├── ReportController.java
│   │       │   ├── AnalystController.java
│   │       │   ├── SecurityController.java
│   │       │   ├── AuditController.java
│   │       │   ├── ExportController.java
│   │       │   ├── PdfExportController.java
│   │       │   ├── SessionController.java
│   │       │   └── XmlImportController.java
│   │       ├── dto/
│   │       │   ├── ReportDto.java
│   │       │   ├── AnalystDto.java
│   │       │   ├── SecurityDto.java
│   │       │   ├── AuditLogDto.java
│   │       │   ├── CreateReportRequest.java
│   │       │   ├── XmlImportResponse.java
│   │       │   ├── XmlValidationResponse.java
│   │       │   └── ErrorResponse.java
│   │       ├── mapper/
│   │       │   ├── ReportApiMapper.java
│   │       │   ├── AnalystApiMapper.java
│   │       │   ├── SecurityApiMapper.java
│   │       │   └── AuditLogApiMapper.java
│   │       └── GlobalExceptionHandler.java
│   └── out/
│       └── persistence/
│           ├── entity/
│           │   ├── ResearchReportEntity.java
│           │   ├── AnalystEntity.java
│           │   ├── SecurityEntity.java
│           │   └── AuditLogEntity.java
│           ├── repository/
│           │   ├── JpaReportRepository.java
│           │   ├── JpaAnalystRepository.java
│           │   ├── JpaSecurityRepository.java
│           │   └── JpaAuditLogRepository.java
│           ├── mapper/
│           │   ├── ReportPersistenceMapper.java
│           │   ├── AnalystPersistenceMapper.java
│           │   ├── SecurityPersistenceMapper.java
│           │   └── AuditLogPersistenceMapper.java
│           └── adapter/
│               ├── ReportPersistenceAdapter.java
│               ├── AnalystPersistenceAdapter.java
│               ├── SecurityPersistenceAdapter.java
│               └── AuditLogPersistenceAdapter.java
│
├── config/
│   ├── WebConfig.java               (CORS, Web-Konfiguration)
│   ├── SecurityHeadersFilter.java   (8 OWASP Security-Header)
│   ├── SessionConfig.java           (30-Min-Timeout, FINMA)
│   └── OpenApiConfig.java           (Swagger/OpenAPI 3.0)
│
└── ResearchPortalApplication.java
```

---

## 3. Frontend-Architektur

### Konzept: Angular 21 Standalone

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Angular 21 Application                          │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    ROUTING (Lazy-loaded)                     │  │
│  │  /login       → LoginComponent                              │  │
│  │  /dashboard   → DashboardComponent      [authGuard]         │  │
│  │  /reports     → ReportsComponent        [authGuard]         │  │
│  │  /reports/:id → ReportDetailComponent   [authGuard]         │  │
│  │  /reports/new → ReportFormComponent     [authGuard]         │  │
│  │  /securities  → SecuritiesComponent     [authGuard]         │  │
│  │  /analysts    → AnalystsComponent       [authGuard]         │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌──────────────────┐  ┌──────────────────┐                       │
│  │   Layout         │  │   Shared          │                       │
│  │  SidebarComponent│  │  SessionWarning   │                       │
│  │  TopbarComponent │  │  LanguageSwitcher │                       │
│  │                  │  │  ShortcutHelp     │                       │
│  └──────────────────┘  └──────────────────┘                       │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │               CORE SERVICES                                  │  │
│  │                                                              │  │
│  │  ReportStateService   ReportService     AnalystService       │  │
│  │  SecurityService      AuthService       SessionService       │  │
│  │  ThemeService         KeyboardShortcutService                │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │               STATE-MANAGEMENT                               │  │
│  │                                                              │  │
│  │  Angular Signals:   reports, analysts, securities, loading   │  │
│  │                     sessionTimeoutMinutes, remainingSeconds   │  │
│  │                     isWarningVisible, resolvedTheme           │  │
│  │                                                              │  │
│  │  RxJS BehaviorSubject: ReportStateService.reports$           │  │
│  │                        ReportStateService.loading$           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Standalone Components

Alle Komponenten sind Standalone Components (kein NgModule). Beispiel:

```typescript
@Component({
  selector: 'app-dashboard',
  standalone: true,                          // kein NgModule
  imports: [DatePipe, DecimalPipe, TranslatePipe],
  templateUrl: './dashboard.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,  // Performance
})
export class DashboardComponent implements OnInit {
  // Signal-basierter State
  reports = signal<Report[]>([]);
  loading = signal(true);

  // Computed Signals (abgeleitet, reaktiv)
  ratingDistribution = computed(() => { ... });
  latestReports = computed(() => [...this.reports()].sort(...).slice(0, 8));
  avgAccuracy = computed(() => { ... });
}
```

### Signal-basierter State

Angular Signals ersetzen Zone.js-basierte Change Detection für lokalen UI-State:

| Service / Komponente | Signal | Beschreibung |
|----------------------|--------|--------------|
| `SessionService` | `sessionTimeoutMinutes` | Konfigurierter Timeout in Minuten |
| `SessionService` | `remainingSeconds` | Countdown bis Session-Ablauf |
| `SessionService` | `isWarningVisible` | Warndialog sichtbar |
| `ThemeService` | `resolvedTheme` | Aktuelles Theme (dark/light) |
| `DashboardComponent` | `reports`, `analysts`, `securities` | Lokaler Daten-State |
| `DashboardComponent` | `loading` | Ladezustand |

### RxJS für HTTP-Calls und Event-Streams

```typescript
// ReportStateService: BehaviorSubject als zentraler Report-State
@Injectable({ providedIn: 'root' })
export class ReportStateService {
  private readonly reportsSubject = new BehaviorSubject<Report[]>([]);
  readonly reports$ = this.reportsSubject.asObservable();

  createReport(request: CreateReportRequest): Observable<Report> {
    return this.reportService.create(request).pipe(
      tap(() => this.refresh())  // Auto-Refresh nach CRUD
    );
  }
}

// SessionService: catchError für fehlertolerante Keep-Alive-Requests
this.http.post('/api/session/keepalive', {})
  .pipe(catchError(() => of(null)))
  .subscribe(...);
```

### Services

| Service | Pattern | Aufgabe |
|---------|---------|---------|
| `ReportStateService` | BehaviorSubject + RxJS | Zentraler Report-Cache, Auto-Refresh nach CRUD |
| `ReportService` | HttpClient + Observables | HTTP-Calls für Report-CRUD und Export |
| `AnalystService` | HttpClient + Observables | Abrufen von Analysten-Daten |
| `SecurityService` | HttpClient + Observables | Abrufen von Wertschriften-Stammdaten |
| `AuthService` | Signal (isLoggedIn) | Sitzungsverwaltung, Logout |
| `SessionService` | Signals + setInterval | Countdown-Timer, Keep-Alive, Aktivitätserkennung |
| `ThemeService` | Signal (resolvedTheme) | Dark/Light/System Theme via data-theme Attribut |
| `KeyboardShortcutService` | Event Listener | Globale Tastaturkürzel |

### Layout

```
┌────────────────────────────────────────────────────────────────────┐
│                          TopBar                                    │
│  Logo | Breadcrumb | Theme-Toggle | Language-Switch | User-Info   │
├──────────────┬─────────────────────────────────────────────────────┤
│              │                                                     │
│   Sidebar    │              Main Content Area                     │
│              │                                                     │
│  Dashboard   │   ┌─────────────────────────────────────────────┐ │
│  Reports     │   │     Feature Component (lazy-loaded)         │ │
│  Wertschr.   │   │     (DashboardComponent, ReportsComponent,  │ │
│  Analysten   │   │      SecuritiesComponent, AnalystsComponent)│ │
│              │   └─────────────────────────────────────────────┘ │
│              │                                                     │
└──────────────┴─────────────────────────────────────────────────────┘
```

---

## 4. Datenmodell

### ER-Diagramm

```
┌─────────────────────┐         ┌─────────────────────────────────────────────┐
│      analysts       │         │               research_reports              │
├─────────────────────┤         ├─────────────────────────────────────────────┤
│ id          BIGINT PK│◄────── │ id              BIGINT PK                  │
│ name        VARCHAR │  1:N   │ analyst_id      BIGINT FK → analysts.id     │
│ title       VARCHAR │         │ security_id     BIGINT FK → securities.id  │
│ department  VARCHAR │         │ published_at    TIMESTAMP                   │
│ email       VARCHAR │         │ report_type     VARCHAR                     │
│ coverage_   VARCHAR │         │ title           VARCHAR                     │
│   universe  (CSV)   │         │ executive_      VARCHAR(2000)               │
│ star_rating INT     │         │   summary                                   │
│ accuracy_12m DOUBLE │         │ full_text       VARCHAR(10000)              │
└─────────────────────┘         │ rating          VARCHAR                     │
                                │ previous_rating VARCHAR                     │
┌─────────────────────┐         │ rating_changed  BOOLEAN                    │
│      securities     │         │ target_price    DECIMAL(10,2)               │
├─────────────────────┤         │ previous_target DECIMAL(10,2)              │
│ id          BIGINT PK│◄────── │ current_price   DECIMAL(10,2)              │
│ ticker      VARCHAR │  1:N   │ implied_upside  DECIMAL(5,2)               │
│ isin        VARCHAR │         │ risk_level      VARCHAR                     │
│ name        VARCHAR │         │ investment_     VARCHAR(1000)               │
│ asset_class VARCHAR │         │   catalysts     (CSV)                       │
│ sector      VARCHAR │         │ key_risks       VARCHAR(1000)               │
│ industry    VARCHAR │         │ tags            VARCHAR(500)                │
│ exchange    VARCHAR │         └─────────────────────────────────────────────┘
│ currency    VARCHAR │                           │
│ market_cap  DECIMAL │                           │ 1:N
└─────────────────────┘                           ▼
                                ┌─────────────────────────────────────────────┐
                                │               audit_logs                    │
                                ├─────────────────────────────────────────────┤
                                │ id          BIGINT PK                       │
                                │ timestamp   TIMESTAMP                       │
                                │ action      VARCHAR  (CREATE/UPDATE/DELETE) │
                                │ entity_type VARCHAR  (REPORT/SECURITY/etc.) │
                                │ entity_id   BIGINT   FK → report.id        │
                                │ user_id     VARCHAR                         │
                                │ user_name   VARCHAR                         │
                                │ user_role   VARCHAR                         │
                                │ details     VARCHAR                         │
                                │ ip_address  VARCHAR                         │
                                └─────────────────────────────────────────────┘
```

### Beziehungen

| Beziehung | Typ | Beschreibung |
|-----------|-----|--------------|
| `ResearchReport → Analyst` | N:1 | Jeder Report gehört genau einem Analysten |
| `ResearchReport → Security` | N:1 | Jeder Report bezieht sich auf genau eine Wertschrift |
| `AuditLog → ResearchReport` | N:1 | Jede Aktion auf einem Report erzeugt einen Audit-Eintrag |
| `Analyst → Security` | M:N (logisch) | Analyst beobachtet mehrere Wertschriften (coverageUniverse) |

### Schweizer Demo-Daten

Die Datenbank wird beim Start mit repräsentativen Schweizer Wertschriften befüllt:

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

---

## 5. API-Design

### REST Endpoints

| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| GET | `/api/reports` | Alle Research Reports abrufen |
| GET | `/api/reports?analystId={id}` | Reports nach Analyst filtern |
| GET | `/api/reports?securityId={id}` | Reports nach Wertschrift filtern |
| POST | `/api/reports` | Neuen Research Report erstellen |
| GET | `/api/reports/{id}` | Einzelnen Report abrufen |
| PUT | `/api/reports/{id}` | Report aktualisieren (Kursziel, Rating) |
| DELETE | `/api/reports/{id}` | Report löschen |
| GET | `/api/analysts` | Alle Analysten abrufen |
| GET | `/api/analysts/{id}` | Einzelnen Analysten abrufen |
| GET | `/api/securities` | Alle Wertschriften abrufen |
| GET | `/api/securities/{id}` | Einzelne Wertschrift abrufen |
| GET | `/api/audit` | Audit-Trail abrufen (letzten 50 Einträge) |
| GET | `/api/audit?entityType=REPORT&entityId={id}` | Audit-Trail gefiltert |
| GET | `/api/audit/report/{id}` | Audit-Trail für einen Report |
| GET | `/api/export/reports/csv` | Alle Reports als CSV exportieren |
| GET | `/api/export/reports/excel` | Alle Reports als Excel (XLSX) exportieren |
| GET | `/api/export/reports/{id}/pdf` | Einzelnen Report als PDF exportieren |
| POST | `/api/import/xml` | Reports aus XML-Datei importieren (XPath-Parsing) |
| POST | `/api/import/xml/validate` | XML-Datei gegen XSD validieren |
| GET | `/api/import/sample` | Beispiel-XML abrufen |
| GET | `/api/session/status` | Session-Status abfragen |
| POST | `/api/session/keepalive` | Session verlängern (Touch) |

### API-Konventionen

- **Format:** JSON für alle Request/Response-Bodies
- **Fehler-Responses:** Einheitliches `ErrorResponse`-DTO mit `message`, `timestamp`, `path`
- **Validierung:** Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`) auf allen Requests
- **HTTP-Status:** 200 OK, 201 Created, 204 No Content, 400 Bad Request, 404 Not Found, 500 Internal Server Error
- **Dokumentation:** OpenAPI 3.0 via SpringDoc, erreichbar unter `/swagger-ui.html`
- **Export:** CSV mit Semikolon-Trenner (Schweizer Standard), UTF-8 mit BOM für Excel

---

## 6. Sicherheitsarchitektur

### Session-Management

```
Benutzer aktiv        Benutzer inaktiv
     │                      │
     ▼                      ▼
┌────────────────────────────────────────────────────┐
│              SessionService (Frontend)             │
│                                                    │
│  Activity Detection: mousemove, keydown, click     │
│  Keep-Alive-Interval: alle 5 Minuten              │
│  Countdown-Timer: sekundenweise (Signal-basiert)   │
│  Warning-Schwelle: 5 Minuten vor Ablauf           │
│  Automatischer Logout bei Timeout                 │
└────────────────────────┬───────────────────────────┘
                         │ POST /api/session/keepalive
                         ▼
┌────────────────────────────────────────────────────┐
│              SessionConfig (Backend)               │
│                                                    │
│  Timeout: 30 Minuten (FINMA-Anforderung)          │
│  Konfigurierbar via app.session.timeout-minutes   │
│  Servlet-Session-Management                       │
└────────────────────────────────────────────────────┘
```

### Security Headers (OWASP)

Der `SecurityHeadersFilter` setzt bei jeder HTTP-Response 8 Security-Header:

| Header | Wert | Schutz gegen |
|--------|------|--------------|
| `X-Content-Type-Options` | `nosniff` | MIME-Type-Sniffing |
| `X-Frame-Options` | `DENY` | Clickjacking via iframes |
| `X-XSS-Protection` | `1; mode=block` | XSS (Legacy-Browser) |
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | Downgrade-Angriffe, HTTP-Hijacking |
| `Content-Security-Policy` | `default-src 'self'; script-src 'self'; ...` | XSS, Code-Injection |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Datenleck via Referrer-Header |
| `Permissions-Policy` | `camera=(), microphone=(), geolocation=()` | Ungewollter Zugriff auf Browser-Features |
| `Cache-Control` | `no-cache, no-store, must-revalidate` | Caching sensitiver API-Antworten |

### Audit Trail (AOP)

```
Benutzeraktion (z.B. Report erstellen)
        │
        ▼
ReportController.createReport()
        │
        ├── @AfterReturning von AuditAspect abgefangen
        │
        ▼
AuditAspect.auditReportCreate()
        │
        ▼
AuditService.log(CREATE, "REPORT", reportId, details)
        │
        ▼
AuditLogRepository.save(auditLog)
        │
        ▼
audit_logs Tabelle (unveränderlich)
```

**Abgedeckte Aktionen:**
- `CREATE`: Neuer Report erstellt
- `UPDATE`: Report aktualisiert (Rating-Änderung, Kursziel-Anpassung)
- `DELETE`: Report gelöscht
- `VIEW`: Report angesehen (Compliance-relevante Einsicht)

### FINMA-Compliance-Awareness

Das System demonstriert Compliance-Bewusstsein gemäss FINMA-Rundschreiben 2023/1:

- **Session-Timeout:** 30 Minuten Inaktivitäts-Limit (technische Zugangskontrolle)
- **Audit-Trail:** Vollständige Protokollierung aller Änderungen mit Zeitstempel, Benutzer und IP
- **Security-Header:** OWASP-konforme HTTP-Header auf allen Responses
- **Trennungsprinzip:** DTOs vermeiden unbeabsichtigte Datenexposition (kein JPA-Entity im REST-Response)
- **Compliance-Felder:** `isRestricted`-Flag-Konzept an Reports (erweiterbar für Informationsbarrieren)

---

## 7. DevOps und Deployment

### Docker Compose (Lokal/Demo)

```yaml
services:
  backend:
    build: ./backend          # Multi-Stage Build: Maven → JDK 17 JRE
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: local

  frontend:
    build: ./frontend         # Multi-Stage Build: Node/Angular → nginx
    ports: ["4200:80"]
    depends_on: [backend]
```

### Container-Struktur

```
backend/Dockerfile:
  Stage 1 (build):  maven:3.9-eclipse-temurin-17-alpine
                    mvn clean package -DskipTests
  Stage 2 (runtime): eclipse-temurin:17-jre-alpine
                    java -jar app.jar

frontend/Dockerfile:
  Stage 1 (build):  node:22-alpine
                    npm ci && ng build --configuration=production
  Stage 2 (runtime): nginx:alpine
                    nginx.conf mit Angular-SPA-Routing
```

### CI/CD Pipeline (Jenkins)

```
Checkout → Build Backend (Maven) → Test Backend (JUnit 5 + JaCoCo)
    ↓
Frontend: [Build Angular] parallel [Test Angular (Karma)]
    ↓
Quality Gate (SonarQube, Coverage > 70%)
    ↓
Security Scan (OWASP Dependency-Check, nur main/develop/release)
    ↓
Docker Build & Push (nur main/develop/release)
    ↓
Deploy Staging (automatisch) → Deploy Production (manuelle Freigabe)
```

**Besonderheit Production-Deployment (Banking-Compliance):**
- Manuelle Freigabe durch `devops-leads` oder `release-managers` erforderlich
- Change-Management Ticket-Nummer (CHG-YYYY-NNN) wird protokolliert
- Zero-Downtime Rolling Update via Kubernetes

### GitLab CI (alternativ zu Jenkins)

Die Datei `.gitlab-ci.yml` spiegelt dieselben Stages ab und ist für GitLab-Umgebungen konfiguriert, inkl. Merge-Request-Templates für Feature, Bugfix und Release-Workflows.

### Git-Flow Branching

```
main ──────────────────────────────────────────────────────────────►
  │                                        ▲               ▲
  │          merge                         │               │
  ├──────────►──────────────►──────────────┤     hotfix/   │
  │          develop                       │     critical   │
  │            │                           │     bugfix    │
  │            ├──► feature/report-export ─┤               │
  │            ├──► feature/xml-import ────┤               │
  │            ├──► feature/pdf-export ────┤               │
  │            │                           │               │
  │            └──► release/1.0.0 ─────────────────────────┘
  │
```

**Branch-Typen:**

| Branch | Zweck | Merge nach |
|--------|-------|-----------|
| `main` | Production-Ready Code | — |
| `develop` | Integrations-Branch | main (via release) |
| `feature/*` | Neue Funktionalität | develop |
| `release/*` | Release-Vorbereitung | main + develop |
| `hotfix/*` | Kritische Produktionsfehler | main + develop |

### Environments

| Umgebung | Datenbank | Spring-Profil | URL |
|----------|-----------|--------------|-----|
| local | H2 In-Memory (flüchtig) | `local` | http://localhost:8080 |
| demo | H2 File-based (persistent) | `demo` | Docker Compose |
| staging | H2 File-based / Oracle | `staging` | Kubernetes (intern) |
| production | Oracle DB | `production` | Kubernetes (intern) |

---

## 8. Tech-Stack Referenz

### Geforderte Skills und Nachweis im Prototyp

| Skill / Technologie | Wo im Prototyp | Konkreter Nachweis |
|---------------------|----------------|-------------------|
| **Java** | Backend komplett | Java 17, Records, Pattern Matching, Streams |
| **Spring Boot** | Backend komplett | Spring Boot 3.5, Auto-Configuration, Starter |
| **Spring MVC / REST** | `adapter/in/web/controller/` | 8 REST-Controller, @RestController, @RequestMapping |
| **Hibernate / JPA** | `adapter/out/persistence/` | Entity Mapping, JPQL, @GeneratedValue, @Column |
| **Spring AOP** | `application/aspect/AuditAspect.java` | @Aspect, @AfterReturning, @Pointcut, Audit-Trail |
| **Bean Validation** | DTOs, Controller | @Valid, @NotNull, @NotBlank auf CreateReportRequest |
| **Angular** | Frontend komplett | Angular 21, Standalone Components, Strict Mode |
| **TypeScript** | Frontend komplett | Strict Mode, Typed Models, keine any-Typen |
| **RxJS** | Core Services | Observable, BehaviorSubject, tap, catchError, of |
| **Angular Signals** | SessionService, ThemeService, Components | signal(), computed(), effect() |
| **Angular Router** | `app.routes.ts` | Lazy Loading, authGuard, loadComponent |
| **Angular Forms** | ReportFormComponent | Typed Reactive Forms, FormBuilder |
| **HTML5 / CSS3** | Gesamtes Frontend | 100% Custom CSS, CSS Custom Properties, Grid, Flexbox |
| **SQL** | `resources/data.sql`, JPA Repositories | Demo-Daten, Custom Queries |
| **H2 Database** | `application.yml` | In-Memory für local, File-based für demo |
| **Oracle** | `application.yml` (Profil production) | Konfiguration vorbereitet |
| **Maven** | `pom.xml` | Spring Boot Parent, Dependencies, Build-Plugins |
| **Node.js / NPM** | `package.json`, Angular CLI | npm 11, Angular CLI 21 |
| **Cypress** | `frontend/cypress/e2e/` | E2E-Tests: auth, navigation, reports, securities, dashboard |
| **JUnit 5** | `src/test/java/` | Unit-Tests für Services, Controller, Mapper, Adapter |
| **Mockito** | Backend-Tests | Mock-basierte Unit-Tests ohne Datenbankzugriff |
| **XPath / XML** | `XmlReportParserService.java` | XPath-Parsing, XSD-Validierung, Multipart-Upload |
| **Apache POI** | `ExportService.java` | Excel XLSX-Export mit Formatierung, Auto-Width |
| **OpenPDF** | `PdfExportService.java` | PDF-Export mit farbig hervorgehobenem Rating |
| **OpenAPI / Swagger** | `OpenApiConfig.java`, alle Controller | @Operation, @ApiResponse, @Schema, Swagger UI |
| **Docker** | `Dockerfile` (backend + frontend), `docker-compose.yml` | Multi-Stage Builds, nginx-Konfiguration |
| **Jenkins** | `Jenkinsfile` | Declarative Pipeline, Kubernetes Agent, 9 Stages |
| **GitLab CI** | `.gitlab/` | Merge Request Templates (Feature, Bugfix, Release) |
| **SonarQube** | `sonar-project.properties`, `Jenkinsfile` | Quality Gate, Coverage-Threshold 70% |
| **OWASP** | `SecurityHeadersFilter.java`, `Jenkinsfile` | 8 Security-Header, Dependency-Check in Pipeline |
| **Git-Flow** | Branch-Strategie, `.githooks/` | commit-msg Hook, pre-commit Hook |
| **SAFe / Scrum** | Projektstruktur, Dokumentation | Sprint-basierte Feature-Planung |
| **Hexagonale Architektur** | Backend-Paketstruktur | Ports & Adapters, Domain ohne Framework-Abhängigkeiten |
| **DDD** | Domain-Paket | Entities, Value Objects, Bounded Contexts |
| **i18n** | `public/assets/i18n/` | Mehrsprachigkeit: Deutsch, Englisch, Französisch |
| **Chart.js** | DashboardComponent | Rating-Donut-Chart, Sektor-Balkendiagramm |

### Technologie-Versionen

| Technologie | Version | Rolle |
|-------------|---------|-------|
| Java | 17 (LTS) | Backend-Laufzeitumgebung |
| Spring Boot | 3.5.0 | Backend-Framework |
| Angular | 21.1.x | Frontend-Framework |
| TypeScript | 5.9.x | Frontend-Sprache |
| RxJS | 7.8.x | Reaktive Programmierung |
| PrimeNG | 21.1.x | Tabellen-Komponente (strukturell) |
| Chart.js | 4.5.x | Datenvisualisierung (Dashboard) |
| ngx-translate | 17.x | Internationalisierung |
| Apache POI | 5.2.5 | Excel-Export |
| OpenPDF | 2.0.3 | PDF-Export |
| SpringDoc OpenAPI | 2.8.6 | API-Dokumentation |
| H2 Database | (Spring Boot managed) | Entwicklungs-Datenbank |
| Cypress | 15.x | End-to-End-Tests |
| JUnit 5 | (Spring Boot managed) | Backend-Unit-Tests |
| Lombok | (Spring Boot managed) | Boilerplate-Reduzierung |

---

*Dokumentation erstellt: 2026-02-23*
*Prototyp-Version: 1.0*
*Ziel-Umgebung: Banking Research Portal, Schweizer Bank (wahrscheinlich ZKB)*
