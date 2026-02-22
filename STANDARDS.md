# Standards: Research Portal Prototype 1.0

> Design-System, Coding-Standards und Qualitätsrichtlinien.

---

## Design-System

### Farben

| Rolle | Wert | Verwendung |
|-------|------|------------|
| Background | `#0A0A0A` | Haupthintergrund |
| Surface | `#111111` | Cards, Panels, Sidebar |
| Surface Alt | `#161616` | Hover, alternating rows |
| Border | `#1E1E1E` | Subtile Trennlinien |
| Border Light | `#2A2A2A` | Stärkere Trennlinien |
| Text Primary | `#FFFFFF` | Headlines, Werte |
| Text Muted | `#6B7280` | Labels, Datum, Sekundär |
| Text Dim | `#4B5563` | Placeholder, Tertiär |
| Accent | `#38BDF8` | Active, BUY, CTAs |
| Accent Hover | `#0EA5E9` | Hover auf Accent |
| Negative | `#F87171` | SELL, negative Deltas |
| Neutral | `#94A3B8` | HOLD, neutral |

### Accent-Sparprinzip
Accent `#38BDF8` AUSSCHLIESSLICH für:
- Aktiver Navigationspunkt
- BUY / STRONG_BUY Rating-Text
- Positive Kursveränderungen (+14.2%)
- Primärer CTA-Button
- Input Fokus-Ring

### Typografie

| Element | Font | Gewicht | Größe | Extras |
|---------|------|---------|-------|--------|
| H1 (Seitentitel) | Inter | 600 | 1.5rem | - |
| H2 (Section) | Inter | 600 | 1.125rem | - |
| Body | Inter | 400 | 0.875rem | - |
| Label/Caption | Inter | 500 | 0.625rem | UPPERCASE, letter-spacing: 0.15em |
| Daten/Zahlen | JetBrains Mono | 400 | 0.875rem | tabular-nums |
| Ticker | JetBrains Mono | 600 | 0.875rem | letter-spacing: 0.05em |
| Rating | Inter | 700 | 0.75rem | Farbcodiert |

### Spacing Scale

```css
--space-xs:  4px;
--space-sm:  8px;
--space-md:  16px;
--space-lg:  24px;
--space-xl:  32px;
--space-2xl: 48px;
--space-3xl: 64px;
```

### Border Radius

```css
--radius-sm: 4px;   /* Buttons, Inputs */
--radius-md: 8px;   /* Cards */
--radius-lg: 12px;  /* Modals, Panels */
```

### Shadows (minimal, dunkel)

```css
--shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.3);
--shadow-md: 0 4px 12px rgba(0, 0, 0, 0.4);
```

---

## Coding-Standards

### TypeScript (Frontend)

```typescript
// RICHTIG: Strict Typing
interface ReportFilter {
  assetClass: AssetClass | null;
  rating: Rating | null;
  searchTerm: string;
}

// FALSCH: any
const data: any = response;
```

- `strict: true` in tsconfig.json
- Kein `any` Typ
- Interfaces statt Klassen für Datenmodelle
- Enums für feste Wertelisten
- `readonly` wo möglich

### Angular

```typescript
// RICHTIG: Standalone Component
@Component({
  selector: 'app-report-table',
  standalone: true,
  imports: [CommonModule, TableModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './report-table.component.html',
  styleUrl: './report-table.component.css'
})
export class ReportTableComponent {
  private reportService = inject(ReportService);
  reports = this.reportService.reports$;
}

// FALSCH: NgModule, Constructor Injection
```

### Java (Backend)

```java
// RICHTIG: Constructor Injection, Domain hat keine Spring-Imports
public class PublishReportService implements PublishReportUseCase {
    private final ReportRepository reportRepository;

    public PublishReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
}

// FALSCH: @Autowired auf Feld, Business-Logik im Controller
```

### CSS

```css
/* RICHTIG: Custom Properties, BEM-ähnlich */
.report-table__header {
  font-family: var(--font-ui);
  font-size: 0.625rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--color-text-muted);
  border-bottom: 1px solid var(--color-border);
}

/* FALSCH: Hartcodierte Farben, !important */
.header {
  color: #6B7280 !important;
}
```

- CSS Custom Properties für alle Werte
- BEM-ähnliche Namenskonvention
- Kein `!important` (außer PrimeNG Override)
- Kein Tailwind, Bootstrap, Material Theme

---

## Commit-Standards

Format: `[typ]: Beschreibung auf Deutsch`

Beispiele:
```
feat: Dashboard KPI-Cards implementiert
fix: Report-Filter gibt korrekte Ergebnisse zurück
style: Tabellen-Header Spacing korrigiert
test: E2E Test für Login-Flow hinzugefügt
docs: Architekturdiagramm aktualisiert
config: Docker Compose für lokale Entwicklung
refactor: ReportService auf Signals umgestellt
```

---

## Qualitäts-Metriken (Ziel)

| Metrik | Ziel | Messung |
|--------|------|---------|
| TypeScript Errors | 0 | `ng build --aot` |
| Java Compile Errors | 0 | `mvn compile` |
| Backend Test Coverage | >70% | `mvn test jacoco:report` |
| Frontend Bundle Size | <500KB (initial) | `ng build --stats-json` |
| Cypress Tests | >5 grün | `npx cypress run` |
| Lighthouse Performance | >80 | Chrome DevTools |
| Accessibility | >90 | Chrome DevTools |
