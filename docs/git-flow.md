# Git-Flow Strategie — Research Portal Prototype

Dieses Dokument beschreibt die verbindliche Git-Flow Branching-Strategie für das Research-Portal-Projekt.

---

## Inhaltsverzeichnis

1. [Überblick](#überblick)
2. [Branch-Übersicht](#branch-übersicht)
3. [Visualisierung des Git-Flow](#visualisierung-des-git-flow)
4. [Detaillierte Branch-Beschreibungen](#detaillierte-branch-beschreibungen)
5. [Workflow: Feature entwickeln](#workflow-feature-entwickeln)
6. [Workflow: Release-Prozess](#workflow-release-prozess)
7. [Workflow: Hotfix-Prozess](#workflow-hotfix-prozess)
8. [Commit-Message Konventionen](#commit-message-konventionen)
9. [Einrichtung der Hooks](#einrichtung-der-hooks)

---

## Überblick

Das Projekt verwendet eine angepasste Version des **Git-Flow-Modells** von Vincent Driessen. Das Modell ist auf die Anforderungen eines Banking-Umfelds ausgerichtet:

- Klare Trennung zwischen stabiler Produktion und aktiver Entwicklung
- Nachvollziehbare, auditierfähige History (wichtig für FINMA-Compliance)
- Strukturierter Release-Prozess mit definierten Qualitäts-Gates
- Schnelle Reaktionsfähigkeit bei kritischen Produktionsfehlern (Hotfix)

---

## Branch-Übersicht

| Branch       | Lebensdauer | Geschützt | Basis      | Merge-Ziel(e)       |
|--------------|-------------|-----------|------------|---------------------|
| `main`       | Permanent   | Ja        |            | (Quelle für Hotfix) |
| `develop`    | Permanent   | Ja        |            | (Quelle für Feature)|
| `feature/*`  | Temporär    | Nein      | `develop`  | `develop`           |
| `release/*`  | Temporär    | Nein      | `develop`  | `main` + `develop`  |
| `hotfix/*`   | Temporär    | Nein      | `main`     | `main` + `develop`  |

---

## Visualisierung des Git-Flow

```
Zeit ──────────────────────────────────────────────────────────────────────►

main      ──●─────────────────────────────────────────●──────────●──────────
            │ v0.1.0                                  │ v1.0.0   │ v1.0.1
            │                                         │          │
hotfix      │                                         │          ├──● Fix
            │                                         │          └──┤
            │                                         │          ┌──┘
            │                                         │          │
develop   ──┼──●────────────●──────────●──────────────●──────────●──────────
            │  │            │ Merge    │ Merge         │          │
            │  │            │ feature  │ feature       │          │
            │  │            │ /auth    │ /search       │ Merge    │
feature     │  ├──●──●──●──┤          │               │ release/ │
/auth       │  │           │          │               │ 1.0.0    │
            │  │           │          │               │          │
feature     │  │           ├──●──●──●─┤               │          │
/search     │  │                                      │          │
            │  │                                      │          │
release     │  │                              ●──●──●─┤          │
/1.0.0      │  │                              │       │          │
            │  │                              │       │          │
            ▼  ▼                              ▼       ▼          ▼


Legende:
  ● = Commit
  ─ = Branch-Verlauf
  ┤ = Merge-Punkt (eingehend)
  ├ = Fork-Punkt (ausgehend)
```

### Vereinfachte Darstellung

```
main:       ●────────────────────────────────────────────────●──────●
             \                                              /      /
              \                                   release/1.0.0   /
               \                                 ●──●──●─────────/
                \                               /
develop:  ────────●────────────●──────────────●─────────────────────●
                   \          / \            /                       \
                    \        /   \          /                         \
feature/auth:        ●──●──●     feature/search:                   hotfix:
                                  ●──●──●                            ●──●
```

---

## Detaillierte Branch-Beschreibungen

### `main` — Produktions-Branch

**Zweck:** Enthält ausschliesslich produktionsreife, getestete und abgenommene Versionen.

**Regeln:**
- Direkter Push ist verboten (nur via Merge Request / Pull Request)
- Jeder Merge auf `main` erhält einen Versions-Tag (`v1.0.0`, `v1.0.1`)
- Die CI/CD-Pipeline deployt automatisch in die Produktionsumgebung
- Branch-Protection: Mindestens 2 Approvals erforderlich

**Tags auf `main`:**
- Semantisches Versionierungsschema: `vMAJOR.MINOR.PATCH`
- MAJOR: Inkompatible API-Änderungen (Breaking Changes)
- MINOR: Neue Features, rückwärtskompatibel
- PATCH: Bugfixes, rückwärtskompatibel

```bash
# Tag erstellen nach Release-Merge
git tag -a v1.0.0 -m "Release 1.0.0: Research Portal MVP"
git push origin v1.0.0
```

---

### `develop` — Integrations-Branch

**Zweck:** Sammelbecken aller abgeschlossenen Features für den nächsten Release. Spiegelt den aktuellen Stand der nächsten Version wider.

**Regeln:**
- Direkter Push ist verboten (nur via Merge Request / Pull Request)
- Muss jederzeit buildbar und testbar sein
- CI/CD deployt automatisch in die Test/Staging-Umgebung
- Branch-Protection: Mindestens 1 Approval erforderlich

**Wann ist `develop` "kaputt"?**
- `ng build` schlägt fehl
- `mvn clean package` schlägt fehl
- Unit-Tests schlagen fehl

Bei einem roten `develop` hat das Beheben des Builds höchste Priorität für das gesamte Team.

---

### `feature/*` — Feature-Branches

**Zweck:** Isolierte Entwicklung eines einzelnen Features oder User Stories.

**Namenskonvention:**
```
feature/<kurzbeschreibung>
feature/<task-id>-<kurzbeschreibung>   (empfohlen mit Task-ID)
```

**Beispiele:**
```
feature/audit-trail
feature/P2-27-git-flow-strategie
feature/search-filter-panel
feature/P2-15-oauth2-integration
```

**Lebensdauer:** Vom Start der Feature-Entwicklung bis zum Merge in `develop`. Danach löschen.

**Regeln:**
- Basiert immer auf aktuellem `develop`
- Enthält genau ein Feature (Single Responsibility)
- Muss lokal vollständig getestet sein vor dem PR
- Merge-Strategie: Squash Merge für saubere `develop`-History

---

### `release/*` — Release-Branches

**Zweck:** Vorbereitung eines neuen Releases. Letzte Stabilisierungsphase: nur Bugfixes, keine neuen Features.

**Namenskonvention:**
```
release/<version>
```

**Beispiele:**
```
release/1.0.0
release/1.1.0
release/2.0.0-beta
```

**Erlaubte Commits auf einem Release-Branch:**
- Bugfixes, die während der QA gefunden wurden
- Versionsnummer-Updates in `package.json` und `pom.xml`
- Anpassungen an CHANGELOG und Release-Notes
- Keine neuen Features

**Lebensdauer:** Von der Erstellung bis zum Merge in `main` und `develop`. Danach löschen.

---

### `hotfix/*` — Hotfix-Branches

**Zweck:** Dringende Fehlerbehebung in der Produktionsumgebung. Umgeht den normalen Feature-Entwicklungszyklus.

**Namenskonvention:**
```
hotfix/<kurzbeschreibung>
```

**Beispiele:**
```
hotfix/login-fix
hotfix/null-pointer-report-export
hotfix/security-patch-jwt
```

**Regeln:**
- Basiert immer auf `main` (nicht auf `develop`)
- Wird in `main` UND `develop` gemergt
- Erhält einen neuen Patch-Version-Tag auf `main`
- Nur der minimale Fix, keine zusätzlichen Änderungen

---

## Workflow: Feature entwickeln

### Schritt 1: Feature-Branch erstellen

```bash
# Sicherstellen dass develop aktuell ist
git checkout develop
git pull origin develop

# Feature-Branch erstellen
git checkout -b feature/P2-42-report-export
```

### Schritt 2: Feature entwickeln

```bash
# Regelmässig committen (Conventional Commits)
git add backend/src/main/java/ch/researchportal/report/
git commit -m "feat(backend): Export-Service Grundstruktur erstellt"

git add frontend/src/app/reports/
git commit -m "feat(frontend): Export-Button in Report-Detail-View ergänzt"

git add backend/src/test/
git commit -m "test(backend): Unit-Tests für Export-Service ergänzt"
```

### Schritt 3: Branch aktuell halten

```bash
# Falls develop sich verändert hat während der Feature-Entwicklung
git fetch origin
git rebase origin/develop

# Bei Konflikten: Konflikte beheben, dann
git add .
git rebase --continue
```

### Schritt 4: Lokal testen

```bash
# Frontend
cd frontend && ng test && ng lint && ng build

# Backend
cd backend && mvn clean test && mvn checkstyle:check
```

### Schritt 5: Pull Request / Merge Request erstellen

```bash
# Branch pushen
git push origin feature/P2-42-report-export

# PR/MR über GitHub/GitLab-UI erstellen
# Basis: develop
# Template: Feature.md auswählen
```

### Schritt 6: Review und Merge

- Mindestens 1 Approval abwarten
- CI/CD-Pipeline muss grün sein
- Review-Kommentare adressieren
- Nach Approval: Squash-Merge in `develop`

```bash
# Nach dem Merge: Feature-Branch löschen
git branch -d feature/P2-42-report-export
git push origin --delete feature/P2-42-report-export
```

---

## Workflow: Release-Prozess

### Schritt 1: Release-Branch erstellen

```bash
# Wenn develop für das Release bereit ist
git checkout develop
git pull origin develop

# Release-Branch erstellen
git checkout -b release/1.0.0
```

### Schritt 2: Versionsnummern aktualisieren

```bash
# Frontend: package.json
# version: "0.9.0" → "1.0.0"

# Backend: pom.xml
# <version>0.9.0-SNAPSHOT</version> → <version>1.0.0</version>

git add frontend/package.json backend/pom.xml
git commit -m "chore: Version auf 1.0.0 gesetzt"
```

### Schritt 3: CHANGELOG aktualisieren

```bash
git add CHANGELOG.md
git commit -m "docs: CHANGELOG für Release 1.0.0 ergänzt"
```

### Schritt 4: QA und Bugfixes

```bash
# Während QA gefundene Fehler direkt auf release/* beheben
git commit -m "fix(search): Sortierung bei leerer Ergebnisliste behoben"
```

### Schritt 5: Release in `main` mergen

```bash
# PR/MR auf main erstellen
# Template: Release.md verwenden
# 2 Approvals + QA Sign-off erforderlich

# Nach Merge: Tag auf main setzen
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release 1.0.0: Research Portal MVP

Features:
- Wertschriften-Suche mit Filterung
- Report-Verwaltung und -Export
- Analysten-Dashboard
- Audit-Trail"

git push origin v1.0.0
```

### Schritt 6: Release-Branch in `develop` zurückmergen

```bash
# PR/MR von release/1.0.0 auf develop erstellen
# Bugfixes aus dem Release-Branch in develop übernehmen
```

### Schritt 7: Release-Branch löschen

```bash
git branch -d release/1.0.0
git push origin --delete release/1.0.0
```

### Visueller Release-Ablauf

```
develop ──●──●──●──────────────────────────────────────●──
           \                                           /
            release/1.0.0                             /
             ●── chore: version ──●── fix ──●────────/──●──
                                                    /
main ──────────────────────────────────────────────●──
                                                   v1.0.0
```

---

## Workflow: Hotfix-Prozess

### Schritt 1: Hotfix-Branch von `main` erstellen

```bash
# WICHTIG: Von main, nicht von develop
git checkout main
git pull origin main

git checkout -b hotfix/login-fix
```

### Schritt 2: Fehler beheben und testen

```bash
# Minimaler, gezielter Fix
git add backend/src/main/java/ch/researchportal/auth/
git commit -m "fix(auth): JWT-Validierung bei abgelaufenen Tokens korrigiert

Das Token wurde fälschlicherweise als gültig akzeptiert wenn die
Ablaufzeit auf UTC-Mitternacht gesetzt war.

Refs: #INC-2847"

# Tests
cd backend && mvn clean test
```

### Schritt 3: Hotfix in `main` mergen

```bash
# PR/MR auf main erstellen
# Template: Bugfix.md verwenden
# Expedited Review (max. 2 Stunden)

# Nach Merge: Patch-Tag setzen
git checkout main
git pull origin main
git tag -a v1.0.1 -m "Hotfix 1.0.1: JWT-Validierung behoben (INC-2847)"
git push origin v1.0.1
```

### Schritt 4: Hotfix in `develop` mergen

```bash
# PR/MR von hotfix/* auf develop erstellen
# Fix muss auch in develop ankommen
```

### Schritt 5: Hotfix-Branch löschen

```bash
git branch -d hotfix/login-fix
git push origin --delete hotfix/login-fix
```

### Visueller Hotfix-Ablauf

```
main    ──────────────●────────────────────●──
          v1.0.0      │                    │ v1.0.1
                      │ hotfix/login-fix   │
                      ├──●──●──────────────┤
                      │                   │
develop ──────────────●───────────────────●──
```

---

## Commit-Message Konventionen

Das Projekt verwendet **Conventional Commits**. Die vollständige Beschreibung findet sich in der [CONTRIBUTING.md](../CONTRIBUTING.md).

### Kurzreferenz

```
feat(scope): Kurze Beschreibung im Imperativ

Optionaler Body: Detailliertere Erklärung der Änderung.
Maximal 72 Zeichen pro Zeile.

Optionaler Footer:
BREAKING CHANGE: Beschreibung der inkompatiblen Änderung
Closes #123
Refs P2-42
```

### Gültige Typen

| Typ        | Verwendung                              |
|------------|-----------------------------------------|
| `feat`     | Neues Feature                           |
| `fix`      | Bugfix                                  |
| `refactor` | Refactoring ohne Funktionsänderung      |
| `test`     | Tests ergänzt oder angepasst            |
| `docs`     | Dokumentation geändert                  |
| `chore`    | Build, Abhängigkeiten, Tooling          |
| `ci`       | CI/CD-Pipeline                          |
| `perf`     | Performance-Verbesserung                |

---

## Einrichtung der Hooks

Die Git-Hooks im Verzeichnis `.githooks/` werden nicht automatisch von Git erkannt. Sie müssen einmalig aktiviert werden:

```bash
# Im Root-Verzeichnis des Repositories ausführen
git config core.hooksPath .githooks

# Prüfen ob die Konfiguration gesetzt ist
git config --get core.hooksPath
# Erwartete Ausgabe: .githooks
```

### Verfügbare Hooks

| Hook          | Datei                     | Prüfung                                      |
|---------------|---------------------------|----------------------------------------------|
| `pre-commit`  | `.githooks/pre-commit`    | `ng lint`, `mvn checkstyle:check`, Secrets   |
| `commit-msg`  | `.githooks/commit-msg`    | Conventional Commits Format                  |

### Hook temporär deaktivieren

In Ausnahmefällen (z.B. automatisierte Commits) können Hooks umgangen werden:

```bash
# pre-commit Hook überspringen
git commit --no-verify -m "chore: Automatischer Commit"
```

Dies ist nur in begründeten Ausnahmefällen erlaubt und sollte im Team kommuniziert werden.

---

*Letzte Aktualisierung: 2026-02-23*
*Basiert auf: Git-Flow von Vincent Driessen (nvie.com/posts/a-successful-git-branching-model)*
