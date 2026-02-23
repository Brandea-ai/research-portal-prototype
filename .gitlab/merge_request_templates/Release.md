## Release-Informationen

| Feld           | Wert                       |
|----------------|----------------------------|
| **Version**    | <!-- z.B. 1.2.0 -->        |
| **Datum**      | <!-- z.B. 2026-03-01 -->   |
| **Release-Typ**| <!-- Major / Minor / Patch -->|
| **Verantwortlich** | <!-- Name -->          |

## Zusammenfassung der Änderungen

<!-- Überblick über alle Änderungen seit dem letzten Release -->

## Enthaltene Features

<!-- Liste aller Features aus dem develop-Branch seit dem letzten Release -->
- feat: ...
- feat: ...

## Behobene Fehler

<!-- Liste aller Bugfixes seit dem letzten Release -->
- fix: ...

## Breaking Changes

<!-- Liste aller inkompatiblen Änderungen. Falls keine: "Keine" eintragen -->
Keine

## Migrationshinweise

<!-- Falls Breaking Changes: Schritte für die Migration beschreiben -->
<!-- Falls keine Breaking Changes: Abschnitt löschen -->

## Verknüpfte Issues und Tasks

Refs:

## Merge-Ziele

Dieser Release-MR wird gemergt in:
- [ ] `main` (mit Version-Tag `v<version>`)
- [ ] `develop` (um den Release-Commit zurückzuspielen)

## Release-Checkliste

### Vorbereitung
- [ ] Versionsnummer in `package.json` (Frontend) aktualisiert
- [ ] Versionsnummer in `pom.xml` (Backend) aktualisiert
- [ ] CHANGELOG.md aktualisiert
- [ ] Release-Notes erstellt
- [ ] Alle geplanten Features und Fixes sind in `develop` enthalten

### Qualitätssicherung
- [ ] Vollständiger Regressions-Test durchgeführt
- [ ] Performance-Tests bestanden
- [ ] Sicherheits-Scan (SonarQube) ohne kritische Findings
- [ ] UAT (User Acceptance Testing) abgeschlossen und abgenommen

### Technisch
- [ ] Frontend-Linting erfolgreich: `ng lint`
- [ ] Backend-Linting erfolgreich: `mvn checkstyle:check`
- [ ] Alle Tests erfolgreich: `ng test` / `mvn test`
- [ ] CI/CD-Pipeline grün
- [ ] Docker-Images gebaut und getestet

### Dokumentation
- [ ] API-Dokumentation aktualisiert
- [ ] Deployment-Dokumentation aktualisiert (falls nötig)
- [ ] Benutzerhandbuch aktualisiert (falls nötig)

### Deployment-Plan
- [ ] Deployment-Zeitfenster kommuniziert
- [ ] Rollback-Plan vorhanden
- [ ] Datenbankmigrationen getestet (falls vorhanden)
- [ ] Konfigurationsänderungen dokumentiert

## Genehmigungen

Vor dem Merge in `main` sind folgende Genehmigungen erforderlich:

- [ ] Tech Lead Approval
- [ ] QA Sign-off
- [ ] (Bei Bedarf) Fachbereich Sign-off
