# Report: Global Search Bar + Request Logging Interceptor

**Datum:** 23.02.2026
**Methode:** 2 parallele Sonnet 4.6 Agents in isolierten Git-Worktrees

---

## Global Search Bar im TopBar (Frontend)

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `frontend/src/app/core/services/search.service.ts` | HTTP Service für GET /api/search, Interfaces SearchResult/SearchResponse |
| `frontend/src/app/shared/components/search-overlay/*.ts/html/css` | Debounced Search mit Dropdown, Keyboard-Nav, Click-Outside |

### Modifizierte Dateien

| Datei | Änderung |
|-------|----------|
| `frontend/src/app/layout/topbar/topbar.component.ts` | SearchOverlayComponent Import |
| `frontend/src/app/layout/topbar/topbar.component.html` | `<app-search-overlay>` eingefügt |
| `frontend/src/app/layout/topbar/topbar.component.css` | position: relative für TopBar |

### Features
- Ctrl+K / Cmd+K Shortcut zum Fokussieren
- 300ms Debounce mit RxJS (Subject + debounceTime + switchMap)
- Keyboard Navigation: Arrow Up/Down, Enter, Escape
- Type-Badges: R (Report/accent), A (Analyst/positive), W (Wertschrift/warning)
- Click-Outside schliesst Dropdown
- Mobile: Suchleiste ausgeblendet

---

## Backend Request Logging + API Metrics

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `backend/.../config/RequestLoggingInterceptor.java` | HandlerInterceptor, loggt --> / <-- Format, WARN bei >500ms |
| `backend/.../config/ApiMetricsCollector.java` | Thread-safe Metriken (AtomicLong, ConcurrentHashMap) |
| `backend/.../config/WebMvcInterceptorConfig.java` | Registriert Interceptor für /api/** (exkl. actuator) |
| `backend/.../dto/ApiMetricsDto.java` | DTO: totalRequests, totalErrors, avgResponseTimeMs, Maps |
| `backend/.../controller/MetricsController.java` | GET /api/metrics, DELETE /api/metrics |
| `backend/.../config/ApiMetricsCollectorTest.java` | 15 Tests inkl. Thread-Safety |
| `backend/.../config/RequestLoggingInterceptorTest.java` | 7 Tests |
| `backend/.../controller/MetricsControllerTest.java` | 10 Tests |

### Endpoints
- `GET /api/metrics` — Aktuelle API-Metriken (Requests, Errors, Avg Response Time)
- `DELETE /api/metrics` — Metriken zurücksetzen (204)

### Design-Entscheidung
Beans via @Bean in WebMvcInterceptorConfig statt @Component, damit @WebMvcTest-Slices die Interceptor-Infrastruktur nicht automatisch laden.

---

## Kompatibilitäts-Check

- [x] `ng build` fehlerfrei
- [x] `mvn test` 245 Tests, 0 Failures (+32 neue)
- [x] Kein File-Overlap zwischen den Agents
- [x] CORS: `/api/**` deckt `/api/metrics` und `/api/search` ab
- [x] Bestehende Controller-Tests nicht gebrochen

## Test-Anleitung

1. TopBar: Ctrl+K drücken → Suchfeld fokussiert
2. "Nestlé" eingeben → Dropdown mit Ergebnissen
3. Arrow Down + Enter → Navigation zum Ergebnis
4. `curl http://localhost:8080/api/metrics` → API-Metriken
5. Mehrere API-Calls machen → Metriken steigen

## Rollback-Plan

- Search Bar: search-overlay/ löschen, search.service.ts löschen, TopBar-Änderungen revertieren
- Logging: RequestLoggingInterceptor, ApiMetricsCollector, WebMvcInterceptorConfig, MetricsController, DTO, Tests löschen
