# Progress Log

> Chronologische Historie aller Arbeitseinheiten. Neueste Einträge oben.
> NICHTS wird gelöscht. Verworfene Ansätze, Richtungswechsel, Erkenntnisse: alles bleibt.

---

## 22.02.2026 - Session 1b: Systemische Kohärenz-Korrektur

**Dauer:** ~20 Min
**Auslöser:** Selbst-Audit gegen 4 Blueprint-Dateien und alle 41 Stellenanzeige-Tags.

**Gefundene Probleme (7):**
1. Drei verschiedene Lesereihenfolgen (README, RULES, START) → Vereinheitlicht: nur START.md definiert
2. Zwei Report-Speicherorte → .claude/reports/ gelöscht, nur _claude-tasks/reports/ bleibt
3. GOLDENE-REGELN.md fehlte → Erstellt mit allen 16 Expertengremium-Prinzipien
4. Entwicklungszyklus + Fehler-Management fehlte in RULES → Ergänzt
5. 6 Skills nicht abgedeckt (Nexus, Harness, Export, i18n, Security, Git-Flow) → 6 neue P2-Tasks
6. DECISION-LOG fehlte → Erstellt mit 7 dokumentierten Entscheidungen
7. REQUIREMENTS.md fehlte → Erstellt mit 35/35 Skills-Mapping und formalen Abnahmekriterien

**Korrigierte Dateien:**
- `.claude/referenz/START.md` → Einzige Lesereihenfolge, Systemkarte
- `.claude/GOLDENE-REGELN.md` → NEU: 16 Prinzipien, Hypothesen, KPIs
- `.claude/referenz/DECISION-LOG.md` → NEU: 7 Entscheidungen
- `.claude/referenz/REQUIREMENTS.md` → NEU: 35/35 Skills, Abnahmekriterien
- `_claude-tasks/RULES.md` → Erweitert: Zyklen, Fehler-Management, Verboten-Liste, Querverweise
- `_claude-tasks/RESEARCH-PORTAL_TODOs.md` → 6 neue Tasks (P2-22 bis P2-27), P2-18 erweitert
- `_claude-tasks/STATUS.md` → Synchronisiert, Abhängigkeiten-Spalte
- `.claude/referenz/CHECKLIST.md` → 13 P2-Tasks, Querverweise
- `.claude/referenz/CURRENT-TASK.md` → Aktualisiert
- `README.md` → Lesereihenfolge verweist auf START.md, Dateikarte
- `.claude/CLAUDE.md` → Session-Protokoll verweist auf START.md und RULES.md

**Erkenntnisse:**
- Kohärenz ist kein Feature, sondern eine Grundvoraussetzung
- Jede Datei muss wissen, wohin sie gehört und worauf sie verweist
- Drei verschiedene Quellen für die gleiche Information = garantierte Inkonsistenz
- 35/35 Skills-Mapping gibt Sicherheit, dass nichts vergessen wird

**Ergebnis:** Alle Dateien referenzieren korrekt aufeinander. Kein Widerspruch. System ist kohärent.

---

## 22.02.2026 - Session 1: Projektgründung

**Dauer:** ~45 Min
**Was wurde gemacht:**
- Tiefgründige Recherche durchgeführt:
  - Wahrscheinlicher Kunde identifiziert: ZKB (75-85%)
  - Banking Research Portal Gold Standards (Bloomberg, FactSet, Refinitiv)
  - Angular 17+ / Spring Boot 3 Best Practices 2026
  - FINMA 2023/1 Compliance-Anforderungen
  - SAFe in Schweizer Banking-IT
  - UI/UX Patterns für Financial Dashboards
  - Datenmodell für Equity Research
- Design-Entscheidung getroffen: Full Stack Match (Angular + Spring Boot)
- Farbschema definiert: #0A0A0A / #FFFFFF / #38BDF8
- Bloomberg-Prinzip: Accent = Signal, nicht Dekoration
- Typografie: Inter (UI) + JetBrains Mono (Daten)
- Keine Icons, nur Typografie und Struktur
- Komplette Projektstruktur nach FIMI-Vorbild erstellt
- 42 Tasks in 3 Phasen definiert (P0: 7, P1: 7, P2: 7 + Subtasks)
- Alle MD-Dateien der Wissensdatenbank erstellt

**Erkenntnisse:**
- ZKB nutzt nachweislich den exakt geforderten Stack
- "SLX" ist ein internes ZKB-Framework, nicht öffentlich dokumentiert
- PrimeNG p-table + Custom CSS = optimaler Kompromiss (Struktur + Skill-Beweis)
- Hexagonale Architektur ist Pflichtstandard in Banking 2026
- Schweizer Terminologie wichtig ("Wertschriften", "Kurs", "Kursziel")

**Verworfene Ansätze:**
- Next.js/React: Verworfen, weil die Stelle explizit Angular fordert
- Angular Material: Verworfen, weil Custom CSS mehr CSS3-Kompetenz zeigt
- Supabase/Cloud DB: Verworfen, H2 lokal ist einfacher und zeigt JPA/Hibernate besser

**Nächster Schritt:** P0-01 Projektinitialisierung (GitHub + Scaffolding)
