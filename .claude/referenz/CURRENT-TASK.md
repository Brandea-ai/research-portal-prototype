# Aktueller Arbeitsstand

> Wird nach jeder Arbeitseinheit aktualisiert. Referenz: `_claude-tasks/STATUS.md`

**Letzte Aktualisierung:** 22.02.2026
**Aktueller Task:** P2-16 + P2-18 abgeschlossen, P2-17 XPath Report Import (nächster)
**Status:** P0 komplett (7/7), P1 komplett (7/7), P2 in Arbeit (3/13) + Design v2.0 + Theme v3.0

---

## Was wurde zuletzt gemacht

### Session 4 (22.02.2026): P2-16 + P2-18 (Parallel)

1. P2-16: 70 Backend Unit Tests (JUnit 5 + Mockito), 0 Failures, ~75-80% Coverage
   - 3 Service Tests, 3 Controller Tests (@WebMvcTest), 3 Mapper Tests, 1 Persistence Adapter Test
2. P2-18: 4 CI/CD-Konfigurationsdateien
   - Jenkinsfile (10 Stages, K8s Agent, Quality Gate)
   - .gitlab-ci.yml (5 Stages, parallele Jobs, Caching)
   - .harness/pipeline.yaml (3 Stages, 4-Augen-Prinzip)
   - sonar-project.properties (Multi-Modul, Quality Gate)

### Session 4 (22.02.2026): Design System v3.0 — Theme Toggle

1. ThemeService: Signal-basiert mit cycle() (dark → light → system), localStorage, effect()
2. styles.css v3.0: Komplettes Dual-Palette-System (Dark + Light) via [data-theme] Attribut
3. Sidebar-Redesign: Logo-Mark ("R"), Buchstaben-Initialen, Collapsed Active State
4. Topbar: Theme-Toggle-Button (Sonne/Mond/Halbkreis)
5. Dashboard: Theme-aware Chart.js (getChartColors() + effect() für Auto-Rebuild)
6. 5 Component CSS Files: Alle hardcoded Farben durch CSS-Variablen ersetzt
7. Build: 292.83 KB, 0 Fehler, 0 Warnings

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
1. **P2-17:** XPath Report Import
2. **P2-19:** Audit Trail (Activity Log, AOP Logging)
3. **P2-20:** API Dokumentation (Springdoc, Swagger UI)
4. **P2-26:** i18n (DE/EN) Internationalisierung

## Offene Fragen / Blocker

Keine aktuell.

## Kontext für die nächste Session

- **Backend:** 12 REST Endpoints, CRUD-Endpoints existieren bereits (POST, PUT, DELETE)
- **Frontend:** 5 Feature-Seiten + Detail + CRUD-Form, Login, Responsive Layout-Shell
- **State:** ReportStateService (BehaviorSubject + auto-Refresh nach CRUD)
- **Tests:** 5 Cypress Specs, 49 E2E Tests (nicht ausgeführt, da Backend nicht läuft)
- **Build:** 292.83 KB Initial Bundle, 0 Fehler, 0 Warnings
- **Deploy:** Vercel (auto) + Fly.io (manuell)

JAVA_HOME: `export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"`
