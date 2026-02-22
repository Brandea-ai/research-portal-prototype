# R0014: Frontend Design Overhaul v2.0

**Datum:** 22.02.2026
**Tasks:** Querschnitt (betrifft P0-02, P1-08 bis P1-14, alle Views)
**Status:** ERLEDIGT
**Methode:** Design System Foundation + 2 parallele Sonnet 4.6 Agents in Git-Worktrees

---

## Zusammenfassung

Kompletter visueller Relaunch des Frontends. Swiss Institutional Banking Aesthetic mit Chart.js Dashboard, layered Shadows, besserer Typografie und kohärentem Design-System.

## Motivation

Bisheriges Frontend wirkte generisch und nicht hochwertig genug für eine Schweizer Bankenpräsentation. Spezifische Kritikpunkte:
- Zu kleine Schriften, schwacher Kontrast
- Dashboard ohne Diagramme (nur Zahlen)
- Tabellen und Karten wirkten billig
- Kein einheitliches visuelles System

## Design-System v2.0 (styles.css)

### Farbpalette (Warm Dark)
- `--color-bg: #08090C` (wärmer als #0A0A0A)
- `--color-surface: #0F1117` (Tiefe statt Flachheit)
- `--color-surface-raised: #151820` (NEU: dritte Oberfläche)
- `--color-text-primary: #F0F2F5` (weicher als reines Weiß)
- `--color-positive: #34D399` (Grün statt Blau, semantisch korrekt)

### Typografie-Scale
- 8 Stufen: text-xs (11px) bis text-3xl (36px)
- Inter 300-800 Weights geladen
- JetBrains Mono 400-700 Weights geladen

### Shadow-System
- 6 Stufen: shadow-xs bis shadow-xl
- 2 Glow-Varianten für Accent-Elemente
- shadow-card + shadow-card-hover für alle Karten

### Animationen
- fadeIn, slideUp, slideDown, scaleIn, shimmer, pulse-glow
- Stagger-System (.stagger > *) mit 60ms Delay-Inkrement
- Cubic-Bezier Easing für alle Transitions

## Agent A: Dashboard Overhaul (Chart.js)

### Neue Dependency
- `chart.js` (npm install)

### dashboard.component.ts
- Chart.register(...registerables) im Constructor
- AfterViewInit mit viewChild für Canvas-Referenzen
- ratingDistribution: computed Signal (BUY/HOLD/SELL Counts)
- sectorDistribution: computed Signal (Securities verknüpft per securityId)
- Doughnut-Chart: cutout 72%, #34D399/#94A3B8/#F87171
- Bar-Chart: horizontal (indexAxis: 'y'), #38BDF8 Accent

### dashboard.component.html
- KPI-Row mit 4 Karten (stagger Animation)
- Charts-Row: Doughnut (1/3) + horizontaler Bar (2/3)
- Custom Legend unterhalb des Doughnut
- Latest Reports Grid (6 Spalten)
- Bottom-Row: Top Analysten + Coverage Stats

### dashboard.component.css
- 17 KB, umfassendes Styling
- Swiss Institutional Palette per :host
- Responsive Breakpoints: 1200px, 900px, 768px, 480px

## Agent B: CSS Overhaul (9 Component-Dateien)

### Geänderte Dateien
1. `reports.component.css` — Tabelle als Card, Gradient-Button, Filter-Bar
2. `securities.component.css` — Card-Treatment, Ticker in Accent
3. `report-detail.component.css` — slideUp, max-width 1000px, Karten-System
4. `report-form.component.css` — slideUp, Gradient-Submit, Focus-Glow
5. `sidebar.component.css` — font-weight 800 Logo, surface-hover
6. `topbar.component.css` — text-lg Titel, text-xs Role-Badge
7. `app.css` — margin-left Transition
8. `login.component.css` — Radial Gradient BG, shadow-lg Card, 48px Button
9. `analysts.component.css` — slideUp, shadow-card

## angular.json

- anyComponentStyle Budget: 10kB/16kB → 20kB/32kB (Dashboard CSS)

## Build-Ergebnis

- Initial Bundle: 285.77 KB (+3 KB durch Design-Tokens)
- Dashboard Chunk: 230.92 KB (Chart.js inklusive)
- Errors: 0
- Warnings: 0

## Geänderte Dateien (komplett)

| Datei | Aktion | Agent |
|-------|--------|-------|
| styles.css | REWRITE | Main |
| index.html | UPDATE (Fonts) | Main |
| angular.json | UPDATE (Budget) | Main |
| dashboard.component.ts | REWRITE | Agent A |
| dashboard.component.html | REWRITE | Agent A |
| dashboard.component.css | REWRITE | Agent A |
| reports.component.css | REWRITE | Agent B |
| securities.component.css | REWRITE | Agent B |
| report-detail.component.css | REWRITE | Agent B |
| report-form.component.css | REWRITE | Agent B |
| sidebar.component.css | REWRITE | Agent B |
| topbar.component.css | REWRITE | Agent B |
| app.css | UPDATE | Agent B |
| login.component.css | REWRITE | Agent B |
| analysts.component.css | REWRITE | Agent B |

## Rollback-Plan

```bash
git revert HEAD   # Commit rückgängig machen
```

## Nächster Schritt

i18n (DE/EN) oder P2-16 Backend Unit Tests
