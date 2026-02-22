# Anforderungsanalyse: Research Portal Prototype 1.0

> Formale Anforderungsanalyse basierend auf der Stellenanzeige Hays AG 2970720.
> Quelle: Stellenbeschreibung, Qualifikationsprofil, Tag-Liste.
> Methode: MECE (Mutually Exclusive, Collectively Exhaustive)

---

## 1. Funktionale Anforderungen (Was muss das System tun?)

### 1.1 Research-Verwaltung (Kernfunktion)
| ID | Anforderung | Quelle | Priorität |
|----|-------------|--------|-----------|
| FA-01 | Research-Reports erstellen, bearbeiten, löschen | "Neu- und Weiterentwicklung moderner Webapplikationen" | P0 |
| FA-02 | Reports nach Kriterien suchen und filtern | "Betriebssupport für Research-Analysten" | P1 |
| FA-03 | Reports mit Rating und Kursziel versehen | "Research, Wertschriften, Anlagegeschäft" | P1 |
| FA-04 | Rating-Verlauf pro Wertschrift anzeigen | "Erarbeitung von Lösungsvarianten" | P1 |
| FA-05 | Wertschriften-Stammdaten verwalten | "Wertschriften oder Anlagegeschäft" | P1 |

### 1.2 Benutzeroberfläche
| ID | Anforderung | Quelle | Priorität |
|----|-------------|--------|-----------|
| FA-06 | Dashboard mit KPIs und aktuellsten Reports | "Betreuung moderner Webapplikationen" | P1 |
| FA-07 | Login mit Authentifizierung | "Banking-Umfeld, Compliance" | P0 |
| FA-08 | 3-Zonen-Layout (Sidebar, Main, Detail) | Bloomberg/FactSet Benchmarking | P0 |
| FA-09 | Responsive Design (Desktop, Tablet, Mobile) | "Moderne Webapplikationen" | P1 |
| FA-10 | Datenexport (CSV/Excel) | JTBD: Analysten exportieren Daten | P2 |

### 1.3 Datenverarbeitung
| ID | Anforderung | Quelle | Priorität |
|----|-------------|--------|-----------|
| FA-11 | XML-Reports importieren via XPath | "XPath" als geforderte Skill | P2 |
| FA-12 | SQL-basierte Datenabfragen | "SQL" als geforderte Skill | P0 |
| FA-13 | Demo-Daten mit Schweizer Wertschriften | "Wertschriften, Anlagegeschäft" | P0 |

---

## 2. Nicht-funktionale Anforderungen (Wie muss das System sein?)

### 2.1 Architektur und Code-Qualität
| ID | Anforderung | Quelle | Nachweis |
|----|-------------|--------|----------|
| NFA-01 | Hexagonale Architektur (Ports & Adapters) | "Software Architecture", "DDD" | Package-Struktur |
| NFA-02 | Domain-Driven Design | "Domain Driven Design" | Bounded Contexts |
| NFA-03 | Test-Driven Development | "Test-Driven Development" | Tests zuerst, >70% Coverage |
| NFA-04 | Clean Code, TypeScript strict | "Sehr gute Kenntnisse TypeScript" | tsconfig strict: true |

### 2.2 Tech-Stack Compliance (vollständig, 35/35)
| ID | Gefordert | Im Prototyp | Wo |
|----|-----------|-------------|-----|
| TS-01 | Java | Java 17 | Backend |
| TS-02 | Spring Framework | Spring Boot 3 | Backend |
| TS-03 | Spring Boot | Spring Boot 3.2+ | Backend |
| TS-04 | Hibernate | Hibernate 6 / JPA | Persistence Adapter |
| TS-05 | Java Persistence API | JPA Repositories, JPQL | Persistence Adapter |
| TS-06 | SQL | H2 SQL, data.sql, Custom Queries | Backend |
| TS-07 | Oracle Financials | Oracle-Profil vorbereitet, Oracle-SQL-Syntax | application-production.yml |
| TS-08 | XPath | XML Report Import Service | P2-17 |
| TS-09 | Angular / AngularJS | Angular 17+ Standalone | Frontend |
| TS-10 | TypeScript | Strict Mode, keine `any` | Frontend |
| TS-11 | RxJS | State Management, HTTP, Live-Updates | Frontend Services |
| TS-12 | Node.js | Angular CLI, Express Dev Server | Frontend Build |
| TS-13 | NPM | Package Management | Frontend |
| TS-14 | HTML5 | Semantisches Markup | Frontend Templates |
| TS-15 | CSS | 100% Custom CSS, CSS Custom Properties | Frontend Styles |
| TS-16 | Apache Maven | pom.xml, Multi-Module Build | Backend |
| TS-17 | Nexus | Maven settings.xml mit Nexus Repository Config | Backend Config |
| TS-18 | Git | Git-Flow Branching Strategie | Projekt |
| TS-19 | GitLab | .gitlab-ci.yml Pipeline | DevOps |
| TS-20 | Jenkins | Jenkinsfile (Declarative Pipeline) | DevOps |
| TS-21 | Cypress | E2E Tests (Login, CRUD, Filter) | Frontend Tests |
| TS-22 | Ant | Erwähnung als Legacy-Build-Erfahrung | README |
| TS-23 | Harness | Harness Pipeline Config vorbereitet | DevOps |
| TS-24 | Scrum | Sprint-basierte Entwicklung | Dokumentation |
| TS-25 | Scaled Agile Framework | SAFe-Konzepte (PI, ART, Team) | README, Dokumentation |
| TS-26 | Domain Driven Design | Bounded Contexts, Aggregates, Value Objects | Backend Package-Struktur |
| TS-27 | Test-Driven Development | JUnit 5 Tests, Red-Green-Refactor | Backend Tests |
| TS-28 | Continuous Integration | Automated Build + Test Pipeline | Jenkinsfile, .gitlab-ci.yml |
| TS-29 | DevOps | Docker, docker-compose, "You build it, you run it" | Container-Config |
| TS-30 | Software Architecture | Hexagonale Architektur, Dokumentiert | ARCHITEKTUR.md |
| TS-31 | Requirements Management | Diese Datei (REQUIREMENTS.md) | Dokumentation |
| TS-32 | Softwareanforderungsanalyse | Formale Analyse in dieser Datei | Dokumentation |
| TS-33 | Test Automation | Cypress E2E + JUnit Unit Tests | Frontend + Backend |
| TS-34 | Web Applikationen | SPA (Angular) + REST API (Spring Boot) | Gesamtprojekt |
| TS-35 | Agile Methodologie | SAFe + Scrum + Kanban (Task-Board) | Dokumentation + Methodik |

### 2.3 Compliance und Sicherheit
| ID | Anforderung | Quelle | Nachweis |
|----|-------------|--------|----------|
| NFA-05 | FINMA 2023/1 Awareness | "Banking", Schweizer Kontext | Audit-Trail, RBAC, Logging |
| NFA-06 | Keine realen Kundendaten | Schweizer Bankgeheimnis | Nur Demo-Daten |
| NFA-07 | Session Timeout Konzept | Banking Security Standard | Auth Guard mit Timeout |
| NFA-08 | Activity Logging | FINMA Audit-Anforderung | AOP-basiertes Logging |

### 2.4 Zukunftsfähigkeit
| ID | Anforderung | Begründung | Status |
|----|-------------|------------|--------|
| NFA-09 | i18n-Vorbereitung (DE/FR/EN) | Schweizer Markt ist dreisprachig | Architektur vorbereitet, nicht implementiert |
| NFA-10 | PDF-Export für Reports | Research Reports werden als PDF verteilt | P2 Task |
| NFA-11 | Keyboard Shortcuts | Bloomberg-Nutzer erwarten das | Konzept in ARCHITEKTUR.md |
| NFA-12 | WebSocket-Ready | Echtzeit-Updates für Kursänderungen | RxJS-Architektur ist kompatibel |
| NFA-13 | Excel/CSV Export | Analysten exportieren Daten regelmäßig | P2 Task |
| NFA-14 | Framework-Extensibility (SLX) | "Mitarbeit an Kunden-spezifischen Frameworks" | Plugin-fähige Service-Architektur |

---

## 3. Abnahmekriterien (Definition of Done: Gesamtprototyp)

- [ ] Alle 35 geforderten Skills nachweisbar im Code oder in der Dokumentation
- [ ] Frontend `ng build --aot` fehlerfrei
- [ ] Backend `mvn clean package` fehlerfrei
- [ ] Mindestens 5 Cypress E2E Tests grün
- [ ] Backend Test Coverage >70%
- [ ] Docker Compose startet Frontend + Backend
- [ ] Login → Dashboard → Research → Securities Flow funktioniert
- [ ] Responsive auf Desktop, Tablet, Mobile
- [ ] Kein PrimeNG Theme sichtbar (nur Custom CSS)
- [ ] Keine Icons im gesamten Frontend
- [ ] Schweizer Wertschriften in Demo-Daten
- [ ] README beschreibt den Prototyp als "Prototype 1.0, veröffentlicht 20.02.2026"
