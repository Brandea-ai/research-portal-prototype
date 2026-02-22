# R0004: P0-03 Backend Domain-Modell

**Datum:** 22.02.2026
**Task:** P0-03
**Status:** Abgeschlossen
**Dauer:** ~15 Min

---

## Was wurde geändert

### Enums (feste Wertelisten)

| Datei | Beschreibung |
|-------|-------------|
| `domain/model/Rating.java` | STRONG_BUY, BUY, HOLD, SELL, STRONG_SELL |
| `domain/model/ReportType.java` | INITIATION, UPDATE, QUARTERLY, FLASH, DEEP_DIVE, CREDIT |
| `domain/model/AssetClass.java` | EQUITY, FIXED_INCOME, DERIVATIVES, MACRO |
| `domain/model/RiskLevel.java` | LOW, MEDIUM, HIGH, SPECULATIVE |

### Entities (Geschäftsobjekte)

| Datei | Beschreibung |
|-------|-------------|
| `domain/model/Analyst.java` | Name, Title, Department, Coverage Universe, Star Rating, Accuracy |
| `domain/model/Security.java` | Ticker, ISIN, Name, AssetClass, Sector, Exchange, Currency, MarketCap |
| `domain/model/ResearchReport.java` | Analyst+Security Referenz, Rating, TargetPrice, ExecutiveSummary, Catalysts, Risks |
| `domain/model/RatingHistory.java` | Historische Rating-Änderungen (FINMA Audit Trail) |
| `domain/model/FinancialEstimates.java` | Revenue, EBITDA, EPS, P/E, EV/EBITDA, Dividendenrendite pro Geschäftsjahr |

### Ports (Schnittstellen)

| Datei | Beschreibung |
|-------|-------------|
| `domain/port/out/ReportRepository.java` | findAll, findById, findByAnalyst, findBySecurity, save, delete |
| `domain/port/out/SecurityRepository.java` | findAll, findById, findByTicker |
| `domain/port/out/AnalystRepository.java` | findAll, findById |
| `domain/port/in/GetReportsUseCase.java` | Lese-Operationen für Reports |
| `domain/port/in/ManageReportUseCase.java` | CRUD-Operationen für Reports |
| `domain/port/in/GetSecuritiesUseCase.java` | Lese-Operationen für Wertschriften |
| `domain/port/in/GetAnalystsUseCase.java` | Lese-Operationen für Analysten |

## Kompatibilitäts-Check

- [x] `mvn clean compile` → BUILD SUCCESS (18 Source Files, 0 Fehler)
- [x] KEIN `import org.springframework` in domain/ (0 Treffer)
- [x] Alle Entities aus CLAUDE.md Datenmodell abgedeckt
- [x] Alle Enums aus TODOs P0-03 Checkliste vorhanden
- [x] BigDecimal für Geldbeträge (nicht double/float)
- [x] LocalDateTime/LocalDate für Zeitwerte (nicht Date)
- [x] Ports trennen Lesen (GetXxxUseCase) von Schreiben (ManageXxxUseCase)

## Test-Anleitung

```bash
cd backend
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
mvn clean compile
# Erwartung: BUILD SUCCESS, 0 Fehler
```

## Rollback-Plan

```bash
git revert HEAD
```

## Nächster Schritt

P0-04: Backend Persistence Layer (JPA Entities, Repositories, Mapper, Flyway, Demo-Daten)
