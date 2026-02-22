# Prompt-Templates

> Standardisierte Prompts für wiederkehrende Aufgaben in diesem Projekt.

---

## Session-Start Prompt

```
Lies bitte folgende Dateien in dieser Reihenfolge:
1. README.md
2. _claude-tasks/RULES.md
3. _claude-tasks/STATUS.md
4. .claude/referenz/CURRENT-TASK.md
5. .claude/CLAUDE.md

Gib mir eine kurze Zusammenfassung und frag, wo ich weiterarbeiten soll.
```

## Neuen Task starten

```
Ich möchte an Task P[X]-[XX] arbeiten: [Taskname].
Lies zuerst den Task in RESEARCH-PORTAL_TODOs.md, prüfe die Abhängigkeiten in STATUS.md,
und lies die relevanten bestehenden Dateien.
Dann schlage einen Plan vor, bevor du anfängst.
```

## Report anfordern

```
Erstelle einen Report für die aktuelle Arbeitseinheit:
_claude-tasks/reports/RXXXX-[THEMA].md

Aktualisiere danach:
- STATUS.md
- CURRENT-TASK.md
- PROGRESS-LOG.md
- CHECKLIST.md
```

## Code Review

```
Prüfe den Code auf:
1. TypeScript strict mode Violations
2. Angular Best Practices (Standalone, OnPush, inject())
3. Hexagonale Architektur Verletzungen (Domain darf kein Spring importieren)
4. CSS: Keine hardcodierten Farben, nur Custom Properties
5. Keine Icons oder Icon-Imports
```

## Architektur-Check

```
Prüfe ob die aktuelle Implementierung mit .claude/planung/ARCHITEKTUR.md übereinstimmt:
1. Ist die Package-Struktur korrekt?
2. Sind Domain und Persistence sauber getrennt?
3. Gibt es Framework-Imports in der Domain-Schicht?
4. Werden DTOs statt JPA Entities in Responses verwendet?
```
