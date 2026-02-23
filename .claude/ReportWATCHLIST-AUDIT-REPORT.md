# Report: Watchlist-Seite + Audit Trail Viewer

**Datum:** 23.02.2026
**Methode:** 2 parallele Sonnet 4.6 Agents in isolierten Git-Worktrees
**Ziel:** Backend-APIs mit Frontend verbinden (Runde 1/3)

---

## Watchlist-Seite (Agent A)

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `frontend/src/app/core/services/watchlist.service.ts` | WatchlistEntry/AddToWatchlistRequest Interfaces, 6 HTTP-Methoden, X-User-Id Header |
| `frontend/src/app/features/watchlist/watchlist.component.ts` | OnPush, Signals (entries, loading, showAddForm, editingEntryId, securities, showDeleteConfirm), computed availableSecurities |
| `frontend/src/app/features/watchlist/watchlist.component.html` | Karten-Grid, Inline-Edit Notizen, Add-Overlay, Delete-Confirm, Empty State |
| `frontend/src/app/features/watchlist/watchlist.component.css` | slideUp, 2-Spalten Grid, Backdrop-Blur Overlay, responsive Breakpoints |

### Features
- Karten-Grid mit Ticker (klickbar → Security Detail), Name, Notizen, Alert-Toggle
- "Wertschrift hinzufügen" Overlay mit Security-Dropdown (filtert bereits beobachtete aus)
- Inline-Notizen-Edit (Klick → Input, Enter/Blur speichert)
- Lösch-Bestätigung pro Karte
- Relative Zeitanzeige (vor X Stunden/Tagen)
- Sortierung: Neueste zuerst

---

## Audit Trail Viewer (Agent B)

### Neue Dateien

| Datei | Beschreibung |
|-------|-------------|
| `frontend/src/app/features/audit/audit.component.ts` | OnPush, Signals (logs, filterAction, filterEntityType, limit), computed filteredLogs, effect() für Auto-Reload |
| `frontend/src/app/features/audit/audit.component.html` | Filter-Bar (3 Dropdowns), 7-Spalten Tabelle, farbcodierte Action-Badges, klickbare Entity-IDs |
| `frontend/src/app/features/audit/audit.component.css` | slideUp, Tabellen-Styling, 6 Badge-Varianten, responsive overflow-x |

### Modifizierte Dateien

| Datei | Änderung |
|-------|----------|
| `frontend/src/app/core/services/audit.service.ts` | 4 neue Methoden (getAllLogs, getLogsByEntity, getLogsByEntityType, getReportAuditTrail), bestehende getRecentLogs unverändert |

### Features
- FINMA-konforme Audit-Tabelle mit 7 Spalten
- Farbcodierte Action-Badges: CREATE=grün, UPDATE=blau, DELETE=rot, VIEW=muted, EXPORT=gelb, IMPORT=blau
- Filter: Aktion, Entitätstyp, Limit (25/50/100)
- Klickbare Entity-IDs → Navigation zu Detail-Seiten
- IP-Adressen in Monospace
- Timestamps in tabular-nums

---

## Gemeinsame Änderungen (manuell gemerged)

| Datei | Änderung |
|-------|----------|
| `frontend/src/app/app.routes.ts` | 2 neue Routes: `/watchlist` + `/audit` (vor Error-Routes) |
| `frontend/src/app/layout/sidebar/sidebar.component.html` | 2 neue Links: ★ Watchlist + P Protokoll |
| `frontend/src/app/core/services/index.ts` | WatchlistService Export |
| `frontend/public/assets/i18n/de.json` | NAV.WATCHLIST, NAV.AUDIT, 12 WATCHLIST.*, 20 AUDIT.* Keys |
| `frontend/public/assets/i18n/en.json` | Gleiche Keys auf Englisch |
| `frontend/public/assets/i18n/fr.json` | Gleiche Keys auf Französisch |

---

## Kompatibilitäts-Check

- [x] `ng build` fehlerfrei
- [x] `mvn test` 317 Tests, 0 Failures (Backend unverändert)
- [x] Dashboard Activity Feed (AuditService.getRecentLogs) unverändert
- [x] Bestehende Navigation (D, R, W, A, S) vollständig erhalten
- [x] Sidebar-Initials eindeutig: D, R, W, A, ★, S, P
- [x] i18n-Keys manuell gemerged (kein Overlap)

## Test-Anleitung

1. Sidebar: ★ Watchlist klicken → 3 Demo-Einträge (NESN, ROG, UBSG) sehen
2. "Wertschrift hinzufügen" → Overlay mit Dropdown, neue Wertschrift wählen
3. Notizen-Feld klicken → Inline-Edit, Enter/Blur speichert
4. "Entfernen" → Bestätigungsdialog
5. Sidebar: P Protokoll klicken → Audit-Tabelle mit Filtern
6. Entity-ID in Audit klicken → Navigation zur Detail-Seite
7. Filter (Aktion/Typ/Limit) testen

## Rollback-Plan

- Watchlist: watchlist.service.ts + watchlist/ Ordner löschen
- Audit: audit/ Ordner löschen, audit.service.ts auf alte Version revertieren
- Gemeinsam: Routes, Sidebar, i18n Keys revertieren
