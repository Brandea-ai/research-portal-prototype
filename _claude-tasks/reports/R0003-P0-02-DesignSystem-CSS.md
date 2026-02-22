# R0003: P0-02 Design-System CSS Grundlage

**Datum:** 22.02.2026
**Task:** P0-02
**Status:** Abgeschlossen
**Dauer:** ~10 Min

---

## Was wurde geändert

| Datei | Änderung |
|-------|----------|
| `frontend/src/styles.css` | Komplettes Design-System: 12 Farbvariablen, 2 Font-Familien, 7 Spacing-Stufen, 3 Radii, 2 Shadows, Layout-Variablen, Transitions, Global Reset, Typografie-Klassen (h1-h3, .label, .data, .ticker, .rating), Button-System (.btn, --primary, --secondary, --ghost), Form-Elemente, Tabellen-Basis, Scrollbar-Styling, Utility-Klassen, Responsive Breakpoints (1024px, 768px) |
| `frontend/src/index.html` | `lang="de"`, Title auf "Research Portal", Google Fonts (Inter 400-700, JetBrains Mono 400+600) mit `preconnect` |

## Kompatibilitäts-Check

- [x] `ng build` → Erfolgreich (5.12 KB CSS, unter 8KB Budget)
- [x] Alle Farben exakt aus CLAUDE.md / STANDARDS.md
- [x] Alle Fonts exakt aus STANDARDS.md
- [x] Alle Spacing-Werte exakt aus STANDARDS.md
- [x] Bloomberg-Prinzip eingehalten: Accent nur für Active/BUY/Positive/CTA/Fokus
- [x] Keine Icons, keine externen CSS-Frameworks
- [x] BEM-ähnliche Namenskonvention
- [x] Kein `!important`
- [x] Custom Properties für alle Werte (keine hartcodierten Farben)
- [x] Responsive Breakpoints definiert (Desktop/Tablet/Mobile)

## Test-Anleitung

```bash
cd frontend
npx ng serve
# → http://localhost:4200
# Hintergrund muss #0A0A0A sein (fast schwarz)
# Text muss weiß sein
# Schrift muss Inter sein
```

## Rollback-Plan

```bash
git revert HEAD
```

## Nächster Schritt

P0-03: Backend Domain-Modell (Analyst, Security, ResearchReport, RatingHistory, FinancialEstimates)
