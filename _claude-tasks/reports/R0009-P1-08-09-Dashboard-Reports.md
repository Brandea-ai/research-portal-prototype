# R0009: P1-08 Dashboard View + P1-09 Reports Tabelle (Parallel)

**Datum:** 22.02.2026
**Phase:** P1 Kernfeatures
**Status:** Abgeschlossen
**Methode:** Parallele Subagents in isolierten Worktrees

---

## P1-08: Dashboard View

### Geänderte Dateien
- `features/dashboard/dashboard.component.ts` - 5 neue computed() Signals
- `features/dashboard/dashboard.component.html` - 2 neue Sektionen
- `features/dashboard/dashboard.component.css` - 347 Zeilen, responsive

### Features
- **KPI-Cards verbessert:** Zahlen in font-data (2.25rem), Untertitel, Hover-Effekt
- **Neueste Reports:** Sortiert nach Datum (neueste zuerst), Typ-Badge, Rating-Farben
- **Top Analysten (NEU):** Top 3 nach accuracy12m, Rang-Nummer, Unicode-Sterne
- **Coverage-Übersicht (NEU):** Sektoren-Anzahl, Ticker abgedeckt, Avg. Accuracy

## P1-09: Reports Tabelle

### Geänderte Dateien
- `features/reports/reports.component.ts` - Sort, Filter, Debounced Search, Analyst-Mapping
- `features/reports/reports.component.html` - Filter-Leiste, erweiterte Tabelle
- `features/reports/reports.component.css` - 57 Zeilen, kompakt und responsive

### Features
- **Sortierung:** Datum, Kursziel, Upside (Unicode ▲/▼ Pfeile)
- **Filter:** Typ-Dropdown, Rating-Dropdown, Debounced Suchfeld (300ms via RxJS Subject)
- **Analyst-Spalte (NEU):** Name via AnalystService geladen, Fallback auf ID
- **Rating-Change:** Vorheriges Rating unter aktuellem Rating angezeigt
- **Typ-Badges:** Kleine Badges mit Border
- **Leerer Zustand:** "Keine Reports gefunden" Meldung
- **Filter-Status:** "X Filter aktiv" + "Filter zurücksetzen" Button

## Kompatibilitäts-Check
- [x] Dashboard + Reports haben keine gemeinsamen Dateien (kein Merge-Konflikt)
- [x] angular.json Budget angepasst (6kB/10kB für anyComponentStyle)
- [x] Kombinierter Build fehlerfrei (276 KB Initial, 0 Errors)
- [x] Login-Flow weiterhin funktionsfähig
- [x] Alle anderen Seiten unverändert

## Test-Anleitung
1. Login mit analyst/analyst
2. Dashboard: 4 KPI-Cards, Top Analysten, Coverage-Übersicht prüfen
3. Reports: Sortierung via Spaltenheader testen
4. Reports: Filter Typ → "QUARTERLY", Rating → "BUY" testen
5. Reports: Suche "Nestlé" testen (mit Debounce)
6. Reports: "Filter zurücksetzen" klicken

## Rollback-Plan
```bash
git revert HEAD
```
