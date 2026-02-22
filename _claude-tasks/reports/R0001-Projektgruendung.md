# Report R0001: Projektgründung

**Datum:** 22.02.2026
**Task-Referenz:** Infrastruktur (vor P0-01)
**Dauer:** ca. 60 Minuten

## Kontext

Neue Bewerbung auf Hays AG Projekt 2970720: Applikations-Entwickler (m/w/d), Zürich, 24 Monate Freelance. Gefordert: Angular + Spring Boot Full-Stack für Banking Research bei einer renommierten Schweizer Bank.

Aufgabe: Einen hochwertigen Prototypen bauen, der dem Kunden zeigt, dass wir den geforderten Stack beherrschen.

## Was wurde gemacht

### 1. Tiefgründige Recherche (Evidence-based)
- **Kundenidentifikation:** Zürcher Kantonalbank (ZKB) mit 75-85% Wahrscheinlichkeit identifiziert
  - Evidenz: Java/Spring Boot + Angular + SAFe + Oracle + Research-Team + "SLX" Framework
  - ZKB hat 50+ Research-Experten, eigenes Portal (research.zkb.ch)
- **Gold-Standard Architektur:** Hexagonale Architektur + DDD als Pflicht in Banking 2026
- **UI/UX Patterns:** Bloomberg Terminal, FactSet, Refinitiv analysiert
  - 3-Zonen-Layout (Sidebar + Main + Detail) als Industriestandard
  - Dichte Informationsdarstellung, nicht Consumer-UX
- **Angular Best Practices 2026:** Standalone Components, Signals + RxJS, OnPush
- **Spring Boot Best Practices:** Ports & Adapters, strikte Domain/Persistence Trennung
- **FINMA 2023/1:** Audit-Trail, RBAC, Activity Logging als Compliance-Anforderung
- **SAFe in Banking:** PI Planning, ARTs, Scrum-Team-Integration

### 2. Design-Entscheidungen
| Entscheidung | Wahl | Begründung |
|-------------|------|------------|
| Framework | Angular (nicht Next.js/React) | Stelle fordert Angular explizit |
| CSS | Custom über PrimeNG Struktur | Zeigt HTML5/CSS3 Kompetenz |
| Backend | Hexagonale Architektur | Gold-Standard Banking 2026 |
| Datenbank (lokal) | H2 In-Memory | Einfach, zeigt JPA/Hibernate |
| Icons | Keine | User-Vorgabe: seriös, nur Typografie |
| Farben | #0A0A0A / #FFFFFF / #38BDF8 | User-Vorgabe: Schwarz/Weiß + Hellblau |
| Fonts | Inter + JetBrains Mono | Bloomberg-Prinzip: Tabular Numerics |

### 3. Projektstruktur erstellt
Komplette Wissensdatenbank nach FIMI-Vorbild:
- README.md (Navigation + Projektüberblick)
- .claude/CLAUDE.md (Standards, Design, Datenmodell)
- .claude/referenz/ (START, CURRENT-TASK, PROGRESS-LOG, CHECKLIST)
- .claude/planung/ARCHITEKTUR.md (Hexagonal + DDD + UI Layout)
- _claude-tasks/RULES.md (10 Goldene Regeln)
- _claude-tasks/RESEARCH-PORTAL_TODOs.md (42 Tasks in 3 Phasen)
- _claude-tasks/STATUS.md (Fortschritts-Tracker)
- STANDARDS.md (Design-System, Coding-Standards)

## Geänderte Dateien

| Datei | Änderung | Begründung |
|-------|----------|------------|
| README.md | Neu erstellt | Projekt-Navigation, Tech-Stack |
| .claude/CLAUDE.md | Neu erstellt | Single Source of Truth |
| .claude/referenz/START.md | Neu erstellt | Session-Einstiegspunkt |
| .claude/referenz/CURRENT-TASK.md | Neu erstellt | Aktueller Stand |
| .claude/referenz/PROGRESS-LOG.md | Neu erstellt | Chronologie |
| .claude/referenz/CHECKLIST.md | Neu erstellt | Phasenfortschritt |
| .claude/planung/ARCHITEKTUR.md | Neu erstellt | Architektur + DDD |
| _claude-tasks/RULES.md | Neu erstellt | 10 Goldene Regeln |
| _claude-tasks/RESEARCH-PORTAL_TODOs.md | Neu erstellt | 42 Tasks |
| _claude-tasks/STATUS.md | Neu erstellt | Tracker |
| STANDARDS.md | Neu erstellt | Design-System |

## Kompatibilitäts-Check

Nicht anwendbar (Erstinitialisierung, keine bestehenden Komponenten).

## Test-Anleitung

1. Ordner auf Desktop prüfen: `/Users/brandea/Desktop/research-portal-prototype/`
2. Alle MD-Dateien durchlesen und auf Konsistenz prüfen
3. Keine Code-Dateien vorhanden (noch kein Scaffolding)

## Rollback-Plan

Ordner löschen: `rm -rf /Users/brandea/Desktop/research-portal-prototype/`

## Erkenntnisse

- ZKB als wahrscheinlicher Kunde gibt dem Prototyp eine klare Richtung
- Schweizer Terminologie (Wertschriften, Kurs, Kursziel) ist wichtig
- Bloomberg-Prinzip "Accent = Signal" verhindert Design-Überladung
- FIMI-Projektstruktur hat sich als Vorlage bewährt: klar, nachvollziehbar, skalierbar

## Nächste Schritte

1. **P0-01:** GitHub Repository erstellen (öffentlich)
2. **P0-01:** Angular 17+ Projekt scaffolden
3. **P0-01:** Spring Boot 3 Projekt scaffolden
4. **P0-02:** Design-System CSS Grundlage
