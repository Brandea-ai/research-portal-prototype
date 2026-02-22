# Aktueller Arbeitsstand

> Wird nach jeder Arbeitseinheit aktualisiert. Referenz: `_claude-tasks/STATUS.md`

**Letzte Aktualisierung:** 22.02.2026
**Aktueller Task:** P0-06 Frontend Grundstruktur
**Status:** Abgeschlossen

---

## Was wurde zuletzt gemacht

### Session 3 (22.02.2026): P0-06 Frontend Grundstruktur

1. 3 TypeScript Models (Analyst, Security, Report) + Barrel-Export mit `export type`
2. 3 HTTP Services (ReportService, SecurityService, AnalystService) mit Observable<T>
3. app.config.ts erweitert: `provideHttpClient()` hinzugefügt
4. tsconfig.json: Path-Aliases (@core/*, @features/*, @shared/*)
5. Sidebar-Komponente: Fixed 220px, 4 Nav-Links, RouterLinkActive
6. Topbar-Komponente: Dynamischer Titel via `input()` Signal
7. Dashboard: 4 KPI-Cards + neueste 5 Reports, computed Signal für Rating-Änderungen
8. Reports: Datentabelle mit 7 Spalten, Rating-Farbcodierung
9. Securities: Datentabelle, Ticker in Accent, formatMarketCap Helper
10. Analysts: Card-Grid mit Unicode-Sternen, Coverage-Tags
11. App Shell: Sidebar + Content-Area mit Topbar + router-outlet
12. 4 Lazy-loaded Routes, 264 KB Initial Bundle
13. 4 Build-Fehler behoben (inject() statt Constructor, export type, computed statt Arrow in Template)

## Was steht als Nächstes an

1. **P0-07:** Login-Seite (Mock-Auth mit Rolle-basiertem Zugang)
2. **P1-08:** Dashboard View (Erweiterung mit Live-Daten)
3. **P1-09:** Research Reports Tabelle (Sortierung, Filter)

## Offene Fragen / Blocker

Keine aktuell.

## Kontext für die nächste Session

P0-06 ist komplett. Das Frontend hat:
- 4 Feature-Seiten die live Daten vom Backend laden
- Layout-Shell mit Sidebar-Navigation und dynamischem Topbar-Titel
- Alle Services nutzen `environment.apiUrl` → funktioniert lokal und auf Vercel
- Build ist clean (0 Fehler, 0 Warnungen)
- Angular 21 Standalone Components mit Signals

JAVA_HOME muss bei jedem Bash-Befehl gesetzt werden:
`export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"`

Nächster Schritt: P0-07 Login-Seite.
