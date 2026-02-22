# Research Portal Prototype 1.0

> Banking Research Webapplikation für Equity/Fixed-Income-Analysten.
> Prototyp zur Demonstration von Full-Stack-Kompetenz (Angular + Spring Boot).

**Veröffentlicht:** 20.02.2026, 23:33 Uhr
**Status:** In Entwicklung
**Auftraggeber-Kontext:** Hays AG, Projekt-ID 2970720, Zürich

---

## Projekt-Navigation

Die verbindliche Lesereihenfolge ist in **`.claude/referenz/START.md`** definiert.
Das ist die EINZIGE Stelle, die die Reihenfolge festlegt. Beginne dort.

### Dateikarte (was lebt wo)

| Bereich | Datei | Zweck |
|---------|-------|-------|
| **Einstieg** | `.claude/referenz/START.md` | Lesereihenfolge, Systemkarte |
| **Was wir bauen** | `.claude/CLAUDE.md` | Standards, Design-System, Datenmodell |
| **Wie wir denken** | `.claude/GOLDENE-REGELN.md` | 16 Prinzipien, Hypothesen, KPIs |
| **Wie wir arbeiten** | `_claude-tasks/RULES.md` | 10 Regeln, Zyklen, Report-Format |
| **Was wir tun** | `_claude-tasks/RESEARCH-PORTAL_TODOs.md` | 45+ Tasks (P0/P1/P2) |
| **Wo wir stehen** | `_claude-tasks/STATUS.md` | Fortschritts-Tracker |
| **Aktueller Stand** | `.claude/referenz/CURRENT-TASK.md` | Letzter Arbeitsschritt |
| **Chronologie** | `.claude/referenz/PROGRESS-LOG.md` | Vollständiges Gedächtnis |
| **Phasen** | `.claude/referenz/CHECKLIST.md` | Phase 0/1/2 Gates |
| **Entscheidungen** | `.claude/referenz/DECISION-LOG.md` | Jede Wahl mit Begründung |
| **Anforderungen** | `.claude/referenz/REQUIREMENTS.md` | 35/35 Skills, Abnahmekriterien |
| **Architektur** | `.claude/planung/ARCHITEKTUR.md` | Hexagonal, DDD, UI Layout |
| **Design** | `STANDARDS.md` | Farben, Fonts, Coding-Standards |
| **Protokolle** | `_claude-tasks/reports/R0XXX-*.md` | Ein Report pro Arbeitseinheit |
| **Templates** | `_claude-tasks/PROMPTS.md` | Prompt-Vorlagen für Sessions |

---

## Tech-Stack

### Frontend
- **Angular 17+** (Standalone Components)
- **TypeScript** (strict mode)
- **RxJS** (State Management, HTTP, Live-Updates)
- **PrimeNG** (Daten-Grid Struktur) + **Custom CSS** (100% eigenes Design)
- **Cypress** (E2E Tests)
- **HTML5 + CSS3** (kein Tailwind, kein Bootstrap)

### Backend
- **Java 17** + **Spring Boot 3**
- **Hibernate/JPA** (Entity Mapping, JPQL)
- **H2 Database** (lokal) / Oracle-Config vorbereitet
- **Maven** (Build)
- **Flyway** (DB Migrations)

### DevOps
- **Jenkinsfile** (CI/CD Pipeline)
- **GitLab CI** (.gitlab-ci.yml)
- **Docker** + **Docker Compose**
- **Git-Flow** Branching-Strategie

---

## Lokal starten

```bash
# Backend (Spring Boot + H2)
cd backend && mvn spring-boot:run

# Frontend (Angular Dev Server)
cd frontend && npm start

# Alles zusammen
docker-compose up
```

---

## Wahrscheinlicher Endkunde

**Zürcher Kantonalbank (ZKB)** (75-85% Wahrscheinlichkeit)
- Evidenz: Java/Spring Boot + Angular + SAFe + Oracle + Research-Team + "SLX" Framework
- Über 50 Research-Experten, eigenes Portal (research.zkb.ch)
- Konservativ, stabil, reguliert (FINMA 2023/1)

---

## Projektmethodik

- Evidence-based und evaluiert
- Gold-Standard und Best-Practice (Hexagonale Architektur, DDD, SAFe)
- Framework-getrieben (MECE, TDD, DDD, Git-Flow)
- Phasenweise Entwicklung mit Reports nach jeder Arbeitseinheit
- Dynamische Dokumentation (Pläne entwickeln sich weiter)
