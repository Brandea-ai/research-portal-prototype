# Architektur: Research Portal Prototype 1.0

> Hexagonale Architektur (Ports & Adapters) + Domain-Driven Design
> Gold-Standard für Banking-Applikationen 2026

---

## Architektur-Übersicht

```
┌─────────────────────────────────────────────────────────────┐
│                        FRONTEND                              │
│                    Angular 17+ (SPA)                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  Dashboard   │  │  Research   │  │ Securities  │         │
│  │  Feature     │  │  Feature    │  │  Feature    │         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         │                │                │                  │
│  ┌──────┴────────────────┴────────────────┴──────┐          │
│  │              Core Services (RxJS)              │          │
│  │   HTTP Interceptor ←→ State Management         │          │
│  └────────────────────┬──────────────────────────┘          │
└───────────────────────┼──────────────────────────────────────┘
                        │ REST API (JSON)
                        │ Port 4200 → Port 8080
┌───────────────────────┼──────────────────────────────────────┐
│                       │        BACKEND                        │
│                       ▼                                       │
│  ┌──────────────────────────────────────────┐                │
│  │           Adapter: REST (IN)              │                │
│  │   ResearchReportController                │                │
│  │   SecurityController                      │                │
│  │   AnalystController                       │                │
│  └──────────────────┬───────────────────────┘                │
│                     │                                         │
│  ┌──────────────────┴───────────────────────┐                │
│  │         Application: Use Cases            │                │
│  │   PublishReportService                    │                │
│  │   SearchReportsService                    │                │
│  │   ManageSecuritiesService                 │                │
│  └──────────────────┬───────────────────────┘                │
│                     │                                         │
│  ┌──────────────────┴───────────────────────┐                │
│  │              DOMAIN (Kern)                │                │
│  │   ┌─────────────────────────────┐        │                │
│  │   │ Models: ResearchReport,     │        │                │
│  │   │  Analyst, Security,         │        │                │
│  │   │  RatingHistory              │        │                │
│  │   ├─────────────────────────────┤        │                │
│  │   │ Ports IN:                   │        │                │
│  │   │  PublishReportUseCase       │        │                │
│  │   │  SearchReportsUseCase       │        │                │
│  │   ├─────────────────────────────┤        │                │
│  │   │ Ports OUT:                  │        │                │
│  │   │  ReportRepository           │        │                │
│  │   │  SecurityRepository         │        │                │
│  │   │  NotificationPort           │        │                │
│  │   └─────────────────────────────┘        │                │
│  │   KEINE Spring/JPA Imports hier!          │                │
│  └──────────────────┬───────────────────────┘                │
│                     │                                         │
│  ┌──────────────────┴───────────────────────┐                │
│  │        Adapter: Persistence (OUT)         │                │
│  │   JPA Entities (separate von Domain!)     │                │
│  │   JPA Repositories                        │                │
│  │   Entity-to-Domain Mapper                 │                │
│  │   H2 (lokal) / Oracle (Prod)              │                │
│  └──────────────────────────────────────────┘                │
└───────────────────────────────────────────────────────────────┘
```

---

## Backend Package-Struktur (DDD + Hexagonal)

```
com.research.portal/
├── ResearchPortalApplication.java      # Spring Boot Main
│
├── domain/                             # REINE Business-Logik
│   ├── model/
│   │   ├── Analyst.java               # Domain Entity
│   │   ├── Security.java              # Domain Entity
│   │   ├── ResearchReport.java        # Aggregate Root
│   │   ├── RatingHistory.java         # Domain Entity
│   │   ├── FinancialEstimates.java    # Value Object
│   │   ├── Rating.java                # Enum
│   │   ├── ReportType.java            # Enum
│   │   ├── AssetClass.java            # Enum
│   │   └── RiskLevel.java             # Enum
│   └── port/
│       ├── in/                         # Use-Case Interfaces
│       │   ├── PublishReportUseCase.java
│       │   ├── SearchReportsUseCase.java
│       │   ├── ManageReportsUseCase.java
│       │   └── GetSecuritiesUseCase.java
│       └── out/                        # Repository Interfaces
│           ├── ReportRepository.java
│           ├── SecurityRepository.java
│           ├── AnalystRepository.java
│           └── RatingHistoryRepository.java
│
├── application/                        # Use-Case Implementierungen
│   └── service/
│       ├── PublishReportService.java
│       ├── SearchReportsService.java
│       ├── ManageReportsService.java
│       └── GetSecuritiesService.java
│
├── adapter/
│   ├── in/
│   │   └── rest/                       # REST Controllers
│   │       ├── ResearchReportController.java
│   │       ├── SecurityController.java
│   │       ├── AnalystController.java
│   │       ├── dto/                    # Request/Response DTOs
│   │       │   ├── ReportResponse.java
│   │       │   ├── ReportCreateRequest.java
│   │       │   ├── SecurityResponse.java
│   │       │   └── AnalystResponse.java
│   │       └── mapper/
│   │           └── DtoMapper.java
│   │
│   └── out/
│       └── persistence/                # JPA Adapter
│           ├── entity/                 # JPA Entities (NICHT Domain!)
│           │   ├── ReportJpaEntity.java
│           │   ├── SecurityJpaEntity.java
│           │   ├── AnalystJpaEntity.java
│           │   └── RatingHistoryJpaEntity.java
│           ├── repository/             # Spring Data JPA
│           │   ├── ReportJpaRepository.java
│           │   ├── SecurityJpaRepository.java
│           │   ├── AnalystJpaRepository.java
│           │   └── RatingHistoryJpaRepository.java
│           ├── mapper/
│           │   └── PersistenceMapper.java
│           └── adapter/
│               ├── ReportRepositoryAdapter.java
│               ├── SecurityRepositoryAdapter.java
│               └── AnalystRepositoryAdapter.java
│
└── config/                             # Spring Configuration
    ├── CorsConfig.java
    ├── SecurityConfig.java
    └── GlobalExceptionHandler.java
```

---

## Frontend Struktur (Angular Standalone)

```
src/app/
├── app.component.ts                    # Root Component
├── app.config.ts                       # App Configuration (provideRouter, provideHttpClient)
├── app.routes.ts                       # Top-Level Routes (Lazy Loading)
│
├── core/                               # Singleton Services, Guards, Interceptors
│   ├── services/
│   │   ├── auth.service.ts             # JWT Token Management
│   │   ├── api.service.ts              # Base HTTP Service
│   │   └── notification.service.ts     # Toast/Alert Service
│   ├── guards/
│   │   └── auth.guard.ts              # Route Protection
│   ├── interceptors/
│   │   ├── auth.interceptor.ts        # JWT Token Injection
│   │   └── error.interceptor.ts       # Global Error Handling
│   └── models/
│       ├── analyst.model.ts
│       ├── security.model.ts
│       ├── report.model.ts
│       ├── rating.enum.ts
│       └── report-type.enum.ts
│
├── shared/                             # Wiederverwendbare Komponenten
│   ├── components/
│   │   ├── data-table/                # Custom Table Wrapper
│   │   ├── detail-panel/             # Rechtes Detail-Panel
│   │   └── filter-bar/               # Filter-Chips
│   ├── pipes/
│   │   ├── currency-format.pipe.ts   # CHF 118.50
│   │   ├── percentage.pipe.ts        # +14.2%
│   │   └── date-format.pipe.ts       # 22.02.2026
│   └── directives/
│       └── rating-color.directive.ts  # BUY=Accent, SELL=Rot
│
├── features/
│   ├── auth/
│   │   ├── login/
│   │   │   ├── login.component.ts
│   │   │   └── login.component.css
│   │   └── auth.routes.ts
│   │
│   ├── dashboard/
│   │   ├── dashboard.component.ts
│   │   ├── dashboard.component.css
│   │   ├── components/
│   │   │   ├── kpi-card.component.ts
│   │   │   └── recent-reports.component.ts
│   │   ├── services/
│   │   │   └── dashboard.service.ts   # RxJS Observables
│   │   └── dashboard.routes.ts
│   │
│   ├── research/
│   │   ├── research.component.ts       # Container: Table + Detail
│   │   ├── research.component.css
│   │   ├── components/
│   │   │   ├── report-table.component.ts
│   │   │   ├── report-detail.component.ts
│   │   │   ├── report-form.component.ts
│   │   │   └── report-filters.component.ts
│   │   ├── services/
│   │   │   ├── report.service.ts       # HTTP + State
│   │   │   └── report-state.service.ts # BehaviorSubject
│   │   └── research.routes.ts
│   │
│   └── securities/
│       ├── securities.component.ts
│       ├── securities.component.css
│       ├── components/
│       │   ├── security-table.component.ts
│       │   └── security-detail.component.ts
│       ├── services/
│       │   └── security.service.ts
│       └── securities.routes.ts
│
└── layout/
    ├── sidebar/
    │   ├── sidebar.component.ts
    │   └── sidebar.component.css
    ├── topbar/
    │   ├── topbar.component.ts
    │   └── topbar.component.css
    └── shell/
        ├── shell.component.ts          # 3-Zonen Layout
        └── shell.component.css
```

---

## UI Layout (3-Zonen Bloomberg-inspiriert)

```
┌──────────────────────────────────────────────────────────────────┐
│  TOP BAR                                                         │
│  ┌──────────────────────────┐  ┌──────────┐  ┌────────────────┐ │
│  │ RESEARCH PORTAL          │  │ Search...│  │ A. Amerllahu ▾ │ │
│  └──────────────────────────┘  └──────────┘  └────────────────┘ │
├────────────┬─────────────────────────────────┬───────────────────┤
│            │                                 │                   │
│  SIDEBAR   │       MAIN CONTENT              │   DETAIL PANEL    │
│  ~220px    │       flex-grow: 1              │   ~320px          │
│            │                                 │                   │
│  DASHBOARD │  ┌─────────────────────────┐    │  ┌─────────────┐ │
│            │  │ Filters: [Equity] [BUY] │    │  │ NESN        │ │
│  RESEARCH  │  ├─────────────────────────┤    │  │ Nestlé SA   │ │
│  ● aktiv   │  │ Date   Ticker  Rating   │    │  ├─────────────┤ │
│            │  │ 22.02  NESN    BUY      │    │  │ Rating      │ │
│  SECURITIES│  │ 22.02  NOVN    HOLD     │    │  │ BUY         │ │
│            │  │ 21.02  UBSG    BUY      │    │  │             │ │
│  ────────  │  │ 21.02  ROG     SELL     │    │  │ Kursziel    │ │
│            │  │ 20.02  ZURN    HOLD     │    │  │ CHF 118.50  │ │
│  FILTER    │  │ ...                     │    │  │             │ │
│            │  └─────────────────────────┘    │  │ Upside      │ │
│  Asset     │                                 │  │ +14.2%      │ │
│  ○ Equity  │  Showing 25 of 1,247            │  │             │ │
│  ○ Fixed   │                                 │  │ Analyst     │ │
│  ○ Macro   │                                 │  │ M. Weber    │ │
│            │                                 │  └─────────────┘ │
├────────────┴─────────────────────────────────┴───────────────────┤
│  v1.0.0 Prototype  │  Last Sync: 22.02.2026  │  FINMA Compliant │
└──────────────────────────────────────────────────────────────────┘
```

---

## Datenfluss

```
User klickt "BUY" Filter
    │
    ▼
FilterBar.component → emit filterChange
    │
    ▼
Research.component → report-state.service.updateFilter('rating', 'BUY')
    │
    ▼
ReportStateService (BehaviorSubject)
    │
    ├── HTTP Request: GET /api/reports?rating=BUY
    │       │
    │       ▼
    │   ReportController.searchReports(rating=BUY)
    │       │
    │       ▼
    │   SearchReportsService.search(criteria)
    │       │
    │       ▼
    │   ReportRepository.findByCriteria(criteria)
    │       │
    │       ▼
    │   ReportJpaRepository (Spring Data)
    │       │
    │       ▼
    │   H2 Database → SQL: SELECT ... WHERE rating = 'BUY'
    │       │
    │       ▼
    │   JPA Entity → PersistenceMapper → Domain Model
    │       │
    │       ▼
    │   Domain Model → DtoMapper → ReportResponse DTO
    │       │
    │       ▼
    │   JSON Response
    │
    ▼
ReportStateService.next(filteredReports)
    │
    ├── ReportTable.component (async pipe) → Tabelle aktualisiert
    └── ReportDetail.component → Detail-Panel aktualisiert (wenn Report selektiert)
```

---

## Abhängigkeiten

### Frontend
```json
{
  "@angular/core": "^17.x",
  "@angular/forms": "^17.x",
  "@angular/router": "^17.x",
  "rxjs": "^7.x",
  "primeng": "^17.x",
  "primeicons": "NICHT INSTALLIEREN (keine Icons)"
}
```

### Backend
```xml
<dependencies>
    <dependency>spring-boot-starter-web</dependency>
    <dependency>spring-boot-starter-data-jpa</dependency>
    <dependency>spring-boot-starter-validation</dependency>
    <dependency>h2 (runtime)</dependency>
    <dependency>flyway-core</dependency>
    <dependency>lombok (optional)</dependency>
    <dependency>spring-boot-starter-test</dependency>
</dependencies>
```
