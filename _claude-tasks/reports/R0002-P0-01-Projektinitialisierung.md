# R0002: P0-01 Projektinitialisierung

**Datum:** 22.02.2026
**Task:** P0-01
**Status:** Abgeschlossen
**Dauer:** ~30 Min

---

## Was wurde geändert

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `backend/pom.xml` | Spring Boot 3.5.0, Java 17, Dependencies: Web, JPA, H2, Validation, Lombok, DevTools, Flyway |
| `backend/src/.../ResearchPortalApplication.java` | Spring Boot Main Class |
| `backend/src/.../config/WebConfig.java` | CORS-Konfiguration für Angular (localhost:4200) |
| `backend/src/.../resources/application.yml` | 3 Profile: local (H2 mem), demo (H2 file), production (Oracle) |
| `backend/Dockerfile` | Multi-Stage Build: JDK 17 build + JRE 17 runtime |
| `frontend/` (komplett) | Angular 21 Projekt, Standalone Components, strict TypeScript |
| `frontend/Dockerfile` | Multi-Stage Build: Node 22 build + nginx |
| `frontend/nginx.conf` | SPA-Routing + API-Proxy zu Backend |
| `docker-compose.yml` | Frontend (nginx:80) + Backend (java:8080) |

### Hexagonale Package-Struktur

```
com.research.portal/
├── domain/
│   ├── model/           # Entities ohne Framework-Abhängigkeiten
│   └── port/
│       ├── in/          # Use-Case Interfaces
│       └── out/         # Repository Interfaces
├── application/
│   └── service/         # Use-Case Implementierungen
├── adapter/
│   ├── in/rest/
│   │   ├── dto/         # Request/Response DTOs
│   │   └── mapper/      # Domain ↔ DTO Mapping
│   └── out/persistence/
│       ├── entity/      # JPA Entities
│       ├── repository/  # Spring Data Repos
│       ├── mapper/      # Domain ↔ Entity Mapping
│       └── adapter/     # Port-Out Implementierungen
└── config/              # Spring Config, CORS, Security
```

## Kompatibilitäts-Check

- [x] `mvn clean package` → BUILD SUCCESS
- [x] `ng build` → Application bundle generation complete
- [x] application.yml: 3 Profile korrekt separiert
- [x] CORS: Nur localhost:4200, nur /api/** Pfade
- [x] .gitignore deckt backend/target/ und frontend/node_modules/ ab
- [x] Docker Compose: Services referenzieren korrekt aufeinander

## Test-Anleitung

```bash
# Backend starten
cd backend
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
mvn spring-boot:run
# → http://localhost:8080/h2-console

# Frontend starten (separates Terminal)
cd frontend
npx ng serve
# → http://localhost:4200

# Docker (optional)
docker compose up --build
# → Frontend: http://localhost:4200, Backend: http://localhost:8080
```

## Rollback-Plan

```bash
git revert HEAD  # Letzten Commit rückgängig machen
```

## Entscheidungen

| Entscheidung | Begründung |
|-------------|-----------|
| Spring Boot 3.5.0 statt 3.4.3 | Einzig verfügbare Version auf Spring Initializr |
| Angular 21 statt 17 | Aktuelle CLI-Version, abwärtskompatibel |
| Flyway im local-Profil deaktiviert | create-drop für Prototyp ausreichend |
| Node 22 in Docker statt 25 | LTS-Version, produktionssicherer |

## Nächster Schritt

P0-02: Design-System CSS (Farbschema, Typografie, Layout-Grid, Custom Properties)
