## Zusammenfassung

<!-- Kurze Beschreibung der Änderungen: Was wurde gemacht und warum? -->

## Art der Änderung

<!-- Zutreffendes mit [x] markieren -->
- [ ] `feat` — Neues Feature
- [ ] `fix` — Bugfix / Hotfix
- [ ] `refactor` — Refactoring (kein neues Feature, kein Bugfix)
- [ ] `test` — Tests ergänzt oder angepasst
- [ ] `docs` — Dokumentation
- [ ] `chore` — Build, Abhängigkeiten, Tooling
- [ ] `ci` — CI/CD-Pipeline
- [ ] `perf` — Performance-Verbesserung

## Verknüpfte Issues

<!-- Format: Closes #<nr> schliesst das Issue automatisch nach dem Merge -->
Closes #

## Checkliste

### Pflichtfelder

- [ ] Selbst-Review des Diffs durchgeführt
- [ ] Branch ist aktuell mit dem Basis-Branch (rebase/merge durchgeführt)
- [ ] Commit-Messages folgen Conventional Commits (`type(scope): message`)
- [ ] CI/CD-Pipeline ist grün

### Tests

- [ ] Neue Tests für geänderte Logik ergänzt
- [ ] Alle Tests lokal bestanden: `ng test` (Frontend), `mvn test` (Backend)
- [ ] Keine bestehenden Tests beschädigt

### Code Qualität

- [ ] Frontend-Linting: `ng lint` fehlerfrei
- [ ] Backend-Linting: `mvn checkstyle:check` fehlerfrei
- [ ] Kein `console.log` im produktiven Code
- [ ] Kein auskommentierter Code committed

### Dokumentation

- [ ] Inline-Kommentare für komplexe Logik ergänzt (falls nötig)
- [ ] README oder Dokumentation aktualisiert (falls nötig)

## Screenshots (optional)

<!-- Bei UI-Änderungen: Vorher/Nachher Screenshots einfügen -->

## Zusätzliche Hinweise für Reviewer

<!-- Gibt es etwas, worauf Reviewer besonders achten sollen? Bekannte Einschränkungen? -->
