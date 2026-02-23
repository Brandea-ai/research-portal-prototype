# Report: Breadcrumb Navigation + Watchlist API

**Datum:** 23.02.2026
**Methode:** 2 parallele Sonnet 4.6 Agents in isolierten Git-Worktrees

---

## Breadcrumb Navigation (Frontend)

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `frontend/src/app/shared/components/breadcrumb/breadcrumb.component.ts` | BreadcrumbItem Interface + Component, @Input items, OnPush |
| `frontend/src/app/shared/components/breadcrumb/breadcrumb.component.html` | Nav mit @for-Loop, RouterLink, U+203A Separator |
| `frontend/src/app/shared/components/breadcrumb/breadcrumb.component.css` | fadeIn Animation, responsive (< 480px: nur letzte 2 Items) |

### Modifizierte Dateien

| Datei | Änderung |
|-------|----------|
| `frontend/.../report-detail/report-detail.component.ts` | BreadcrumbComponent Import, RouterLink entfernt |
| `frontend/.../report-detail/report-detail.component.html` | back-link → Breadcrumb (Dashboard > Reports > [Titel]) |
| `frontend/.../security-detail/security-detail.component.ts` | BreadcrumbComponent Import, RouterLink entfernt |
| `frontend/.../security-detail/security-detail.component.html` | back-link → Breadcrumb (Dashboard > Wertschriften > [TICKER — Name]) |
| `frontend/.../analyst-detail/analyst-detail.component.ts` | BreadcrumbComponent Import, RouterLink entfernt |
| `frontend/.../analyst-detail/analyst-detail.component.html` | back-link → Breadcrumb (Dashboard > Analysten > [Name]) |

### Features
- Klickbare Pfad-Navigation in allen 3 Detail-Ansichten
- Unicode "›" Separator, kein Icon-Set nötig
- Responsive: Mobile zeigt nur letzte 2 Items mit "..." Ellipsis
- Accessibility: `<nav aria-label="Breadcrumb">`
- Fehler-State zeigt verkürzte Breadcrumbs (ohne Entity-Name)

---

## Watchlist/Favorites API (Backend)

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `backend/.../entity/WatchlistEntity.java` | JPA Entity, @PrePersist für addedAt, FK auf Security |
| `backend/.../repository/JpaWatchlistRepository.java` | 5 Query-Methoden (findByUserId, existsBy..., deleteBy...) |
| `backend/.../service/WatchlistService.java` | @Service, @Transactional, 6 Methoden, Duplikat/Security-Prüfung |
| `backend/.../controller/WatchlistController.java` | 6 REST-Endpoints mit X-User-Id Header, Swagger-Annotationen |
| `backend/.../dto/WatchlistEntryDto.java` | DTO mit Security-Stammdaten (Ticker, Name) angereichert |
| `backend/.../dto/AddToWatchlistRequest.java` | @NotNull securityId, @Size(max=500) notes, Bean Validation |
| `backend/.../db/migration/V5__create_watchlist_table.sql` | CREATE TABLE mit FK, Unique-Constraint |
| `backend/.../service/WatchlistServiceTest.java` | 13 Unit-Tests |
| `backend/.../controller/WatchlistControllerTest.java` | 11 MockMvc-Tests |

### Modifizierte Dateien

| Datei | Änderung |
|-------|----------|
| `backend/.../resources/data.sql` | 3 Demo-Watchlist-Einträge (NESN, ROG, UBSG) |
| `backend/.../GlobalExceptionHandler.java` | IllegalStateException → 409 Conflict Handler |

### Endpoints
- `GET /api/watchlist` — Alle Watchlist-Einträge des Users
- `POST /api/watchlist` — Wertschrift hinzufügen (201 Created)
- `PUT /api/watchlist/{securityId}` — Notizen/Alert aktualisieren
- `DELETE /api/watchlist/{securityId}` — Entfernen (204 No Content)
- `GET /api/watchlist/{securityId}/check` — Ist auf Watchlist?
- `GET /api/watchlist/count` — Anzahl Einträge

---

## Kompatibilitäts-Check

- [x] `ng build` fehlerfrei
- [x] `mvn test` 317 Tests, 0 Failures (+24 neue)
- [x] Kein File-Overlap zwischen den Agents
- [x] Dashboard, Reports-Liste, Securities-Liste, Analysts-Liste unverändert
- [x] Detail-Ansichten: Breadcrumbs ersetzen back-links, Funktionalität erhalten
- [x] GlobalExceptionHandler: IllegalStateException-Handler ergänzt (bestehendes Verhalten unverändert)

## Test-Anleitung

1. Report/Security/Analyst-Detail öffnen → Breadcrumb-Pfad prüfen
2. Breadcrumb-Links klicken → Navigation zu Liste/Dashboard testen
3. `curl http://localhost:8080/api/watchlist` → Demo-Watchlist (3 Einträge)
4. `curl -X POST http://localhost:8080/api/watchlist -H "Content-Type: application/json" -d '{"securityId": 2}'` → 201
5. `curl http://localhost:8080/api/watchlist/1/check` → `{"onWatchlist": true}`

## Rollback-Plan

- Breadcrumbs: breadcrumb/ Ordner löschen, Detail-Templates auf back-link revertieren
- Watchlist: WatchlistEntity, Repository, Service, Controller, DTOs, Migration, Tests löschen; data.sql Watchlist-Zeilen entfernen; GlobalExceptionHandler IllegalStateException-Handler entfernen
