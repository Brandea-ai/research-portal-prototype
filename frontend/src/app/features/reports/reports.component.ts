import { Component, OnInit, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { TranslatePipe } from '@ngx-translate/core';
import { ReportStateService } from '../../core/services/report-state.service';
import { AnalystService } from '../../core/services/analyst.service';
import { Report } from '../../core/models/report.model';
import { environment } from '../../../environments/environment';

type SortColumn = 'publishedAt' | 'rating' | 'targetPrice' | 'impliedUpside';
type SortDirection = 'asc' | 'desc';
type ReportTypeFilter = 'ALL' | 'INITIATION' | 'UPDATE' | 'QUARTERLY' | 'FLASH' | 'DEEP_DIVE';
type RatingFilter = 'ALL' | 'STRONG_BUY' | 'BUY' | 'HOLD' | 'SELL' | 'STRONG_SELL';

const RATING_ORDER: Record<string, number> = {
  'STRONG_BUY': 5,
  'BUY': 4,
  'HOLD': 3,
  'SELL': 2,
  'STRONG_SELL': 1
};

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [DatePipe, DecimalPipe, FormsModule, TranslatePipe],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportsComponent implements OnInit {

  private readonly router = inject(Router);

  reports = signal<Report[]>([]);
  analysts = signal<Map<number, string>>(new Map());
  loading = signal(true);

  sortColumn = signal<SortColumn>('publishedAt');
  sortDirection = signal<SortDirection>('desc');

  filterType = signal<ReportTypeFilter>('ALL');
  filterRating = signal<RatingFilter>('ALL');
  searchTerm = signal('');

  private readonly searchSubject = new Subject<string>();

  reportTypes: ReportTypeFilter[] = ['ALL', 'INITIATION', 'UPDATE', 'QUARTERLY', 'FLASH', 'DEEP_DIVE'];
  ratingOptions: RatingFilter[] = ['ALL', 'STRONG_BUY', 'BUY', 'HOLD', 'SELL', 'STRONG_SELL'];

  activeFilterCount = computed(() => {
    let count = 0;
    if (this.filterType() !== 'ALL') count++;
    if (this.filterRating() !== 'ALL') count++;
    if (this.searchTerm().trim().length > 0) count++;
    return count;
  });

  filteredReports = computed(() => {
    let result = this.reports();

    const typeFilter = this.filterType();
    if (typeFilter !== 'ALL') {
      result = result.filter(r => r.reportType === typeFilter);
    }

    const ratingFilter = this.filterRating();
    if (ratingFilter !== 'ALL') {
      result = result.filter(r => r.rating === ratingFilter);
    }

    const search = this.searchTerm().trim().toLowerCase();
    if (search.length > 0) {
      result = result.filter(r => r.title.toLowerCase().includes(search));
    }

    const column = this.sortColumn();
    const direction = this.sortDirection();
    const dirMultiplier = direction === 'asc' ? 1 : -1;

    result = [...result].sort((a, b) => {
      let comparison = 0;
      switch (column) {
        case 'publishedAt':
          comparison = a.publishedAt.localeCompare(b.publishedAt);
          break;
        case 'rating':
          comparison = (RATING_ORDER[a.rating] ?? 0) - (RATING_ORDER[b.rating] ?? 0);
          break;
        case 'targetPrice':
          comparison = a.targetPrice - b.targetPrice;
          break;
        case 'impliedUpside':
          comparison = a.impliedUpside - b.impliedUpside;
          break;
      }
      return comparison * dirMultiplier;
    });

    return result;
  });

  private readonly reportState = inject(ReportStateService);
  private readonly analystService = inject(AnalystService);

  ngOnInit(): void {
    this.reportState.reports$.subscribe(data => this.reports.set(data));
    this.reportState.loading$.subscribe(loading => {
      if (!loading && this.reports().length >= 0) this.loading.set(false);
    });
    this.reportState.loadReports();

    this.analystService.getAll().subscribe(data => {
      const map = new Map<number, string>();
      data.forEach(a => map.set(a.id, a.name));
      this.analysts.set(map);
    });

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchTerm.set(term);
    });
  }

  onSearchInput(value: string): void {
    this.searchSubject.next(value);
  }

  toggleSort(column: SortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(column);
      this.sortDirection.set('desc');
    }
  }

  getSortIndicator(column: SortColumn): string {
    if (this.sortColumn() !== column) return '';
    return this.sortDirection() === 'asc' ? ' \u25B2' : ' \u25BC';
  }

  resetFilters(): void {
    this.filterType.set('ALL');
    this.filterRating.set('ALL');
    this.searchTerm.set('');
  }

  getAnalystName(analystId: number): string {
    return this.analysts().get(analystId) ?? `Analyst #${analystId}`;
  }

  ratingClass(rating: string): string {
    if (rating === 'STRONG_BUY' || rating === 'BUY') return 'positive';
    if (rating === 'SELL' || rating === 'STRONG_SELL') return 'negative';
    return 'neutral';
  }

  formatRating(rating: string): string {
    return rating.replace(/_/g, ' ');
  }

  formatType(type: string): string {
    return type.replace(/_/g, ' ');
  }

  formatFilterLabel(value: string): string {
    if (value === 'ALL') return 'Alle';
    return value.replace(/_/g, ' ');
  }

  openReport(id: number): void {
    this.router.navigate(['/reports', id]);
  }

  createReport(): void {
    this.router.navigate(['/reports/new']);
  }

  exportCsv(): void {
    window.open(`${environment.apiUrl}/export/reports/csv`, '_blank');
  }

  exportExcel(): void {
    window.open(`${environment.apiUrl}/export/reports/excel`, '_blank');
  }
}
