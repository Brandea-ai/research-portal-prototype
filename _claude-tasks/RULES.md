# Verhaltensregeln: Research Portal Prototype

> Diese Regeln gelten IMMER. Keine Ausnahmen. Keine Abkürzungen.

---

## Die 10 Goldenen Regeln

### R1: KEINE Änderungen ohne Verifikation
Bevor du eine Datei änderst:
1. Lies den aktuellen Stand der Datei
2. Verstehe den Kontext (welche anderen Dateien hängen davon ab?)
3. Prüfe ob die Änderung mit dem Gesamtsystem harmoniert

### R2: Ein Task pro Arbeitseinheit
- Fokus auf EINEN Task, diesen KOMPLETT abschließen
- Kein Springen zwischen Tasks
- Wenn ein Task weitere Tasks aufdeckt: dokumentieren, nicht sofort umsetzen

### R3: Verifiziere BEVOR du arbeitest
Vor jeder Arbeit prüfen:
- [ ] Ist der Task noch relevant?
- [ ] Ist er schon erledigt?
- [ ] Gibt es Abhängigkeiten zu anderen Tasks?
- [ ] Ist die Reihenfolge korrekt?

### R4: Report-Pflicht nach jeder Arbeit
Nach JEDER Arbeitseinheit:
1. Report schreiben: `_claude-tasks/reports/RXXXX-[THEMA].md`
2. STATUS.md aktualisieren
3. CURRENT-TASK.md aktualisieren
4. PROGRESS-LOG.md ergänzen (neueste Einträge oben)
5. CHECKLIST.md Status prüfen

### R5: Vollständiges Projektwissen
Bei jeder neuen Session die verbindliche Lesereihenfolge in `.claude/referenz/START.md` befolgen.
Das ist die EINZIGE Stelle, die die Reihenfolge definiert. Keine andere Datei überschreibt sie.

### R6: Keine Annahmen
- Unklar? FRAGEN.
- Nicht sicher ob Angular oder React? FRAGEN.
- Nicht sicher ob Feature gebraucht wird? FRAGEN.
- Lieber eine Rückfrage zu viel als ein Fehler

### R7: Evidence-based arbeiten
- Jede technische Entscheidung mit Begründung
- Standards referenzieren (welcher Gold-Standard? welches Framework?)
- Keine "das macht man halt so" Argumente
- Quellen nennen wo möglich

### R8: Architektur-Integrität wahren
- Hexagonale Architektur (Backend) NICHT verletzen
- Domain darf KEINE Framework-Abhängigkeiten haben
- Angular Standalone Components, KEINE NgModules
- Custom CSS über PrimeNG, NICHT PrimeNG-Themes verwenden

### R9: Phasenweise bauen, Token-bewusst
- Jede Phase isoliert funktionsfähig
- Nach jeder Phase: Report + Commit
- Scope begrenzen: lieber weniger, aber komplett
- Bei Token-Limit-Gefahr: sauber abschließen, Status sichern

### R10: Professionell und nachvollziehbar
- Alles dokumentiert
- Alles reproduzierbar
- Alles nachvollziehbar für die nächste Session
- Commit-Messages auf Deutsch: `[Typ]: Beschreibung`

---

## Status-Codes

| Code | Bedeutung |
|------|-----------|
| `[ ]` | Offen |
| `[~]` | In Arbeit |
| `[x]` | Erledigt |
| `[!]` | Blockiert (Grund dokumentieren) |
| `[-]` | Nicht relevant (Grund dokumentieren) |

---

## Report-Format (Pflicht)

```markdown
# Report RXXXX: [Thema]

**Datum:** TT.MM.JJJJ
**Task-Referenz:** P[0/1/2]-[Nr]
**Dauer:** ca. X Minuten

## Kontext
Was war der Ausgangspunkt?

## Was wurde gemacht
- Datei X: [Art der Änderung] - [Begründung]
- Datei Y: [Art der Änderung] - [Begründung]

## Geänderte Dateien
| Datei | Änderung | Begründung |
|-------|----------|------------|
| ... | ... | ... |

## Kompatibilitäts-Check
Was wurde geprüft? Was hängt davon ab?

## Test-Anleitung
Schritt-für-Schritt Validierung

## Rollback-Plan
Wie rückgängig machen?

## Erkenntnisse
- Was lief gut?
- Was war unerwartet?
- Was muss beachtet werden?

## Nächste Schritte
Priorisierte Liste
```

---

## Commit-Typen

| Typ | Verwendung |
|-----|-----------|
| `feat` | Neues Feature |
| `fix` | Bugfix |
| `docs` | Dokumentation |
| `refactor` | Code-Umstrukturierung |
| `style` | Formatierung, CSS |
| `test` | Tests hinzufügen/ändern |
| `config` | Konfiguration, Build |
| `chore` | Wartung, Aufräumen |

Format: `[typ]: Beschreibung auf Deutsch`

---

## Entwicklungszyklus (pro Arbeitseinheit)

Jeder Task durchläuft diesen Zyklus vollständig. Kein Schritt wird übersprungen.

```
ANALYSE     → Aktuellen Stand vollständig erfassen
               Was existiert? Was hängt davon ab? Was könnte brechen?

PLANUNG     → Scope definieren, betroffene Dateien identifizieren
               Welche Dateien werden geändert? In welcher Reihenfolge?

UMSETZUNG   → Isoliert bauen, eine Komponente pro Iteration
               Nicht zwei Dinge gleichzeitig. Fokus.

VALIDIERUNG → Funktionalität prüfen, Regression ausschließen
               Build? Tests? Konsolen-Fehler? Abhängige Komponenten?

DOKUMENTATION → Report erstellen, Steuerungsdateien aktualisieren
                 R0XXX.md + STATUS.md + CURRENT-TASK.md + PROGRESS-LOG.md

FREIGABE    → Commit nur nach expliziter Genehmigung durch Armend
               Kein eigenständiges Pushen. Immer erst fragen.
```

---

## Fehler-Management

Bei Fehlern gilt eine strikte Reihenfolge. Kein Weiterarbeiten auf kaputtem Stand.

```
STOPP       → Sofort aufhören. Nicht auf fehlerhaftem Code weiterarbeiten.

ANALYSE     → Ursache isolieren. Nicht Symptome behandeln.
               Welcher Commit hat es eingeführt? Welche Datei?

FIX         → Minimal-invasiv korrigieren. Keine "Gelegenheitsrefactorings".

VALIDIERUNG → Fix verifizieren. Regression ausschließen.
               Hat der Fix andere Stellen beeinflusst?

REPORT      → Ursache, Lösung und Präventionsmaßnahme dokumentieren.
               Was lernen wir daraus? Wie verhindern wir es künftig?
```

---

## Verboten (absolute Grenzen)

- Bestehende Funktionalität ohne Rücksprache verändern oder entfernen
- Gegen dokumentierte Regeln (CLAUDE.md, RULES.md, GOLDENE-REGELN.md) arbeiten
- Architekturentscheidungen eigenmächtig treffen
- Fehler mit Workarounds überdecken statt sie zu beheben
- Safety-Checks umgehen (`--no-verify`, `--force`, `--hard` etc.)
- Icons oder Icon-Sets einbauen
- PrimeNG Themes aktivieren
- Spring-Annotationen in der Domain-Schicht verwenden
- `any` in TypeScript verwenden

---

## Querverweise

| Datei | Beziehung zu RULES |
|-------|-------------------|
| `.claude/referenz/START.md` | Definiert die Lesereihenfolge (R5) |
| `.claude/GOLDENE-REGELN.md` | Definiert die Denkprinzipien (R7 basiert darauf) |
| `.claude/CLAUDE.md` | Definiert die technischen Standards (R8 basiert darauf) |
| `.claude/referenz/REQUIREMENTS.md` | Definiert die Anforderungen (R3 prüft dagegen) |
| `.claude/referenz/DECISION-LOG.md` | Hält Architekturentscheidungen fest (R7, R8) |
| `_claude-tasks/STATUS.md` | Zeigt den Fortschritt (R4 aktualisiert ihn) |
| `STANDARDS.md` | Definiert Design/Coding-Standards (R8, R10) |
