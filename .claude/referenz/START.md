# Session-Einstiegspunkt

> EINZIGE verbindliche Lesereihenfolge. Alle anderen Dateien referenzieren hierher.

## Pflicht-Lesereihenfolge (bei jeder neuen Session)

| Schritt | Datei | Warum |
|---------|-------|-------|
| 1 | `README.md` | Projektüberblick, Tech-Stack, Navigation |
| 2 | `.claude/CLAUDE.md` | Standards, Design-System, Datenmodell |
| 3 | `.claude/GOLDENE-REGELN.md` | 16 Prinzipien, Methodik, Hypothesen |
| 4 | `_claude-tasks/RULES.md` | Arbeitsregeln, Zyklen, Report-Format |
| 5 | `_claude-tasks/STATUS.md` | Aktueller Fortschritt aller Tasks |
| 6 | `.claude/referenz/CURRENT-TASK.md` | Was war zuletzt? Was steht an? |
| 7 | `.claude/referenz/PROGRESS-LOG.md` | Chronologie, Learnings |
| 8 | `.claude/referenz/CHECKLIST.md` | Phasenfortschritt |

## Bei Bedarf (nicht jede Session)

| Datei | Wann lesen |
|-------|-----------|
| `.claude/referenz/REQUIREMENTS.md` | Wenn Skills-Abdeckung geprüft werden soll |
| `.claude/referenz/DECISION-LOG.md` | Wenn eine Architekturentscheidung ansteht |
| `.claude/planung/ARCHITEKTUR.md` | Wenn Code geschrieben wird |
| `STANDARDS.md` | Wenn CSS/Design-Entscheidungen anstehen |
| `_claude-tasks/RESEARCH-PORTAL_TODOs.md` | Wenn der nächste Task gewählt wird |
| `_claude-tasks/PROMPTS.md` | Prompt-Templates für wiederkehrende Aktionen |

## Nach dem Einlesen

1. Kurze Zusammenfassung des Projektstands
2. Offene Punkte oder Risiken benennen
3. Frage: "Wo soll ich weiterarbeiten?"

## Datei-Beziehungen (Systemkarte)

```
README.md ──────────────────────── Einstieg, verweist auf alles
    │
    ├── .claude/CLAUDE.md ──────── WAS wir bauen (Standards, Modell)
    │       │
    │       ├── GOLDENE-REGELN.md  WIE wir denken (Prinzipien)
    │       │
    │       └── referenz/
    │           ├── START.md ───── DIESE DATEI (Lesereihenfolge)
    │           ├── CURRENT-TASK.md Aktueller Stand
    │           ├── PROGRESS-LOG.md Chronologie
    │           ├── CHECKLIST.md ── Phasenfortschritt
    │           ├── DECISION-LOG.md Entscheidungen + Begründungen
    │           └── REQUIREMENTS.md Formale Anforderungen (35/35 Skills)
    │
    ├── .claude/planung/
    │       └── ARCHITEKTUR.md ─── WIE wir bauen (Hexagonal, DDD, Layout)
    │
    ├── _claude-tasks/ ─────────── WAS wir tun
    │       ├── RULES.md ───────── WIE wir arbeiten (10 Regeln, Zyklen)
    │       ├── RESEARCH-PORTAL_TODOs.md  Alle 45+ Tasks (P0/P1/P2)
    │       ├── STATUS.md ──────── Fortschritt
    │       ├── PROMPTS.md ─────── Prompt-Templates
    │       └── reports/ ──────── R0001, R0002, ... (eine Stelle, kein Duplikat)
    │
    ├── STANDARDS.md ───────────── WIE es aussieht (Farben, Fonts, Coding)
    │
    ├── frontend/ ──────────────── Angular 17+ Code
    └── backend/ ───────────────── Spring Boot 3 Code
```
