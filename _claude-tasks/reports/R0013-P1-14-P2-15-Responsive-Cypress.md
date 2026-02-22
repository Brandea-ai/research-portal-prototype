# R0013: P1-14 Responsive Design + P2-15 Cypress E2E Tests

**Datum:** 22.02.2026
**Tasks:** P1-14, P2-15
**Status:** ERLEDIGT
**Methode:** Parallele Subagents in isolierten Git-Worktrees

---

## Zusammenfassung

Zwei Features parallel entwickelt:
- **P1-14:** Responsive Design mit collapsible Sidebar und Hamburger-Menu
- **P2-15:** Cypress E2E Tests (5 Specs, 49 Tests)

## P1-14: Responsive Design

### Geänderte Dateien
- `layout/sidebar/sidebar.component.ts` — collapsed/mobileOpen Signals, MediaQueryList Listener, DestroyRef Cleanup
- `layout/sidebar/sidebar.component.html` — Collapsed-Bindings, Toggle-Button, Mobile Overlay
- `layout/sidebar/sidebar.component.css` — Collapsed-State (56px), Mobile translateX, Overlay
- `layout/topbar/topbar.component.ts` — menuToggle output()
- `layout/topbar/topbar.component.html` — Hamburger-Button (☰), topbar__left Wrapper
- `layout/topbar/topbar.component.css` — Menu-Button, Mobile hide role/user
- `app.ts` — viewChild für Sidebar, sidebarMargin computed, onMenuToggle()
- `app.html` — #sidebar Referenz, dynamic margin-left, menuToggle Binding
- `app.css` — Mobile margin-left: 0
- `features/reports/reports.component.html` — .hide-mobile auf 3 Spalten
- `features/reports/reports.component.css` — .hide-mobile Regel

### Breakpoints
- Desktop (>1024px): Sidebar 220px, alle Spalten sichtbar
- Tablet (768-1024px): Sidebar 56px (collapsed), Toggle-Button
- Mobile (<768px): Sidebar hidden, Hamburger in Topbar, Overlay-Menu

## P2-15: Cypress E2E Tests

### Neue Dateien
- `cypress.config.ts` — Konfiguration (baseUrl, viewport, no video)
- `cypress/support/e2e.ts` — Support-Entry
- `cypress/support/commands.ts` — Custom cy.login() Command
- `cypress/tsconfig.json` — TypeScript für Cypress
- `cypress/e2e/auth.cy.ts` — 6 Tests (Login, Logout, Redirect, Validation)
- `cypress/e2e/dashboard.cy.ts` — 8 Tests (KPI, Reports, Analysten, Coverage)
- `cypress/e2e/reports.cy.ts` — 13 Tests (Tabelle, Filter, Suche, Detail, CRUD)
- `cypress/e2e/navigation.cy.ts` — 8 Tests (Views, Active State, Topbar)
- `cypress/e2e/securities.cy.ts` — 12 Tests (Tabelle, Filter, Suche, Sort)

### Geänderte Dateien
- `package.json` — Scripts: cy:open, cy:run, e2e
- `.gitignore` — cypress/videos/, screenshots/, downloads/

### Nutzung
```bash
npm run cy:open   # Cypress GUI
npm run cy:run    # Headless
npm run e2e       # Alias
```

## Build-Ergebnis

- Initial Bundle: 282.66 KB (+6 KB durch Sidebar-Logik)
- Errors: 0
- Warnings: 0

## Kompatibilitäts-Check

- Sidebar-Logik nutzt Signal-based viewChild (Angular 17+)
- output() statt EventEmitter (Angular 17+)
- MediaQueryList.addEventListener (keine deprecated addListener)
- Cypress 15.10 kompatibel mit Angular 21
- Keine Änderungen an Backend, styles.css, angular.json

## Rollback-Plan

```bash
git revert HEAD   # Commit rückgängig machen
```

## Nächster Schritt

P2-16 Backend Unit Tests + P2-17 XPath Report Import
