# Beitragsrichtlinien — Research Portal Prototype

Dieses Dokument beschreibt die Arbeitsweise im Research-Portal-Projekt. Es gilt für alle Entwicklerinnen und Entwickler, die zum Repository beitragen.

---

## Inhaltsverzeichnis

1. [Branch-Konventionen](#branch-konventionen)
2. [Commit-Message Konventionen](#commit-message-konventionen)
3. [Pull-Request-Prozess](#pull-request-prozess)
4. [Code Style Guidelines](#code-style-guidelines)
5. [Lokale Entwicklungsumgebung](#lokale-entwicklungsumgebung)

---

## Branch-Konventionen

Das Projekt folgt dem **Git-Flow-Modell**. Die vollständige Dokumentation findet sich in [`docs/git-flow.md`](docs/git-flow.md).

### Haupt-Branches

| Branch    | Zweck                                      | Schutzregel             |
|-----------|--------------------------------------------|-------------------------|
| `main`    | Produktions-Branch, immer stabil und deploybar | Kein direkter Push, nur Merge via PR/MR |
| `develop` | Integrations-Branch für das nächste Release | Kein direkter Push, nur Merge via PR/MR |

### Support-Branches

| Branch-Typ    | Muster                          | Beispiel                   | Basis-Branch | Ziel-Branch(e)      |
|---------------|---------------------------------|----------------------------|--------------|---------------------|
| `feature/*`   | `feature/<kurzbeschreibung>`    | `feature/audit-trail`      | `develop`    | `develop`           |
| `release/*`   | `release/<version>`             | `release/1.0.0`            | `develop`    | `main` + `develop`  |
| `hotfix/*`    | `hotfix/<kurzbeschreibung>`     | `hotfix/login-fix`         | `main`       | `main` + `develop`  |

### Namensregeln

- Nur Kleinbuchstaben, Ziffern und Bindestriche
- Keine Leerzeichen, Unterstriche oder Sonderzeichen
- Kurz und beschreibend (max. 50 Zeichen nach dem Präfix)
- Feature-Branches können eine Task-ID enthalten: `feature/P2-27-git-flow-strategie`

---

## Commit-Message Konventionen

Das Projekt verwendet **Conventional Commits** (https://www.conventionalcommits.org/).

### Format

```
<typ>(<scope>): <kurze Beschreibung>

[optionaler Body: ausführlichere Erklärung]

[optionale Footer: BREAKING CHANGE, Refs, Closes]
```

### Erlaubte Typen

| Typ        | Verwendung                                              |
|------------|---------------------------------------------------------|
| `feat`     | Neues Feature oder neue Funktionalität                  |
| `fix`      | Bugfix (behebt einen Fehler)                            |
| `refactor` | Code-Umstrukturierung ohne Verhaltensänderung           |
| `test`     | Hinzufügen oder Anpassen von Tests                      |
| `docs`     | Änderungen an Dokumentation                             |
| `chore`    | Build-Prozess, Abhängigkeiten, Tooling                  |
| `ci`       | CI/CD-Pipeline-Konfiguration                            |
| `perf`     | Performance-Verbesserungen                              |

### Scope (optional)

Der Scope beschreibt den betroffenen Bereich:

- `frontend` — Angular-Frontend
- `backend` — Spring-Boot-Backend
- `api` — API-Schnittstellen
- `auth` — Authentifizierung und Autorisierung
- `db` — Datenbankmigrationen
- `ci` — CI/CD-Konfiguration
- `docs` — Dokumentation

### Beispiele

```
feat(frontend): Suchergebnisse mit Highlighting versehen

fix(backend): NullPointerException bei leerem Suchbegriff behoben

refactor(api): Repository-Schicht von direkten DB-Calls entkoppelt

test(auth): Unit-Tests für JWT-Validierung ergänzt

docs: Git-Flow Strategie dokumentiert

chore: Angular auf Version 19.2 aktualisiert

feat(auth)!: OAuth2 PKCE Flow eingeführt

BREAKING CHANGE: Bestehende Session-Tokens werden ungültig.
Alle Clients müssen neu authentifiziert werden.
Refs: P2-15
```

### Regeln

- Erste Zeile (Header) maximal 72 Zeichen
- Beschreibung im Imperativ: "Add feature" nicht "Added feature"
- Kein Punkt am Ende des Headers
- Body und Footer mit Leerzeile vom Header trennen
- `BREAKING CHANGE:` im Footer kennzeichnet inkompatible Änderungen

---

## Pull-Request-Prozess

### Voraussetzungen vor dem PR erstellen

- [ ] Alle Tests lokal erfolgreich: `ng test` (Frontend), `mvn test` (Backend)
- [ ] Linting fehlerfrei: `ng lint`, `mvn checkstyle:check`
- [ ] Selbst-Review durchgeführt (diff noch einmal durchgelesen)
- [ ] Branch ist aktuell mit dem Basis-Branch (rebase oder merge)
- [ ] Commit-Messages folgen den Conventional-Commits-Konventionen

### PR-Beschreibung

Verwende die bereitgestellten Templates:

- GitHub: `.github/PULL_REQUEST_TEMPLATE.md` (automatisch geladen)
- GitLab: `.gitlab/merge_request_templates/` (manuell auswählen)

### Review-Prozess

1. **Mindestens ein Approval** erforderlich vor dem Merge
2. **CI-Pipeline muss grün sein**: Alle automatisierten Tests, Linting und SonarQube-Checks
3. **Review-Kommentare** müssen vor dem Merge adressiert sein (resolved oder mit Begründung abgelehnt)
4. Bei umfangreichen Änderungen zwei Approvals anstreben

### Merge-Strategie

| Branch-Typ  | Merge-Strategie       | Begründung                                  |
|-------------|-----------------------|---------------------------------------------|
| `feature/*` | Squash Merge          | Saubere History in `develop`                |
| `release/*` | Merge Commit          | Release-Commit bleibt sichtbar              |
| `hotfix/*`  | Merge Commit          | Hotfix-Commit bleibt in `main` und `develop`|

### Nach dem Merge

- Feature-Branch löschen (Remote und Lokal)
- Verknüpfte Issues/Tasks schliessen
- Release-Notes bei Bedarf aktualisieren

---

## Code Style Guidelines

### Allgemein

- Einrückung: 2 Leerzeichen (Frontend), 4 Leerzeichen (Backend)
- Zeilenende: LF (Unix-Style)
- Zeichensatz: UTF-8
- Maximale Zeilenlänge: 120 Zeichen

Die vollständige Konfiguration ist in der `.editorconfig`-Datei im Root-Verzeichnis definiert.

### Frontend (Angular / TypeScript)

- ESLint-Konfiguration: `.eslintrc.json` (oder `eslint.config.js`)
- Prüfung: `ng lint`
- Automatische Formatierung: Prettier (falls konfiguriert)
- Komponentennamen: PascalCase (`SearchResultComponent`)
- Services: Suffix `Service` (`SearchService`)
- Interfaces: Kein `I`-Präfix (`SearchResult`, nicht `ISearchResult`)

### Backend (Java / Spring Boot)

- Checkstyle-Konfiguration: `checkstyle.xml` im Backend-Verzeichnis
- Prüfung: `mvn checkstyle:check`
- Paketstruktur: `ch.researchportal.<modul>`
- Klassen: PascalCase
- Methoden und Variablen: camelCase
- Konstanten: UPPER_SNAKE_CASE

### Verbotenes

- Keine `console.log`-Aufrufe im produktiven Frontend-Code
- Keine auskommentierten Code-Blöcke committen
- Keine hardcodierten Credentials oder API-Keys

---

## Lokale Entwicklungsumgebung

### Voraussetzungen

- Node.js 20+, npm 10+
- Java 21, Maven 3.9+
- Docker und Docker Compose
- Git 2.40+

### Setup

```bash
# Repository klonen
git clone <repository-url>
cd research-portal-prototype

# Git-Hooks aktivieren
git config core.hooksPath .githooks

# Frontend-Abhängigkeiten installieren
cd frontend && npm install

# Backend bauen
cd ../backend && mvn clean install
```

### Git-Hooks einrichten

Die Hooks im Verzeichnis `.githooks/` müssen einmalig aktiviert werden:

```bash
git config core.hooksPath .githooks
```

Dies richtet `pre-commit` und `commit-msg` Hooks ein, die vor jedem Commit automatisch Linting und das Commit-Message-Format prüfen.

---

*Letzte Aktualisierung: 2026-02-23*
