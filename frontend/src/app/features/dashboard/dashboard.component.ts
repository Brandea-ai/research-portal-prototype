import { Component, OnInit, signal, computed } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ReportService } from '../../core/services/report.service';
import { SecurityService } from '../../core/services/security.service';
import { AnalystService } from '../../core/services/analyst.service';
import { Report } from '../../core/models/report.model';
import { Security } from '../../core/models/security.model';
import { Analyst } from '../../core/models/analyst.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DatePipe, DecimalPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  reports = signal<Report[]>([]);
  securities = signal<Security[]>([]);
  analysts = signal<Analyst[]>([]);
  loading = signal(true);

  ratingChangedCount = computed(
    () => this.reports().filter(r => r.ratingChanged).length
  );

  latestReports = computed(() =>
    [...this.reports()]
      .sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime())
      .slice(0, 5)
  );

  topAnalysts = computed(() =>
    [...this.analysts()]
      .sort((a, b) => b.accuracy12m - a.accuracy12m)
      .slice(0, 3)
  );

  sectorCount = computed(() => {
    const sectors = new Set(this.securities().map(s => s.sector));
    return sectors.size;
  });

  avgAccuracy = computed(() => {
    const list = this.analysts();
    if (list.length === 0) return 0;
    const sum = list.reduce((acc, a) => acc + a.accuracy12m, 0);
    return sum / list.length;
  });

  totalCoverage = computed(() => {
    const tickers = new Set(this.analysts().flatMap(a => a.coverageUniverse));
    return tickers.size;
  });

  constructor(
    private readonly reportService: ReportService,
    private readonly securityService: SecurityService,
    private readonly analystService: AnalystService
  ) {}

  ngOnInit(): void {
    this.reportService.getAll().subscribe(data => this.reports.set(data));
    this.securityService.getAll().subscribe(data => this.securities.set(data));
    this.analystService.getAll().subscribe(data => {
      this.analysts.set(data);
      this.loading.set(false);
    });
  }

  ratingClass(rating: string): string {
    if (rating === 'STRONG_BUY' || rating === 'BUY') return 'positive';
    if (rating === 'SELL' || rating === 'STRONG_SELL') return 'negative';
    return 'neutral';
  }

  formatRating(rating: string): string {
    return rating.replace(/_/g, ' ');
  }

  starDisplay(starRating: number): string {
    const full = Math.round(starRating);
    const empty = 5 - full;
    return '\u2605'.repeat(full) + '\u2606'.repeat(empty);
  }
}
