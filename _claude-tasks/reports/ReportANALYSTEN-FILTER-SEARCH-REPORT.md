# Report: Analysten Filter/Suche + Backend Global Search

**Datum:** 23.02.2026
**Methode:** 2 parallele Sonnet 4.6 Agents in isolierten Git-Worktrees

---

## Analysten Filter/Suche/Sortierung (Frontend)

### Modifizierte Dateien

| Datei | Änderung |
|-------|----------|
| `frontend/src/app/features/analysts/analysts.component.ts` | Signals: searchTerm, filterDepartment, sortOption + computed filteredAnalysts |
| `frontend/src/app/features/analysts/analysts.component.html` | Filter-Leiste mit Suche, Department-Filter, Sortierung, Empty State |
| `frontend/src/app/features/analysts/analysts.component.css` | Filter-Bar CSS (gleicher Stil wie Reports), Responsive |

### Features
- Textsuche über Name, Title, Department, Email, Coverage-Tickers
- Department-Filter (dynamisch aus Daten extrahiert)
- 6 Sortieroptionen: Name A-Z/Z-A, Accuracy hoch/niedrig, Rating, Coverage
- Zähler: "X von Y Analysten"
- Filter-Zurücksetzen Button
- Empty State bei 0 Ergebnissen

---

## Backend Global Search Endpoint

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `backend/.../dto/SearchResultDto.java` | DTO: type, id, title, subtitle, highlight, relevance |
| `backend/.../dto/SearchResponseDto.java` | DTO: query, totalResults, results, resultsByType |
| `backend/.../service/SearchService.java` | Globale Suche über Reports/Analysten/Securities, Relevanz-Scoring |
| `backend/.../controller/SearchController.java` | GET /api/search?q=...&type=...&limit=... |
| `backend/.../service/SearchServiceTest.java` | 21 Unit-Tests |
| `backend/.../controller/SearchControllerTest.java` | 8 MockMvc-Tests |

### Endpoint
- `GET /api/search?q=Nestlé` — Sucht über alle Entitäten
- `GET /api/search?q=test&type=REPORT` — Nur Reports
- `GET /api/search?q=NESN&limit=5` — Limitiert auf 5 Ergebnisse

### Relevanz-Score
- Exakter Match: 1.0
- Titel/Name-Match: 0.8
- Sekundärfelder: 0.5
- Sortiert nach Relevanz absteigend

---

## Kompatibilitäts-Check

- [x] `ng build` fehlerfrei
- [x] `mvn test` 213 Tests, 0 Failures (+29 neue)
- [x] Kein File-Overlap zwischen den Agents
- [x] CORS: `/api/**` Pattern deckt `/api/search` ab
- [x] Bestehende Funktionalität nicht gebrochen

## Test-Anleitung

1. Analysten-View: Suchfeld nutzen, Department filtern, Sortierung ändern
2. `curl "http://localhost:8080/api/search?q=Nestlé"` → Suchergebnisse
3. `curl "http://localhost:8080/api/search?q=Healthcare&type=SECURITY"` → Nur Securities
4. `curl "http://localhost:8080/api/search?q=BUY&limit=3"` → Max 3 Ergebnisse

## Rollback-Plan

- Analysten Filter: analysts.component.* auf vorherigen Stand zurücksetzen
- Search: SearchController, SearchService, DTOs, Tests löschen
