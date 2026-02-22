# R0007: P0-06 Frontend Grundstruktur

**Datum:** 22.02.2026
**Phase:** P0 Fundament
**Status:** Abgeschlossen

---

## Was wurde gemacht

### TypeScript Models (3 Dateien)
- `analyst.model.ts`: Analyst-Interface (id, name, title, department, email, coverageUniverse, starRating, accuracy12m)
- `security.model.ts`: Security-Interface (ticker, isin, name, assetClass, sector, industry, exchange, currency, marketCap)
- `report.model.ts`: Report-Interface + CreateReportRequest-Interface (alle Felder inkl. Enums als Strings)
- `index.ts`: Barrel-Export mit `export type` (isolatedModules-konform)

### HTTP Services (3 Dateien)
- `report.service.ts`: 7 Methoden (getAll, getById, getByAnalyst, getBySecurity, create, update, delete)
- `security.service.ts`: 3 Methoden (getAll, getById, getByTicker)
- `analyst.service.ts`: 2 Methoden (getAll, getById)
- Alle nutzen `HttpClient` + `Observable<T>`, environment.apiUrl als Base-URL

### Layout-Komponenten (2 Dateien)
- `sidebar.component`: Fixed 220px Sidebar, 4 Navigation-Links mit RouterLinkActive, Version-Footer
- `topbar.component`: 48px Höhe, dynamischer Seitentitel via `input()` Signal

### Feature-Komponenten (4 Dateien)
- `dashboard.component`: 4 KPI-Cards + 5 neueste Reports, ratingChangedCount als computed Signal
- `reports.component`: Datentabelle mit 7 Spalten, Rating-Farbcodierung, Upside-Anzeige
- `securities.component`: Datentabelle mit 7 Spalten, Ticker in Accent-Farbe, formatMarketCap Helper
- `analysts.component`: Card-Grid, Unicode-Sterne, Coverage-Tags, Accuracy-Statistik

### App Shell
- `app.html`: Layout-Shell mit Sidebar + Content-Area (Topbar + router-outlet)
- `app.ts`: Dynamischer Seitentitel via Router-Events → `toSignal()` → `computed()`
- `app.css`: Flexbox-Layout mit `margin-left: var(--sidebar-width)`
- `app.config.ts`: `provideHttpClient()` hinzugefügt

### Routing
- `app.routes.ts`: 4 Lazy-loaded Routes + Redirect ('' → dashboard, ** → dashboard)

### Konfiguration
- `tsconfig.json`: Path-Aliases (@core/*, @features/*, @shared/*)

## Build-Ergebnis

- **Initial Bundle:** 264 KB (74 KB transfer)
- **Lazy Chunks:** 4 Feature-Komponenten separat geladen
- **Fehler:** 0
- **Warnungen:** 0

## Behobene Probleme

| Problem | Ursache | Lösung |
|---------|---------|--------|
| `router` used before initialization | `toSignal()` als Feld-Initialisierer + Constructor Injection | `inject(Router)` statt Constructor |
| `isolatedModules` Export-Fehler | Barrel-File re-exportiert Interfaces mit `export {}` | `export type {}` verwenden |
| Arrow Function in Template | Angular Template Parser erlaubt kein `=>` | `computed()` Signal im Component |
| Unused DecimalPipe import | Securities-Template nutzt Method statt Pipe | Import entfernt |

## Architektur-Entscheidungen

- **inject() statt Constructor für Router**: Moderne Angular-Praxis, ermöglicht `toSignal()` in Feld-Initialisierern
- **Computed Signals für abgeleitete Werte**: `ratingChangedCount` ist reaktiv und effizient
- **Lazy Loading für alle Features**: Minimiert Initial Bundle, jede Seite nur bei Bedarf geladen
- **Kein PrimeNG-Template**: 100% Custom HTML/CSS für maximale Kontrolle und CSS3-Kompetenz-Beweis

## Dateien erstellt/geändert

**Erstellt (18):**
- `frontend/src/app/core/models/analyst.model.ts`
- `frontend/src/app/core/models/security.model.ts`
- `frontend/src/app/core/models/report.model.ts`
- `frontend/src/app/core/models/index.ts`
- `frontend/src/app/core/services/report.service.ts`
- `frontend/src/app/core/services/security.service.ts`
- `frontend/src/app/core/services/analyst.service.ts`
- `frontend/src/app/core/services/index.ts`
- `frontend/src/app/layout/sidebar/sidebar.component.ts`
- `frontend/src/app/layout/sidebar/sidebar.component.html`
- `frontend/src/app/layout/sidebar/sidebar.component.css`
- `frontend/src/app/layout/topbar/topbar.component.ts`
- `frontend/src/app/layout/topbar/topbar.component.html`
- `frontend/src/app/layout/topbar/topbar.component.css`
- `frontend/src/app/features/dashboard/dashboard.component.ts`
- `frontend/src/app/features/dashboard/dashboard.component.html`
- `frontend/src/app/features/dashboard/dashboard.component.css`
- `frontend/src/app/features/reports/reports.component.ts`
- `frontend/src/app/features/reports/reports.component.html`
- `frontend/src/app/features/reports/reports.component.css`
- `frontend/src/app/features/securities/securities.component.ts`
- `frontend/src/app/features/securities/securities.component.html`
- `frontend/src/app/features/securities/securities.component.css`
- `frontend/src/app/features/analysts/analysts.component.ts`
- `frontend/src/app/features/analysts/analysts.component.html`
- `frontend/src/app/features/analysts/analysts.component.css`

**Geändert (4):**
- `frontend/src/app/app.html` (Placeholder → Layout Shell)
- `frontend/src/app/app.ts` (RouterOutlet → + Sidebar, Topbar, inject())
- `frontend/src/app/app.css` (leer → Flexbox Layout)
- `frontend/src/app/app.config.ts` (+ provideHttpClient)
- `frontend/src/app/app.routes.ts` (4 lazy-loaded Routes)
- `frontend/tsconfig.json` (+ path aliases)
