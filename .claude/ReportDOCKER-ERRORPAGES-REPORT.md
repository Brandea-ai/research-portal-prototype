# Report: Docker Compose Setup + Error Pages (404/403/500)

**Datum:** 23.02.2026
**Methode:** 2 parallele Sonnet 4.6 Agents in isolierten Git-Worktrees

---

## Docker Compose Setup

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `docker-compose.yml` | Produktions-Setup: backend (8080) + frontend (4200), Health Checks, research-net |
| `docker-compose.dev.yml` | Dev-Override: Debug-Port 5005, Volume-Mounts, Maven-Cache |
| `.env.example` | Dokumentierte Umgebungsvariablen (DE-Kommentare) |
| `frontend/nginx.conf` | Reverse Proxy /api/ → backend, SPA Fallback, Gzip, Security Headers, Cache |
| `.dockerignore` | Root-Level: .git, node_modules, dist, target, IDE |
| `backend/.dockerignore` | target, IDE-Dateien |
| `frontend/.dockerignore` | node_modules, dist, .angular, Cypress |

### Features
- Health Check: `wget --spider http://localhost:8080/api/actuator/health`
- `depends_on` mit `condition: service_healthy`
- Restart-Policy: `unless-stopped`
- Nginx Security Headers (X-Frame-Options, X-Content-Type-Options, Referrer-Policy)
- Cache-Control: 1 Jahr für gehashte Assets, no-cache für index.html
- Dev-Override: Java Remote Debug (Port 5005), persistenter Maven-Cache

---

## Error Pages (404/403/500)

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `frontend/src/app/features/error-pages/not-found/*.ts/html/css` | 404 Seite, --color-accent, "Zum Dashboard" Button |
| `frontend/src/app/features/error-pages/forbidden/*.ts/html/css` | 403 Seite, --color-warning, "Zurück" Button |
| `frontend/src/app/features/error-pages/server-error/*.ts/html/css` | 500 Seite, --color-negative, "Erneut versuchen" + "Zum Dashboard" |

### Modifizierte Dateien

| Datei | Änderung |
|-------|----------|
| `frontend/src/app/app.routes.ts` | 3 neue Routen: `/forbidden`, `/error`, `**` (Wildcard) |

### Design
- Farbcodierung: 404 = Accent (blau), 403 = Warning (gelb), 500 = Negative (rot)
- Grosse Zahlen in JetBrains Mono (6rem)
- slideUp Animation, responsive
- Standalone Components, OnPush, Lazy-loaded

---

## Kompatibilitäts-Check

- [x] `ng build` fehlerfrei
- [x] `mvn test` 184 Tests, 0 Failures
- [x] `docker compose config` validiert
- [x] Kein File-Overlap zwischen den Agents
- [x] Bestehende Routes nicht geändert (nur am Ende hinzugefügt)
- [x] Bestehende Dockerfiles nicht modifiziert

## Test-Anleitung

1. Ungültige URL aufrufen (z.B. `/xyz`) → 404 Page
2. `/forbidden` aufrufen → 403 Page
3. `/error` aufrufen → 500 Page
4. `docker compose up --build` → Backend + Frontend starten
5. `docker compose -f docker-compose.yml -f docker-compose.dev.yml up` → Dev-Modus

## Rollback-Plan

- Docker: `docker-compose.yml`, `docker-compose.dev.yml`, `.env.example`, `nginx.conf`, `.dockerignore` Dateien löschen
- Error Pages: `error-pages/` Ordner löschen, 3 Routen aus app.routes.ts entfernen
