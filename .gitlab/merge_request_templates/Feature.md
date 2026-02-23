## Zusammenfassung

<!-- Kurze Beschreibung: Was wurde implementiert und warum? -->

## Motivation und Kontext

<!-- Welches Problem löst dieses Feature? Welcher Business-Wert entsteht? -->

## Art der Änderung

- [ ] Neues Feature (nicht-brechende Änderung, die Funktionalität hinzufügt)
- [ ] Brechende Änderung (breaking change: bestehende Funktionalität ändert sich)
- [ ] Abhängigkeitsupdate
- [ ] Sonstiges: <!-- Bitte beschreiben -->

## Verknüpfte Issues und Tasks

<!-- Format: Closes #<issue-nr> oder Refs P2-<task-nr> -->
Closes #
Refs:

## Technische Details

<!-- Optional: Architekturentscheide, wichtige Implementierungsdetails, bekannte Einschränkungen -->

## Screenshots / Demo

<!-- Falls UI-Änderungen: Screenshots oder kurze Beschreibung des neuen Verhaltens -->

## Checkliste vor dem Review

### Qualität
- [ ] Selbst-Review des Diffs durchgeführt
- [ ] Kein auskommentierter Code committed
- [ ] Keine `console.log`-Aufrufe im produktiven Code
- [ ] Keine hardcodierten Credentials oder API-Keys

### Tests
- [ ] Unit-Tests ergänzt oder angepasst
- [ ] Integrationstests ergänzt oder angepasst (falls zutreffend)
- [ ] Alle bestehenden Tests lokal erfolgreich: `ng test` / `mvn test`
- [ ] Testabdeckung nicht verschlechtert

### Code Style
- [ ] Frontend-Linting erfolgreich: `ng lint`
- [ ] Backend-Linting erfolgreich: `mvn checkstyle:check`
- [ ] Commit-Messages folgen Conventional Commits

### Dokumentation
- [ ] Inline-Kommentare für komplexe Logik ergänzt
- [ ] API-Dokumentation aktualisiert (falls zutreffend)
- [ ] CHANGELOG oder Release-Notes aktualisiert (falls zutreffend)
- [ ] README aktualisiert (falls zutreffend)

### Branch
- [ ] Branch basiert auf aktuellem `develop`
- [ ] Branch-Name folgt Konvention: `feature/<beschreibung>`

## Review-Fokus

<!-- Auf welche Bereiche soll der Reviewer besonders achten? -->
