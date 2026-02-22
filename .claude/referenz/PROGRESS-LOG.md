# Progress Log

> Chronologische Historie aller Arbeitseinheiten. Neueste Einträge oben.
> NICHTS wird gelöscht. Verworfene Ansätze, Richtungswechsel, Erkenntnisse: alles bleibt.

---

## 22.02.2026 - Session 2 (Fortsetzung): P0-04 Backend Persistence Layer

**Dauer:** ~20 Min
**Was wurde gemacht:**
- 3 JPA Entities: AnalystEntity, SecurityEntity, ResearchReportEntity
- 3 Spring Data Repositories mit Custom Query Methods (findByTicker, findByAnalystId, findBySecurityId)
- 3 Persistence Mapper: Enum-Konvertierung (String ↔ Domain-Enum), List-Konvertierung (pipe/comma-delimited ↔ List<String>)
- 3 Persistence Adapters: AnalystPersistenceAdapter, SecurityPersistenceAdapter, ReportPersistenceAdapter
- data.sql: 5 Analysten, 10 Schweizer Wertschriften (NESN, NOVN, ROG, UBSG, ZURN, SREN, ABBN, LONN, GIVN, SIKA), 10 Research Reports
- application.yml: `defer-datasource-initialization: true` + `sql.init.mode: always` für korrekte data.sql-Ausführung
- Build verifiziert: `mvn clean package -DskipTests` erfolgreich
- Spring Boot Starttest: App startet, Hibernate erstellt Tabellen, data.sql lädt fehlerfrei

**Architektur-Entscheidungen:**
- Enums als String in DB (nicht @Enumerated), damit Entity-Schicht unabhängig von Domain-Enums
- Lists als delimited Strings (pipe für Reports, comma für Analyst coverage)
- Nur ReportRepository hat save/delete (Analyst/Security sind read-only, kämen aus Fremdsystemen)

**Erkenntnisse:**
- Spring Boot 3.x führt data.sql standardmässig VOR Hibernate aus → `defer-datasource-initialization: true` nötig
- H2 `create-drop` + data.sql = perfekte Kombination für Demo-Daten (bei jedem Start frisch)
- Mapper-Schicht ist der Schlüssel zur sauberen Trennung: Domain-Objekte kennen kein JPA, JPA-Entities kennen keine Domain-Enums

**Nächster Schritt:** P0-05 Backend REST API

---

## 22.02.2026 - Session 2: P0-01 Projektinitialisierung

**Dauer:** ~30 Min
**Was wurde gemacht:**
- Java 17 via Homebrew installiert, JAVA_HOME konfiguriert
- Maven 3.9.12 installiert
- Angular CLI 21.1.4 installiert (Node.js 25)
- GitHub Repo erstellt: https://github.com/Brandea-ai/research-portal-prototype
- Initial Commit mit allen Dokumentations-Dateien gepusht
- Spring Boot 3.5.0 via Spring Initializr generiert (Web, JPA, H2, Validation, Lombok, DevTools)
- Hexagonale Package-Struktur angelegt (11 Packages)
- application.yml mit 3 Profilen (local/H2 in-memory, demo/H2 file, production/Oracle)
- WebConfig.java für CORS (localhost:4200)
- Angular 21 Projekt generiert (Standalone Components, strict TypeScript)
- PrimeNG installiert (nur als strukturelle Basis, kein Theme)
- Docker Compose + Dockerfiles (Backend: JDK 17 multi-stage, Frontend: nginx)
- nginx.conf mit API-Proxy und SPA-Routing
- Beide Builds verifiziert: `mvn clean package` BUILD SUCCESS + `ng build` erfolgreich

**Probleme und Lösungen:**
- Spring Initializr bot Boot 3.4.3 nicht an → 3.5.0 verwendet (neuer ist besser)
- Maven nutzte Java 25 statt 17 → JAVA_HOME explizit gesetzt
- `sudo` für Java-Symlink in Sandbox nicht möglich → PATH-Export statt Symlink
- Shell-Variablen in Sandbox expandieren nicht über Bash-Tool-Calls → Literale Pfade
- Flyway ohne Migrationen blockiert Start → Flyway im local-Profil deaktiviert

**Erkenntnisse:**
- Angular 21 ist aktuell (Angular CLI 21.1.4), deutlich neuer als Angular 17+ im Plan
- Standalone Components sind jetzt der Default (kein `--standalone` Flag mehr nötig)
- Spring Boot 3.5.0 kompatibel mit Java 17 (trotz JDK 25 auf dem System)
- JAVA_HOME muss bei jedem Bash-Call neu gesetzt werden (kein Shell-State-Persistence)

**Nächster Schritt:** P0-02 Design-System CSS

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
