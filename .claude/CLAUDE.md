# CLAUDE.md: Research Portal Prototype 1.0

> Projektspezifische Standards, Tech-Vorgaben und Wissensdatenbank.
> Diese Datei ist die SINGLE SOURCE OF TRUTH für alle technischen Entscheidungen.

---

## Projekt-Kontext

**Was:** Banking Research Portal Prototyp für Equity/Fixed-Income-Analysten
**Warum:** Demonstration von Full-Stack-Kompetenz für Hays AG Projekt 2970720
**Für wen:** DevOps-Team "Research" bei einer renommierten Schweizer Bank (wahrscheinlich ZKB)
**Stack-Match:** Angular + Spring Boot + Oracle/H2 + Hibernate/JPA + Maven + Cypress

---

## Wahrscheinlicher Endkunde: Zürcher Kantonalbank (ZKB)

**Evidenzbasis:**
- ZKB nutzt nachweislich Java/Spring Boot + Angular + SAFe + Oracle
- Über 50 Research-Experten, eigenes Portal (research.zkb.ch)
- "SLX" = wahrscheinlich internes ZKB-Framework
- DevOps-Kultur: "You build it, you run it"
- SAFe als Orchestrierungs-Framework bestätigt
- Oracle-Datenbanken für Kernbankensysteme

**Implikationen für den Prototyp:**
- Konservativer, seriöser Auftritt (Kantonalbank, nicht Startup)
- Schweizer Wertschriften-Terminologie verwenden
- FINMA-Compliance-Bewusstsein zeigen
- Research-Workflow abbilden (Beobachten, Analysieren, Publizieren)

---

## Design-System

### Farbschema v3.0 — Theme System (STRIKT einhalten)

**Theme-Mechanismus:** `[data-theme]` Attribut auf `<html>`, gesteuert durch ThemeService (Signal-basiert)
**3 Modi:** Dark (Default), Light, System (folgt OS-Präferenz)

```css
/* === DARK THEME (Default) === */
--color-bg:             #0B0D12;
--color-surface:        #12151C;
--color-surface-raised: #181C26;
--color-surface-alt:    #1E2330;
--color-surface-hover:  #252A38;
--color-border:         #1E2330;
--color-border-light:   #2A3040;
--color-text-primary:   #ECEEF2;
--color-text-secondary: #A0AAB8;
--color-text-muted:     #6C7A8D;
--color-text-dim:       #4A5568;
--color-accent:         #4DA3FF;
--color-accent-hover:   #3B8EE8;
--color-positive:       #2DD4A0;
--color-negative:       #F87171;
--color-neutral:        #94A3B8;
--color-warning:        #FBBF24;

/* === LIGHT THEME === */
--color-bg:             #F5F6F8;
--color-surface:        #FFFFFF;
--color-surface-raised: #F0F1F3;
--color-surface-alt:    #E8EAF0;
--color-surface-hover:  #DFE2EA;
--color-border:         #D8DCE4;
--color-border-light:   #C0C6D0;
--color-text-primary:   #1A1D24;
--color-text-secondary: #4A5060;
--color-text-muted:     #6C7688;
--color-text-dim:       #9AA0B0;
--color-accent:         #0062CC;
--color-accent-hover:   #004FA3;
--color-positive:       #00875A;
--color-negative:       #CC2936;
--color-neutral:        #5A6474;
--color-warning:        #C27800;
```

### Shadow-System (Theme-abhängig)
```css
/* Dark */
--shadow-card:       0 2px 8px rgba(0,0,0,0.30), 0 0 0 1px rgba(255,255,255,0.04);
--shadow-card-hover: 0 8px 24px rgba(0,0,0,0.40), 0 0 0 1px rgba(255,255,255,0.06);

/* Light */
--shadow-card:       0 1px 4px rgba(0,0,0,0.06), 0 0 0 1px rgba(0,0,0,0.04);
--shadow-card-hover: 0 4px 16px rgba(0,0,0,0.10), 0 0 0 1px rgba(0,0,0,0.06);
```

### Accent-Regel (Bloomberg-Prinzip)
Accent wird NUR verwendet für:
- Active/Selected States in der Navigation
- CTA-Buttons (primär)
- Fokus-Ringe bei Formularen
- Ticker-Symbole und Accent-Highlights

Positive (Grün) wird für BUY-Ratings und positive Deltas verwendet.

NIEMALS Accent für: großflächige Hintergründe, dekorative Elemente

### Typografie

```css
/* Fonts (erweiterte Weights) */
--font-ui:   'Inter', system-ui, -apple-system, sans-serif;  /* 300-800 */
--font-data: 'JetBrains Mono', 'SF Mono', 'Courier New', monospace;  /* 400-700 */

/* Typography Scale */
--text-xs:   0.6875rem;   /* 11px — Labels, Captions */
--text-sm:   0.8125rem;   /* 13px — Sekundärtext */
--text-base: 0.9375rem;   /* 15px — Body */
--text-md:   1rem;        /* 16px — Emphasis */
--text-lg:   1.125rem;    /* 18px — Section Titles */
--text-xl:   1.375rem;    /* 22px — Page Titles */
--text-2xl:  1.75rem;     /* 28px — Hero Numbers */
--text-3xl:  2.25rem;     /* 36px — KPI Values */

/* Alle Zahlen/Finanzdaten */
font-family: var(--font-data);
font-variant-numeric: tabular-nums;
```

### Animationen
- `slideUp`: Page-Enter-Animation für alle Views
- `stagger`: .stagger > * mit 60ms nth-child Delays
- `pulse-glow`: Subtile Aufmerksamkeit auf Rating-Karten
- Cubic-Bezier Transitions: 150ms (fast), 250ms (normal), 400ms (slow)

### KEINE Icons
- Kein Icon-Set (kein Lucide, kein Material Icons, kein FontAwesome)
- Stattdessen: Typografie, Kontraste, Whitespace, Borders
- Rating-Anzeige: Text ("BUY", "SELL") mit Farbcodierung
- Navigation: Text-Labels
- Sortierung: Unicode-Pfeile (U+25B2, U+25BC)

---

## Architektur-Standards

### Frontend (Angular)

**Pflicht:**
- Standalone Components (KEINE NgModules)
- Lazy-loaded Routes
- Signals für UI-State
- RxJS für HTTP-Calls, Event-Streams, Orchestrierung
- Typed Reactive Forms
- OnPush Change Detection
- PrimeNG nur als strukturelle Basis für p-table
- 100% Custom CSS darüber (kein PrimeNG Theme)

**Verboten:**
- NgModules
- Zone.js-abhängiger Code wo vermeidbar
- `any` Typ
- Inline Styles
- Tailwind, Bootstrap oder andere CSS-Frameworks

### Backend (Spring Boot)

**Pflicht:**
- Hexagonale Architektur (Ports & Adapters)
- Domain-Schicht OHNE Framework-Abhängigkeiten
- DTOs für API-Kommunikation (nie JPA-Entities direkt exponieren)
- Repository-Pattern über JPA
- Service-Layer für Business-Logik
- Bean Validation für Input
- Global Exception Handler

**Verboten:**
- JPA-Entities in REST Responses
- Business-Logik in Controllern
- Spring-Annotationen in der Domain-Schicht
- `@Autowired` auf Feldern (nur Constructor Injection)

### Datenbank

- **Lokal:** H2 In-Memory (Profil: `local`)
- **Demo:** H2 File-based (Profil: `demo`)
- **Prod-Ready:** Oracle-Config vorbereitet (Profil: `production`)
- **Migrations:** Flyway
- **Demo-Daten:** Schweizer Wertschriften (NESN, NOVN, UBSG, ZURN, ROG, SREN, ABBN, LONN)

---

## Datenmodell (Kern-Entities)

### Analyst
- id, name, title, department, email
- coverageUniverse (Ticker-Array)
- starRating (1-5), accuracy12m (%)

### Security (Wertschrift)
- id, ticker, isin, name
- assetClass (EQUITY, FIXED_INCOME, DERIVATIVES, MACRO)
- sector, industry, exchange, currency, marketCap

### ResearchReport
- id, analystId, securityId
- publishedAt, reportType (INITIATION, UPDATE, QUARTERLY, FLASH, DEEP_DIVE, CREDIT)
- title, executiveSummary, fullText
- rating, previousRating, ratingChanged
- targetPrice, previousTarget, currentPrice, impliedUpside
- riskLevel (LOW, MEDIUM, HIGH, SPECULATIVE)
- investmentCatalysts[], keyRisks[], tags[]

### RatingHistory
- id, securityId, analystId, date, rating, targetPrice, reportId

### FinancialEstimates
- fiscalYear, revenue, revenueGrowth, ebitda, ebitdaMargin
- eps, peRatio, evEbitda, dividendYield

---

## Geforderte Skills und wo sie demonstriert werden

| Geforderte Skill | Wo im Prototyp | Nachweis |
|-----------------|----------------|----------|
| Java + Spring Boot | Backend komplett | Hexagonale Architektur, REST API |
| Hibernate/JPA | Persistence Adapter | Entity Mapping, JPQL, Relationships |
| Angular + TypeScript | Frontend komplett | Standalone Components, Strict Mode |
| RxJS | HTTP Services, Dashboard | Observables, Operators, Subjects |
| HTML5 + CSS3 | Gesamtes Styling | 100% Custom CSS, kein Framework |
| SQL | data.sql, Repositories | Demo-Daten, Custom Queries |
| Maven | pom.xml | Multi-Module Build |
| Cypress | cypress/e2e/ | Login, CRUD, Filter Tests |
| XPath | XML Report Import | Report-Parser Service |
| Node.js + NPM | Frontend Build | Angular CLI, Package Management |
| TDD | Backend Tests | JUnit 5, Mockito |
| DDD | Package-Struktur | Bounded Contexts, Value Objects |
| Git-Flow | Branch-Strategie | Feature Branches, Release Tags |
| SAFe/Scrum | README, Dokumentation | Sprint-Planung, PI-Konzept |
| CI/CD | Jenkinsfile, .gitlab-ci.yml | Build, Test, Deploy Pipeline |
| DevOps | Dockerfile, docker-compose | Container-Konfiguration |

---

## Schweizer Banking-Kontext

### Terminologie (im Prototyp verwenden)
- "Wertschriften" (nicht "Securities" oder "Aktien")
- "Kurs" (nicht "Preis")
- "Analysten" (nicht "Researcher")
- "Empfehlung" (neben Rating)
- "Kursziel" (neben Target Price)
- CHF als Standard-Währung

### FINMA-Awareness (im Code demonstrieren)
- Audit-Trail Konzept (wer hat wann was publiziert)
- Role-Based Access Control Grundstruktur
- Compliance-Flag an Reports (isRestricted)
- Activity Logging vorbereitet

### Schweizer Wertschriften für Demo-Daten

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
| GIVN | Givaudan SA | Materials |
| SIKA | Sika AG | Materials |

---

## Session-Protokoll

### Bei jeder neuen Session
Verbindliche Lesereihenfolge in `.claude/referenz/START.md` befolgen.
Das ist die EINZIGE Stelle, die die Reihenfolge definiert.

### Nach jeder Arbeitseinheit
Verbindlicher Abschluss-Zyklus aus `_claude-tasks/RULES.md` (R4) befolgen:
1. Report erstellen (`_claude-tasks/reports/RXXXX-[THEMA].md`)
2. `_claude-tasks/STATUS.md` aktualisieren
3. `.claude/referenz/CURRENT-TASK.md` aktualisieren
4. `.claude/referenz/PROGRESS-LOG.md` ergänzen (neueste oben)
5. `.claude/referenz/CHECKLIST.md` Status prüfen
6. Commit vorschlagen (nur mit Freigabe durch Armend)

### Querverweise
- Lesereihenfolge: `.claude/referenz/START.md`
- Arbeitsregeln: `_claude-tasks/RULES.md`
- Denkprinzipien: `.claude/GOLDENE-REGELN.md`
- Anforderungen: `.claude/referenz/REQUIREMENTS.md`
- Entscheidungen: `.claude/referenz/DECISION-LOG.md`

---

## Qualitäts-Gates (vor jedem Commit)

- [ ] Frontend: `ng build` fehlerfrei
- [ ] Backend: `mvn clean package` fehlerfrei
- [ ] Keine TypeScript `any` Typen
- [ ] Keine Konsolen-Fehler zur Laufzeit
- [ ] Custom CSS: Kein PrimeNG Theme sichtbar
- [ ] Responsive: Desktop + Tablet + Mobile geprüft
- [ ] Cypress Tests: Alle grün
- [ ] Report erstellt
- [ ] Steuerungsdateien aktuell
