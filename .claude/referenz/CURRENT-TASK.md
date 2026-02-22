# Aktueller Arbeitsstand

> Wird nach jeder Arbeitseinheit aktualisiert. Referenz: `_claude-tasks/STATUS.md`

**Letzte Aktualisierung:** 22.02.2026
**Aktueller Task:** P0-01 Projektinitialisierung
**Status:** Abgeschlossen

---

## Was wurde zuletzt gemacht

### Session 2 (22.02.2026): P0-01 Projektinitialisierung

1. Java 17 via Homebrew installiert und JAVA_HOME konfiguriert
2. Maven 3.9.12 installiert
3. Angular CLI 21.1.4 installiert (Node.js 25)
4. GitHub Repo erstellt: https://github.com/Brandea-ai/research-portal-prototype
5. Spring Boot 3.5.0 Projekt via Spring Initializr generiert
6. Hexagonale Package-Struktur angelegt (domain/application/adapter/config)
7. application.yml mit 3 Profilen (local, demo, production)
8. CORS-Konfiguration für Angular Dev-Server
9. Angular 21 Projekt generiert (Standalone, strict TypeScript)
10. PrimeNG installiert (nur Struktur, kein Theme)
11. Docker Compose + Dockerfiles erstellt
12. Beide Builds verifiziert: `mvn clean package` + `ng build`
13. Commit + Push auf GitHub

## Was steht als Nächstes an

1. **P0-02:** Design-System CSS (Farbschema, Typografie, Layout-Grid)
2. **P0-03:** Backend Domain-Modell (Analyst, Security, ResearchReport, etc.)
3. **P0-04:** Backend Persistence Layer (JPA Entities, Repositories)
4. **P0-05:** Backend REST API (Controller, DTOs, Mapper)

## Offene Fragen / Blocker

Keine aktuell.

## Kontext für die nächste Session

P0-01 ist komplett. Angular 21 + Spring Boot 3.5.0 bauen fehlerfrei.
JAVA_HOME muss bei jedem Bash-Befehl gesetzt werden:
`export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"`
Node.js 25 Warnung ist kosmetisch (Prototype-Kontext akzeptabel).
Nächster Schritt: P0-02 Design-System CSS.
