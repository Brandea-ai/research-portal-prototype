# Aktueller Arbeitsstand

> Wird nach jeder Arbeitseinheit aktualisiert. Referenz: `_claude-tasks/STATUS.md`

**Letzte Aktualisierung:** 22.02.2026
**Aktueller Task:** Design Overhaul v2.0 abgeschlossen, P2-16 Backend Unit Tests (nächster)
**Status:** P0 komplett (7/7), P1 komplett (7/7), P2 in Arbeit (1/13) + Design Overhaul

---

## Was wurde zuletzt gemacht

### Session 4 (22.02.2026): Design Overhaul v2.0

1. styles.css komplett neugeschrieben (Design System v2.0: Warm Dark Palette, Shadow-System, Animationen)
2. index.html: Inter 300-800 + JetBrains Mono 400-700 Weights
3. Chart.js installiert, Dashboard mit Doughnut + Bar Chart
4. 9 Component-CSS-Dateien neugeschrieben (Swiss Institutional Banking Aesthetic)
5. angular.json Budget erhöht auf 20kB/32kB
6. Build: 285.77 KB, 0 Fehler, 0 Warnings

### Session 4 (22.02.2026): P1-14 + P2-15 (Parallel)

**P1-14 Responsive Design:**
1. Sidebar: collapsed Signal + mobileOpen Signal, MediaQueryList Listener
2. Toggle-Button (Unicode ◀/▶) für Tablet, Hamburger (☰) in Topbar für Mobile
3. Mobile: Sidebar per translateX(-100%) versteckt, Overlay bei mobileOpen
4. Topbar: menuToggle output() an App Shell, Hamburger nur auf Mobile sichtbar
5. App Shell: viewChild für Sidebar-Referenz, sidebarMargin computed Signal
6. Reports-Tabelle: Analyst/Kurs/Upside Spalten auf Mobile ausgeblendet

**P2-15 Cypress E2E Tests:**
1. Cypress 15.10 installiert + konfiguriert (cypress.config.ts)
2. Custom cy.login() Command (formControlName-Selektoren)
3. 5 Test-Specs: auth (6), dashboard (8), reports (13), navigation (8), securities (12) = 49 Tests
4. npm Scripts: cy:open, cy:run, e2e

**Methode:** 2 parallele Subagents in isolierten Git-Worktrees, 0 Konflikte

## Was steht als Nächstes an

**Optionen (Armend entscheidet):**
1. **i18n (DE/EN)** — Internationalisierung als nächstes
2. **P2-16:** Backend Unit Tests (>70% Coverage)
3. **P2-17:** XPath Report Import
4. **P2-18:** CI/CD Pipeline (Jenkins, GitLab, Harness, Nexus)

## Offene Fragen / Blocker

Keine aktuell.

## Kontext für die nächste Session

- **Backend:** 12 REST Endpoints, CRUD-Endpoints existieren bereits (POST, PUT, DELETE)
- **Frontend:** 5 Feature-Seiten + Detail + CRUD-Form, Login, Responsive Layout-Shell
- **State:** ReportStateService (BehaviorSubject + auto-Refresh nach CRUD)
- **Tests:** 5 Cypress Specs, 49 E2E Tests (nicht ausgeführt, da Backend nicht läuft)
- **Build:** 282.66 KB Initial Bundle, 0 Fehler, 0 Warnings
- **Deploy:** Vercel (auto) + Fly.io (manuell)

JAVA_HOME: `export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"`
