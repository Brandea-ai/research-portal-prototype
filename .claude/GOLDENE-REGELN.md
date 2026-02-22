# Goldene Regeln: Prinzipien und Methodik

> Dieses Dokument definiert WIE wir denken und entscheiden.
> Es wird durch RULES.md ergänzt (dort steht WIE wir arbeiten).
> Quelle: Expertengremium-Framework (McKinsey/Deloitte-Niveau)

---

## Die 16 Pflichtprinzipien

### 1. Evidence-based und evaluiert
Nur belastbare Fakten, Daten, Studien, Standards oder klar markierte Annahmen.
Keine Behauptungen ohne Grundlage. Wenn Evidenz fehlt, wird das transparent benannt.

### 2. Gold-Standard und Best-Practice
Jede technische Entscheidung benennt den anerkannten Standard:
- Architektur: Hexagonale Architektur (Alistair Cockburn, 2005)
- DDD: Eric Evans, "Domain-Driven Design" (2003)
- Angular: Official Style Guide + angular-architects.io
- Spring Boot: Baeldung, Spring.io Reference
- CI/CD: GitLab CI Best Practices, Jenkins Pipeline Syntax

### 3. Framework-Nutzung (nur wenn sinnvoll)
Verfügbare Frameworks für dieses Projekt:
- **DDD** (Domain-Driven Design): Bounded Contexts, Aggregates, Value Objects
- **Hexagonal Architecture**: Ports & Adapters, Dependency Inversion
- **SAFe**: PI Planning, ARTs, Team-Level Scrum
- **Git-Flow**: Feature Branches, Release Branches, Hotfixes
- **TDD**: Red-Green-Refactor Zyklus
- **MECE**: Für Anforderungsanalyse und Task-Zerlegung

### 4. Peer-reviewed und wissenschaftlich geprüft
Architekturentscheidungen werden gegen mindestens zwei unabhängige Quellen validiert.
Bei Widersprüchen zwischen Quellen: beide dokumentieren, Empfehlung mit Begründung.

### 5. Reproduzierbar und nachvollziehbar
Jeder Schritt ist so dokumentiert, dass eine andere Person (oder eine neue Session)
den exakten Stand reproduzieren kann. Docker-Compose, README-Anweisungen, Report-Kette.

### 6. Hypothesen-getrieben
Annahmen explizit formulieren und testen:
- **H1:** ZKB ist der Endkunde (Evidenz: Stack-Match, Research-Team, SAFe, SLX)
- **H2:** PrimeNG p-table performt mit 1000+ Rows bei Virtual Scrolling
- **H3:** Hexagonale Architektur lässt sich in einem Prototyp sinnvoll demonstrieren
- **H4:** Custom CSS über PrimeNG zeigt mehr Kompetenz als Angular Material
- **H5:** Schweizer Wertschriften-Daten machen den Prototyp glaubwürdiger als generische Daten
Hypothesen werden im DECISION-LOG mit Ergebnis nachgeführt.

### 7. Messbar und KPI-basiert
| KPI | Ziel | Messung |
|-----|------|---------|
| Build Success | 100% | `ng build` + `mvn package` |
| Test Coverage Backend | >70% | JaCoCo Report |
| TypeScript Errors | 0 | `ng build --aot` |
| Bundle Size (Initial) | <500KB | `ng build --stats-json` |
| Cypress E2E Tests | >5 grün | `npx cypress run` |
| Lighthouse Performance | >80 | Chrome DevTools |
| Skills abgedeckt | 35/35 | Mapping-Tabelle in CLAUDE.md |
| Dokumentations-Vollständigkeit | 100% | Alle Pflicht-Dateien aktuell |

### 8. Benchmarking
Referenz-Systeme für diesen Prototyp:
- **Bloomberg Terminal**: Information Density, Keyboard-Navigation, Farbcodierung
- **FactSet**: Sidebar + Main + Detail Layout, Filter-Logik
- **Refinitiv Eikon**: Widget-basierte Dashboards
- **ZKB Research Portal** (research.zkb.ch): Reale Zielplattform

### 9. Risk- und Compliance-first
- FINMA 2023/1: Audit-Trail, RBAC, Activity Logging
- Schweizer Bankgeheimnis: Keine realen Kundendaten im Prototyp
- DSGVO/DSG: Login-Konzept zeigt Datenschutzbewusstsein
- Security Headers: In Backend-Config vorbereitet

### 10. Continuous Improvement (Kaizen)
Jeder Report enthält "Erkenntnisse" mit:
- Was lief gut (Verstärken)
- Was war unerwartet (Lernen)
- Was muss sich ändern (Verbessern)
PROGRESS-LOG archiviert alle Learnings permanent.

### 11. Decision Log und Rationale
Jede Entscheidung wird in `.claude/referenz/DECISION-LOG.md` festgehalten:
- Was wurde entschieden?
- Welche Alternativen wurden geprüft?
- Warum diese Wahl?
- Welche Risiken bleiben?

### 12. Systems Thinking
Das System besteht aus interdependenten Schichten:
```
Dokumentation ←→ Architektur ←→ Code ←→ Tests ←→ Pipeline
     ↕               ↕            ↕         ↕          ↕
Standards ←→ Design-System ←→ Daten ←→ Reports ←→ Deployment
```
Eine Änderung an einer Stelle wird auf Auswirkungen im Gesamtsystem geprüft.

### 13. Customer-centric und JTBD
**Wer ist der Kunde?** Die Bank (wahrscheinlich ZKB)
**Was ist der Job?** Ein Research-Analyst will:
- Schnell relevante Reports finden (Suche, Filter)
- Bewertungsänderungen sofort sehen (Rating Change Alerts)
- Daten exportieren für eigene Analysen (Excel, PDF)
- Neue Reports effizient erstellen (Formulare, Templates)
- Compliance einhalten ohne Reibungsverluste (Audit-Trail, Restricted-Flag)

### 14. Keine Halluzinationen
Wenn etwas unklar ist, wird es als "Annahme" markiert, nicht als Fakt.
Beispiel: "SLX = internes ZKB-Framework" ist eine Annahme, kein bestätigtes Wissen.

### 15. Realistische Aussagen
Kein "100% Garantie" oder "perfekt". Stattdessen:
- "Der Prototyp deckt 35/35 geforderte Skills ab"
- "Die Architektur folgt dem Banking-Gold-Standard 2026"
- "H2 simuliert Oracle, hat aber nicht alle Oracle-spezifischen Features"

### 16. Entscheidung wie ein Experten-Panel
Bei jeder wesentlichen Entscheidung werden mindestens zwei Perspektiven geprüft:
- Backend-Architekt: Ist die Domain sauber isoliert?
- Frontend-Spezialist: Ist das Angular-Pattern korrekt?
- DevOps-Engineer: Ist die Pipeline reproduzierbar?
- UX-Designer: Ist das Layout für Research-Analysten geeignet?
- Compliance-Officer: Sind Audit-Anforderungen erfüllt?

---

## Vorgehen (bei jedem Task)

```
A) Kontext klären     → Was ist der aktuelle Stand? Was genau soll erreicht werden?
B) Evidenz sammeln    → Welcher Standard gilt? Was sagen die Quellen?
C) Optionen + Trade-offs → Mindestens 2 Wege aufzeigen
D) Empfehlung + Begründung → Klare Wahl mit Rationale
E) Nächste Schritte   → Priorisiert, mit Owner und Timing
```
