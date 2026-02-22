# R0008: P0-07 Login-Seite

**Datum:** 22.02.2026
**Phase:** P0 Fundament
**Status:** Abgeschlossen

---

## Was wurde geändert

### Neue Dateien (8)

| Datei | Beschreibung |
|-------|-------------|
| `core/models/user.model.ts` | User-Interface mit Rollen (ANALYST, SENIOR_ANALYST, ADMIN) |
| `core/services/auth.service.ts` | Mock-Auth mit Signals, sessionStorage, login/logout |
| `core/guards/auth.guard.ts` | Functional CanActivateFn Guard, Redirect zu /login |
| `features/login/login.component.ts` | Reactive Form mit Validation, inject() Pattern |
| `features/login/login.component.html` | Login-Formular mit Branding, Error-Anzeige, Demo-Hint |
| `features/login/login.component.css` | Zentriertes Card-Layout, Accent nur auf Submit-Button |

### Geänderte Dateien (6)

| Datei | Änderung |
|-------|----------|
| `app.routes.ts` | Login-Route hinzugefügt, authGuard auf alle geschützten Routen |
| `app.ts` | AuthService injected, öffentlich für Template |
| `app.html` | Bedingtes Layout: Sidebar+Topbar nur wenn eingeloggt |
| `topbar.component.ts` | AuthService für Benutzername + Logout |
| `topbar.component.html` | Rolle-Badge, Benutzername, Abmelden-Button |
| `topbar.component.css` | Styles für Rolle-Badge und Logout-Button |
| `core/models/index.ts` | User-Export hinzugefügt |

## Architektur-Entscheidungen

- **sessionStorage statt localStorage:** Session endet beim Tab-Schließen (Banking-üblich)
- **Mock-Users als Record:** Einfach erweiterbar, kein Backend-Endpoint nötig für Prototyp
- **Functional Guard:** Modernes Angular-Pattern statt Class-based Guard
- **inject() durchgehend:** Konsistent mit App-Komponente, vermeidet Constructor-Timing-Probleme

## Kompatibilitäts-Check

- [x] Alle bestehenden Routes funktionieren weiterhin (mit Auth-Guard)
- [x] Sidebar/Topbar unverändert wenn eingeloggt
- [x] Dashboard, Reports, Securities, Analysts laden wie bisher
- [x] Build fehlerfrei (271 KB, 0 Errors)
- [x] Logout leert Session und redirected zu /login

## Test-Anleitung

1. Öffne https://research-portal-prototype.vercel.app
2. Du wirst automatisch zu /login redirected
3. Login mit `analyst` / `analyst` → Dashboard mit "Dr. Lukas Meier" in Topbar
4. Oder mit `admin` / `admin` → Dashboard mit "Sarah Brunner" + ADMIN Badge
5. Navigiere zwischen Seiten → alles funktioniert
6. Klicke "Abmelden" → zurück zu /login
7. Versuche direkt /dashboard aufzurufen → Redirect zu /login

## Rollback-Plan

```bash
git revert HEAD  # Letzten Commit rückgängig machen
git push origin main
```

Alternativ: Nur die auth.guard.ts löschen und in app.routes.ts die `canActivate` entfernen.
