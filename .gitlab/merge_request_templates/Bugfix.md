## Zusammenfassung

<!-- Kurze Beschreibung: Welcher Fehler wurde behoben? -->

## Fehlerbeschreibung

### Ursprüngliches Verhalten
<!-- Was ist passiert? Wie konnte der Fehler reproduziert werden? -->

### Erwartetes Verhalten
<!-- Was hätte stattdessen passieren sollen? -->

### Ursache
<!-- Was war die Wurzelursache des Fehlers? -->

## Lösung

<!-- Wie wurde der Fehler behoben? Warum wurde dieser Ansatz gewählt? -->

## Verknüpfte Issues und Tasks

<!-- Format: Closes #<issue-nr> oder Refs P2-<task-nr> -->
Closes #
Refs:

## Art des Bugfixes

- [ ] Normaler Bugfix (auf `develop` Branch)
- [ ] Hotfix (kritischer Fehler in Produktion, auf `main` basierend)

## Reproduktionsschritte (vor dem Fix)

<!-- Schrittweise Anleitung um den Fehler zu reproduzieren -->
1.
2.
3.

## Checkliste vor dem Review

### Qualität
- [ ] Selbst-Review des Diffs durchgeführt
- [ ] Fix ist minimal und zielgerichtet (keine unnötigen Änderungen)
- [ ] Keine Regressions-Risiken durch den Fix identifiziert

### Tests
- [ ] Regression-Test ergänzt (Test schlägt vor Fix fehl, besteht danach)
- [ ] Alle bestehenden Tests lokal erfolgreich: `ng test` / `mvn test`
- [ ] Fehler wurde manuell verifiziert (vor und nach dem Fix)

### Code Style
- [ ] Frontend-Linting erfolgreich: `ng lint`
- [ ] Backend-Linting erfolgreich: `mvn checkstyle:check`
- [ ] Commit-Messages folgen Conventional Commits (Typ: `fix`)

### Branch
- [ ] Normaler Bugfix: Branch basiert auf aktuellem `develop`
- [ ] Hotfix: Branch basiert auf `main`, wird in `main` UND `develop` gemergt
- [ ] Branch-Name folgt Konvention: `hotfix/<beschreibung>` oder auf `develop` direkt

## Risikobewertung

- [ ] Niedriges Risiko (isolierter Fix, klare Ursache)
- [ ] Mittleres Risiko (mehrere Komponenten betroffen)
- [ ] Hohes Risiko (Kernfunktionalität betroffen, breites Testen erforderlich)
