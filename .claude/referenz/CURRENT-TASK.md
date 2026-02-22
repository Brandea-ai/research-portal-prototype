# Aktueller Arbeitsstand

> Wird nach jeder Arbeitseinheit aktualisiert. Referenz: `_claude-tasks/STATUS.md`

**Letzte Aktualisierung:** 22.02.2026
**Aktueller Task:** Steuerungsdateien-Synchronisation (dann P1-10)
**Status:** P0 komplett (7/7), P1 in Arbeit (2/7)

---

## Was wurde zuletzt gemacht

### Session 3 (22.02.2026): P1-08 + P1-09 (Parallel)

**P1-08 Dashboard View:**
1. KPI-Cards verbessert (font-data, 2.25rem, Untertitel, Hover)
2. latestReports als computed() mit Datum-Sortierung
3. Top Analysten (NEU): Top 3 nach accuracy12m, Rang, Sterne
4. Coverage-Übersicht (NEU): Sektoren, Ticker, Avg. Accuracy
5. 5 computed() Signals, vollständig responsive

**P1-09 Reports Tabelle:**
1. Sortierung via Unicode-Pfeile (▲/▼), 3 sortierbare Spalten
2. Filter: Typ-Dropdown, Rating-Dropdown, Debounced Suchfeld (RxJS Subject + 300ms)
3. Analyst-Spalte via AnalystService Map<id, name>
4. Rating-Change Indikator, Typ-Badges, Leerer-Zustand
5. computed() für gefilterte+sortierte Liste

**Methode:** 2 parallele Subagents in isolierten Git-Worktrees, 0 Merge-Konflikte

### Davor: P0-07 Login-Seite

1. User-Model mit 3 Rollen (ANALYST, SENIOR_ANALYST, ADMIN)
2. AuthService mit Signals, Mock-Users, sessionStorage-Persistenz
3. Functional Auth-Guard (CanActivateFn) mit Redirect zu /login
4. Login-Komponente: Reactive Form, Validation, Error-Handling
5. App Shell: Bedingtes Layout (Sidebar/Topbar nur wenn eingeloggt)
6. Topbar: Rolle-Badge + Benutzername + Abmelden-Button

## Was steht als Nächstes an

**P1 Kernfeatures (Fortsetzung):**
1. **P1-10:** Report Detail-Ansicht (Split-View, Rating, Finanzdaten)
2. **P1-11:** Securities View (Wertschriften-Tabelle, Verlinkung)
3. **P1-12:** Report CRUD (Create, Edit, Delete, Validation)
4. **P1-13:** RxJS State Management (BehaviorSubject, reaktive Filter)
5. **P1-14:** Responsive Design (Sidebar, Tabelle, Detail, Mobile)

## Offene Fragen / Blocker

Keine aktuell.

## Kontext für die nächste Session

- **Backend:** Hexagonale Architektur, 12 REST Endpoints, 25 Demo-Datensätze
- **Frontend:** 4 Feature-Seiten, Login mit Auth-Guard, Layout-Shell
- **Auth:** Mock-Auth mit 2 Usern (analyst/admin), sessionStorage
- **Deploy:** Vercel (Frontend auto) + Fly.io (Backend manuell)
- **Build:** 276 KB Initial Bundle, 0 Fehler

JAVA_HOME: `export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"`
