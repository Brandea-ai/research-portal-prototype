# R0015: Design System v3.0 — Light/Dark/System Theme Toggle

**Datum:** 22.02.2026
**Session:** 4 (Fortsetzung)
**Dauer:** ~15 Min (Foundation + 2 parallele Sonnet 4.6 Agents)
**Status:** Abgeschlossen

---

## Auslöser

Armend war mit dem Design v2.0 nicht zufrieden:
- Kein helles Design vorhanden (nur Dark Mode)
- Sidebar-Collapsed-Zustand zeigt nicht, wo man sich befindet
- Kontrast immer noch zu schwach
- Gesamteindruck: "wirkt billig"

## Was wurde gemacht

### 1. ThemeService (NEU)
- `theme.service.ts` mit Signal-basiertem Theme-Management
- 3 Modi: Dark, Light, System (folgt OS-Präferenz)
- `cycle()` Methode: dark → light → system → dark
- localStorage-Persistenz (`rp_theme` Key)
- `effect()` setzt `data-theme` Attribut auf `<html>`
- `resolvedTheme` computed Signal für Chart.js-Integration

### 2. styles.css v3.0 (komplett neugeschrieben)
- **Dark Palette:** `--color-bg: #0B0D12`, `--color-accent: #4DA3FF`, `--color-positive: #2DD4A0`
- **Light Palette:** `--color-bg: #F5F6F8`, `--color-accent: #0062CC`, `--color-positive: #00875A`
- **System:** `@media (prefers-color-scheme: light)` mit `[data-theme="system"]`
- `color-scheme: dark` / `color-scheme: light` pro Theme
- Shadows: Stärker für Dark, subtiler für Light
- Neues Token: `--sidebar-collapsed: 60px`

### 3. Sidebar-Redesign
- Logo: Farbiger "R"-Square (32x32) statt Text-only
- Buchstaben-Initialen (D, R, W, A) für jeden Nav-Eintrag
- Active-State in Collapsed: Farbiger Hintergrund auf Initial-Buchstabe
- `.hidden` Klasse statt `--hidden` Modifier
- Collapsed-Breite via CSS-Variable

### 4. Topbar-Update
- Theme-Toggle-Button: Zykliert durch Dunkel/Hell/System
- Unicode-Icons: Sonne (Hell), Mond (Dunkel), Halbkreis (System)
- Tooltip zeigt aktuellen Theme-Modus

### 5. Dashboard — Theme-Aware Charts (Agent A)
- `getChartColors()` Helper: Gibt theme-abhängige Farben zurück
- `effect()` im Constructor: Rebuildet Charts bei Theme-Wechsel
- `:host`-Overrides komplett entfernt (verhinderte Theme-Wechsel)

### 6. Login — Variable-Based (Agent A)
- Radialer Gradient entfernt (war Dark-only)
- Alle Farben über CSS-Variablen

### 7. 5 Component CSS Files (Agent B)
- reports, securities, report-detail, report-form, analysts
- Alle hardcoded Fallback-Werte entfernt
- Gradient-Buttons durch solides Accent + Weiß ersetzt
- Theme-Transitions auf Cards und Containern

## Methode

1. Main Agent: ThemeService, styles.css, Sidebar, Topbar, App Shell
2. Agent A (Sonnet 4.6, Worktree): Dashboard + Login
3. Agent B (Sonnet 4.6, Worktree): 5 Component CSS Files
4. Files aus Worktrees kopiert, Build verifiziert

## Build-Ergebnis

- **292.83 KB** Initial Bundle
- **0 Fehler**, 0 Warnings
- 10 Worktrees aufgeräumt

## Geänderte Dateien

| Datei | Aktion |
|-------|--------|
| `frontend/src/app/core/services/theme.service.ts` | NEU |
| `frontend/src/styles.css` | Rewrite (v3.0) |
| `frontend/src/app/layout/sidebar/sidebar.component.html` | Rewrite |
| `frontend/src/app/layout/sidebar/sidebar.component.css` | Rewrite |
| `frontend/src/app/layout/topbar/topbar.component.ts` | Update |
| `frontend/src/app/layout/topbar/topbar.component.html` | Update |
| `frontend/src/app/layout/topbar/topbar.component.css` | Update |
| `frontend/src/app/app.ts` | Update |
| `frontend/src/app/features/dashboard/dashboard.component.ts` | Update |
| `frontend/src/app/features/dashboard/dashboard.component.css` | Update |
| `frontend/src/app/features/login/login.component.css` | Update |
| `frontend/src/app/features/reports/reports.component.css` | Update |
| `frontend/src/app/features/securities/securities.component.css` | Update |
| `frontend/src/app/features/reports/report-detail/report-detail.component.css` | Update |
| `frontend/src/app/features/reports/report-form/report-form.component.css` | Update |
| `frontend/src/app/features/analysts/analysts.component.css` | Update |
