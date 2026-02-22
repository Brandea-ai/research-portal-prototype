# R0005: P0-04 Backend Persistence Layer

**Datum:** 22.02.2026
**Phase:** P0 Fundament
**Task:** P0-04 Backend Persistence Layer
**Status:** Abgeschlossen
**Abhängig von:** P0-03 Domain-Modell

---

## Zusammenfassung

Die komplette Persistence-Schicht wurde implementiert: 3 JPA Entities, 3 Spring Data Repositories, 3 Mapper und 3 Persistence Adapters. Dazu Demo-Daten mit 5 Analysten, 10 Schweizer Wertschriften und 10 Research Reports.

## Erstellte Dateien (13)

### JPA Entities (Adapter-Schicht)
| Datei | Zweck |
|-------|-------|
| `adapter/out/persistence/entity/AnalystEntity.java` | JPA Entity für Analysten-Tabelle |
| `adapter/out/persistence/entity/SecurityEntity.java` | JPA Entity für Wertschriften-Tabelle |
| `adapter/out/persistence/entity/ResearchReportEntity.java` | JPA Entity für Research Reports |

### Spring Data Repositories
| Datei | Zweck |
|-------|-------|
| `adapter/out/persistence/repository/JpaAnalystRepository.java` | CRUD + findAll |
| `adapter/out/persistence/repository/JpaSecurityRepository.java` | CRUD + findByTicker |
| `adapter/out/persistence/repository/JpaReportRepository.java` | CRUD + findByAnalystId, findBySecurityId |

### Mapper (Entity ↔ Domain)
| Datei | Zweck |
|-------|-------|
| `adapter/out/persistence/mapper/AnalystPersistenceMapper.java` | coverageUniverse: comma-separated String ↔ List |
| `adapter/out/persistence/mapper/SecurityPersistenceMapper.java` | AssetClass enum ↔ String |
| `adapter/out/persistence/mapper/ReportPersistenceMapper.java` | Alle Enums + pipe-delimited Lists |

### Persistence Adapters (implementieren Domain-Ports)
| Datei | Zweck |
|-------|-------|
| `adapter/out/persistence/adapter/AnalystPersistenceAdapter.java` | Implementiert AnalystRepository (read-only) |
| `adapter/out/persistence/adapter/SecurityPersistenceAdapter.java` | Implementiert SecurityRepository (read-only) |
| `adapter/out/persistence/adapter/ReportPersistenceAdapter.java` | Implementiert ReportRepository (full CRUD) |

### Demo-Daten
| Datei | Zweck |
|-------|-------|
| `src/main/resources/data.sql` | 5 Analysten, 10 Securities, 10 Reports |

## Architektur-Entscheidungen

1. **Enums als Strings in DB:** JPA Entities speichern Enums als `String` (nicht `@Enumerated`), damit die Entity-Schicht unabhängig von Domain-Enums bleibt
2. **Lists als delimited Strings:** `investmentCatalysts`, `keyRisks`, `tags` als pipe-delimited Strings in einer Spalte (kein Join-Table für den Prototyp)
3. **coverageUniverse als comma-separated:** Analysten-Ticker-Liste als Komma-getrennt
4. **Read-only für Analyst/Security:** Nur ReportRepository hat `save()` und `deleteById()`, weil Analysten und Securities aus Fremdsystemen kommen würden
5. **`defer-datasource-initialization: true`:** Spring Boot führt `data.sql` erst NACH Hibernate Schema-Erstellung aus

## Demo-Daten Übersicht

### Analysten (5)
| ID | Name | Abteilung | Star-Rating |
|----|------|-----------|-------------|
| 1 | Dr. Lukas Meier | Equity Research | 5 |
| 2 | Sarah Brunner | Financial Research | 4 |
| 3 | Marc Keller | Sector Research | 4 |
| 4 | Dr. Anna Widmer | Fixed Income | 5 |
| 5 | Thomas Gerber | Equity Research | 3 |

### Wertschriften (10, alle SIX Swiss Exchange, CHF)
NESN, NOVN, ROG, UBSG, ZURN, SREN, ABBN, LONN, GIVN, SIKA

### Reports (10)
- 5 verschiedene Report-Typen: INITIATION, UPDATE, QUARTERLY, FLASH, DEEP_DIVE
- 4 verschiedene Ratings: STRONG_BUY (2), BUY (4), HOLD (4)
- 3 Upgrades (ratingChanged = true): UBS HOLD→BUY, Swiss Re HOLD→BUY, Roche SELL→HOLD, Lonza BUY→STRONG_BUY
- Zeitraum: 15.01.2026 bis 20.02.2026

## Qualitäts-Check

- [x] `mvn clean compile` erfolgreich
- [x] `mvn clean package -DskipTests` erfolgreich
- [x] Spring Boot startet fehlerfrei mit Profil `local`
- [x] Hibernate erstellt alle 3 Tabellen korrekt
- [x] `data.sql` wird ohne Fehler geladen
- [x] Keine Spring-Annotationen in Domain-Schicht
- [x] Mapper-Tests: Enum-Konvertierung, null-Safety, List-Splitting

## Skills demonstriert

| Skill | Nachweis |
|-------|----------|
| Hibernate/JPA | 3 JPA Entities mit @Entity, @Table, @Column, @GeneratedValue |
| SQL | data.sql mit 25 INSERT-Statements, realistische Daten |
| DDD | Strikte Trennung Domain ↔ Persistence, Mapper als Brücke |
| Repository Pattern | Domain-Ports + JPA-Adapter-Implementierung |
| Spring Data | JpaRepository-Extension mit Custom Query Methods |
