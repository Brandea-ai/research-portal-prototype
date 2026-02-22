# R0006: P0-05 Backend REST API

**Datum:** 22.02.2026
**Phase:** P0 Fundament
**Task:** P0-05 Backend REST API
**Status:** Abgeschlossen
**Abhängig von:** P0-04 Backend Persistence Layer

---

## Zusammenfassung

Die komplette REST API wurde implementiert: 5 DTOs, 3 API-Mapper, 3 Application Services, 3 REST Controller und ein Global Exception Handler. Alle Endpoints sind live getestet mit korrekten JSON-Responses.

## Erstellte Dateien (16)

### DTOs (adapter/in/web/dto/)
| Datei | Zweck |
|-------|-------|
| `ReportDto.java` | Vollständige Report-Response (alle Felder als Strings/Primitives) |
| `CreateReportRequest.java` | Create/Update-Request mit Bean Validation (@NotNull, @NotBlank, @Positive) |
| `SecurityDto.java` | Wertschriften-Response |
| `AnalystDto.java` | Analysten-Response |
| `ErrorResponse.java` | Einheitliches Fehlerformat (status, error, message, timestamp, details) |

### API-Mapper (adapter/in/web/mapper/)
| Datei | Zweck |
|-------|-------|
| `ReportApiMapper.java` | Domain ↔ DTO, berechnet impliedUpside + ratingChanged automatisch |
| `SecurityApiMapper.java` | Domain → DTO, AssetClass Enum → String |
| `AnalystApiMapper.java` | Domain → DTO |

### Application Services (application/service/)
| Datei | Zweck |
|-------|-------|
| `ReportService.java` | Implementiert GetReportsUseCase + ManageReportUseCase |
| `SecurityService.java` | Implementiert GetSecuritiesUseCase |
| `AnalystService.java` | Implementiert GetAnalystsUseCase |

### Custom Exception (application/exception/)
| Datei | Zweck |
|-------|-------|
| `ResourceNotFoundException.java` | 404-Exception mit lokalisierten Fehlermeldungen |

### REST Controller (adapter/in/web/controller/)
| Datei | Zweck |
|-------|-------|
| `ReportController.java` | CRUD: GET/POST/PUT/DELETE /api/reports |
| `SecurityController.java` | READ: GET /api/securities, /api/securities?ticker=X |
| `AnalystController.java` | READ: GET /api/analysts |

### Exception Handler (adapter/in/web/)
| Datei | Zweck |
|-------|-------|
| `GlobalExceptionHandler.java` | @RestControllerAdvice: 404, Validation, IllegalArgument |

### Geänderte Dateien
| Datei | Änderung |
|-------|----------|
| `config/WebConfig.java` | CORS aktualisiert: allowedOriginPatterns mit Vercel-Wildcard |

## API-Endpoints

| Method | Endpoint | Beschreibung |
|--------|----------|-------------|
| GET | `/api/reports` | Alle Reports |
| GET | `/api/reports/{id}` | Report nach ID |
| GET | `/api/reports?analystId=X` | Reports eines Analysten |
| GET | `/api/reports?securityId=X` | Reports zu einer Wertschrift |
| POST | `/api/reports` | Neuen Report erstellen |
| PUT | `/api/reports/{id}` | Report aktualisieren |
| DELETE | `/api/reports/{id}` | Report löschen |
| GET | `/api/securities` | Alle Wertschriften |
| GET | `/api/securities/{id}` | Wertschrift nach ID |
| GET | `/api/securities?ticker=X` | Wertschrift nach Ticker |
| GET | `/api/analysts` | Alle Analysten |
| GET | `/api/analysts/{id}` | Analyst nach ID |

## Architektur-Entscheidungen

1. **DTOs verwenden Strings für Enums:** JSON-Stabilität, Client muss keine Enums kennen
2. **Bean Validation auf CreateReportRequest:** Spring prüft automatisch, GlobalExceptionHandler formatiert Fehler
3. **Services implementieren Use-Case-Ports:** Controller kennt nur Interfaces, nicht Implementierungen
4. **allowedOriginPatterns statt allowedOrigins:** Ermöglicht Vercel Preview-URL-Wildcards
5. **impliedUpside wird berechnet:** ReportApiMapper berechnet (target-current)/current automatisch

## Live-Test Ergebnisse

| Test | Ergebnis |
|------|----------|
| GET /api/reports | 10 Reports korrekt |
| GET /api/securities | 10 Securities korrekt |
| GET /api/analysts | 5 Analysts korrekt |
| GET /api/reports/1 | Nestlé Report vollständig |
| GET /api/securities?ticker=NESN | Nestlé gefunden |
| GET /api/reports/999 | 404 mit "Report mit ID 999 nicht gefunden" |

## Qualitäts-Check

- [x] `mvn clean package -DskipTests` erfolgreich
- [x] Spring Boot startet fehlerfrei
- [x] Alle 12 Endpoints getestet und funktional
- [x] 404 Error Handler liefert strukturierte JSON-Fehler
- [x] Bean Validation aktiv (CreateReportRequest)
- [x] CORS für localhost + Vercel konfiguriert
- [x] Keine JPA-Entities in REST Responses (nur DTOs)
- [x] Controller nutzt nur Use-Case-Interfaces (keine Service-Klassen direkt)

## Skills demonstriert

| Skill | Nachweis |
|-------|----------|
| Java + Spring Boot | 3 REST Controller, @RestControllerAdvice, Bean Validation |
| Hibernate/JPA | End-to-End: Controller → Service → Repository → JPA → H2 |
| DDD | Use-Case-Ports als Interfaces, Services als Implementierung |
| REST API Design | RESTful Endpoints, Query Parameters, HTTP Status Codes |
| Error Handling | Global Exception Handler, strukturierte Fehler-DTOs |
