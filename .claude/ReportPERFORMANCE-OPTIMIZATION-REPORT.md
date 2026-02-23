# Report: Performance-Optimierung (Cold Start + Dashboard Load)

**Datum:** 2026-02-23
**Typ:** Performance Fix
**Status:** Abgeschlossen

---

## Problem
Beim ersten Aufruf der App dauerte es 10-20 Sekunden bis Daten sichtbar waren. Grund: Fly.io-Maschine schlief komplett ein (Cold Start = JVM + Spring Boot hochfahren).

## Änderungen

### 1. fly.toml — Maschine bleibt aktiv
| Einstellung | Vorher | Nachher | Effekt |
|---|---|---|---|
| `auto_stop_machines` | `stop` | `suspend` | Maschine wird suspendiert statt gestoppt (Warmstart ~1s statt 15s) |
| `min_machines_running` | `0` | `1` | Mindestens 1 Maschine läuft immer |
| `memory_mb` | `1024` | `512` | Weniger RAM = günstiger bei always-on |
| Health Check | keiner | `/api/actuator/health` alle 30s | Fly.io prüft Erreichbarkeit |

### 2. Dockerfile — JVM-Tuning
| Flag | Zweck |
|---|---|
| `-Xms256m -Xmx384m` | Fester Heap, kein Resizing-Overhead |
| `-XX:+UseG1GC` | Garbage Collector für kurze Pausen |
| `-XX:MaxGCPauseMillis=100` | Max 100ms GC-Pausen |
| `-XX:+UseStringDeduplication` | Weniger RAM für duplizierte Strings |
| `-Dspring.profiles.active=local` | Profil explizit setzen |

### 3. Dashboard — Parallele API-Calls
- **Vorher:** 4 einzelne `subscribe()` Calls, loading endet nach dem letzten
- **Nachher:** `forkJoin` für securities + analysts + audit, alle parallel, loading endet wenn alle fertig

## Kompatibilitäts-Check
- [x] `ng build` fehlerfrei
- [x] `mvn test`: 317 Tests, 0 Failures
- [x] Dashboard-Logik unverändert (nur Lade-Reihenfolge optimiert)
- [x] Alle Computed Signals funktionieren wie zuvor

## Erwartete Verbesserung
- **Cold Start:** ~15s → ~1s (suspend statt stop) oder 0s (min_machines=1)
- **Dashboard Load:** ~3 sequentielle Calls → 1 paralleler forkJoin-Block

## Rollback-Plan
1. fly.toml: `min_machines_running = 0`, `auto_stop_machines = 'stop'`
2. Dockerfile: `ENTRYPOINT ["java", "-jar", "app.jar"]`
3. dashboard.component.ts: Revert forkJoin zu einzelnen subscribes
