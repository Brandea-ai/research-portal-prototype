import { Component, OnInit, OnDestroy, signal, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subject, forkJoin } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { SecurityService } from '../../core/services/security.service';
import { ReportService } from '../../core/services/report.service';
import { Security } from '../../core/models/security.model';
import { Report } from '../../core/models/report.model';

type SortColumn = 'name' | 'sector' | 'marketCap';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-securities',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './securities.component.html',
  styleUrl: './securities.component.css'
})
export class SecuritiesComponent implements OnInit, OnDestroy {

  private readonly securityService = inject(SecurityService);
  private readonly reportService = inject(ReportService);
  private readonly router = inject(Router);

  securities = signal<Security[]>([]);
  reports = signal<Report[]>([]);
  loading = signal(true);

  sortColumn = signal<SortColumn>('name');
  sortDirection = signal<SortDirection>('asc');

  filterSector = signal<string>('ALL');
  searchTerm = signal('');

  private readonly searchSubject = new Subject<string>();
  private readonly destroy$ = new Subject<void>();

  availableSectors = computed(() => {
    const sectors = new Set(this.securities().map(s => s.sector));
    return ['ALL', ...Array.from(sectors).sort()];
  });

  latestReportMap = computed(() => {
    const map = new Map<number, Report>();
    const allReports = this.reports();
    for (const report of allReports) {
      const existing = map.get(report.securityId);
      if (!existing || report.publishedAt > existing.publishedAt) {
        map.set(report.securityId, report);
      }
    }
    return map;
  });

  activeFilterCount = computed(() => {
    let count = 0;
    if (this.filterSector() !== 'ALL') count++;
    if (this.searchTerm().trim().length > 0) count++;
    return count;
  });

  filteredSecurities = computed(() => {
    let result = this.securities();

    const sectorFilter = this.filterSector();
    if (sectorFilter !== 'ALL') {
      result = result.filter(s => s.sector === sectorFilter);
    }

    const search = this.searchTerm().trim().toLowerCase();
    if (search.length > 0) {
      result = result.filter(s =>
        s.ticker.toLowerCase().includes(search) ||
        s.name.toLowerCase().includes(search)
      );
    }

    const column = this.sortColumn();
    const direction = this.sortDirection();
    const dirMultiplier = direction === 'asc' ? 1 : -1;

    result = [...result].sort((a, b) => {
      let comparison = 0;
      switch (column) {
        case 'name':
          comparison = a.name.localeCompare(b.name);
          break;
        case 'sector':
          comparison = a.sector.localeCompare(b.sector);
          break;
        case 'marketCap':
          comparison = a.marketCap - b.marketCap;
          break;
      }
      return comparison * dirMultiplier;
    });

    return result;
  });

  ngOnInit(): void {
    forkJoin({
      securities: this.securityService.getAll(),
      reports: this.reportService.getAll()
    }).subscribe(({ securities, reports }) => {
      this.securities.set(securities);
      this.reports.set(reports);
      this.loading.set(false);
    });

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(term => {
      this.searchTerm.set(term);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearchInput(value: string): void {
    this.searchSubject.next(value);
  }

  toggleSort(column: SortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(column);
      this.sortDirection.set(column === 'marketCap' ? 'desc' : 'asc');
    }
  }

  getSortIndicator(column: SortColumn): string {
    if (this.sortColumn() !== column) return '';
    return this.sortDirection() === 'asc' ? ' \u25B2' : ' \u25BC';
  }

  resetFilters(): void {
    this.filterSector.set('ALL');
    this.searchTerm.set('');
  }

  navigateToReports(sec: Security): void {
    this.router.navigate(['/reports'], { queryParams: { security: sec.ticker } });
  }

  getLatestRating(securityId: number): string | null {
    const report = this.latestReportMap().get(securityId);
    return report ? report.rating : null;
  }

  ratingClass(rating: string): string {
    if (rating === 'STRONG_BUY' || rating === 'BUY') return 'positive';
    if (rating === 'SELL' || rating === 'STRONG_SELL') return 'negative';
    return 'neutral';
  }

  formatRating(rating: string): string {
    return rating.replace(/_/g, ' ');
  }

  formatSectorLabel(value: string): string {
    if (value === 'ALL') return 'Alle';
    return value;
  }

  formatMarketCap(value: number): string {
    if (value >= 1_000_000_000_000) return (value / 1_000_000_000_000).toFixed(1) + ' Bio.';
    if (value >= 1_000_000_000) return (value / 1_000_000_000).toFixed(1) + ' Mrd.';
    if (value >= 1_000_000) return (value / 1_000_000).toFixed(1) + ' Mio.';
    return value.toFixed(0);
  }
}
