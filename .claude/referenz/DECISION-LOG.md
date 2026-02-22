# Decision Log

> Jede wesentliche Entscheidung mit Alternativen und Begründung.
> Chronologisch, neueste oben. Nichts wird gelöscht.

---

## D-007: Fehlende Skills ergänzen (22.02.2026)

**Kontext:** Selbst-Audit ergab 6 nicht abgedeckte Skills aus der Stellenanzeige.
**Entscheidung:** Nexus, Harness, Oracle Financials, Ant, Requirements Management und Softwareanforderungsanalyse werden explizit adressiert.
**Alternativen:** Ignorieren (riskant, Kunde bemerkt Lücken) / Nur erwähnen (halbherzig)
**Begründung:** Jeder einzelne Tag in der Stellenanzeige ist dort aus einem Grund. Vollständige Abdeckung zeigt Gründlichkeit.
**Risiko:** Nexus/Harness nur als Config, nicht als laufende Instanz. Akzeptabel für einen Prototyp.

## D-006: Projektstruktur nach FIMI-Vorbild (22.02.2026)

**Kontext:** FIMI-Projekt hat mit dieser Struktur eine riesige Webseite erfolgreich verwaltet.
**Entscheidung:** Exakte Adaption der FIMI-Methodik (Tasks, Reports, Status, Rules).
**Alternativen:** Minimale Doku (schneller, aber Token-Problem) / Jira/Notion (extern, nicht im Repo)
**Begründung:** Im-Repo-Dokumentation überlebt Session-Wechsel. Token-Limits werden durch phasenweises Arbeiten umgangen.
**Risiko:** Keins. Bewährt.

## D-005: PrimeNG + Custom CSS statt Angular Material (22.02.2026)

**Kontext:** Die Stelle fordert "HTML5, CSS3" explizit als Skill.
**Entscheidung:** PrimeNG p-table als strukturelle Engine, 100% Custom CSS darüber.
**Alternativen:** Angular Material (weniger Features) / Reines Custom (zu aufwändig) / AG Grid (Overkill)
**Begründung:** PrimeNG hat Virtual Scrolling + Frozen Columns out-of-the-box. Custom CSS darüber zeigt CSS3-Kompetenz. Kein PrimeNG Theme = volle visuelle Kontrolle.
**Quellen:** Infragistics Vergleich, DiggiByte Analyse, Pencil & Paper UX Patterns

## D-004: Hexagonale Architektur statt Layered (22.02.2026)

**Kontext:** Banking 2026 Gold-Standard recherchiert.
**Entscheidung:** Hexagonale Architektur (Ports & Adapters) mit strikter Domain-Isolation.
**Alternativen:** Classic Layered (Controller→Service→Repository) / CQRS (Overkill)
**Begründung:** Hexagonal ist der de-facto Standard in regulierten Banking-Umgebungen. Zeigt DDD-Kompetenz. Domain hat null Framework-Abhängigkeiten.
**Quellen:** Baeldung "Hexagonal Architecture DDD Spring", Medium "Best Practices Spring Boot 2026"

## D-003: H2 lokal statt Oracle/PostgreSQL (22.02.2026)

**Kontext:** Prototyp muss lokal ohne externe Services laufen.
**Entscheidung:** H2 In-Memory (local), H2 File-based (demo), Oracle-Config vorbereitet (production).
**Alternativen:** PostgreSQL Docker (realistischer, aber aufwändiger) / SQLite (nicht JPA-kompatibel)
**Begründung:** H2 ist Spring Boot Standard für Prototypen. Oracle-kompatible SQL-Syntax. Profile-Switch zu Oracle ist eine Config-Änderung.
**Risiko:** H2 hat nicht alle Oracle-spezifischen Features (Analytics Functions, PL/SQL). Akzeptabel.

## D-002: Angular 17+ statt Next.js/React (22.02.2026)

**Kontext:** Stelle fordert explizit Angular + TypeScript.
**Entscheidung:** Angular 17+ mit Standalone Components.
**Alternativen:** Next.js (schneller zu bauen, aber falscher Stack) / Angular 16 (veraltet)
**Begründung:** Exakter Stack-Match. Standalone Components sind Angular 17+ Default. Zeigt aktuelles Wissen.
**Risiko:** Keins.

## D-001: Zielkunde ZKB identifiziert (22.02.2026)

**Kontext:** Stellenanzeige nennt "renommierte Bank" in Zürich mit SAFe, Oracle, Angular, Spring Boot.
**Entscheidung:** ZKB als Arbeitshypothese (75-85% Wahrscheinlichkeit).
**Alternativen:** UBS (zu groß für Hays), Julius Bär (Azure-Stack), Vontobel (möglich, 10-15%)
**Begründung:** ZKB nutzt nachweislich diesen exakten Stack, hat 50+ Research-Experten, SAFe bestätigt.
**Quellen:** ZKB Karriereseite, house-of-skills.ch Stellenangebot, ZKB IT-Bereich Webseite
**Status:** Hypothese H1, nicht bestätigt. Prototyp wird so gebaut, dass er auch für andere Banken passt.
