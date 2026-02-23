import { Component, OnInit, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SecurityService } from '../../../core/services/security.service';
import { ReportService } from '../../../core/services/report.service';
import { Security } from '../../../core/models/security.model';
import { Report } from '../../../core/models/report.model';

@Component({
  selector: 'app-security-detail',
  standalone: true,
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './security-detail.component.html',
  styleUrl: './security-detail.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SecurityDetailComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly securityService = inject(SecurityService);
  private readonly reportService = inject(ReportService);

  security = signal<Security | null>(null);
  reports = signal<Report[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  sortedReports = computed(() => {
    return [...this.reports()].sort((a, b) =>
      b.publishedAt.localeCompare(a.publishedAt)
    );
  });

  formattedMarketCap = computed(() => {
    const sec = this.security();
    if (!sec) return '';
    const value = sec.marketCap;
    const currency = sec.currency || 'CHF';
    if (value >= 1_000_000_000_000) return `${currency} ${(value / 1_000_000_000_000).toFixed(1)} Bio.`;
    if (value >= 1_000_000_000) return `${currency} ${(value / 1_000_000_000).toFixed(1)} Mrd.`;
    if (value >= 1_000_000) return `${currency} ${(value / 1_000_000).toFixed(1)} Mio.`;
    return `${currency} ${value.toFixed(0)}`;
  });

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = Number(params.get('id'));
        return forkJoin({
          security: this.securityService.getById(id),
          reports: this.reportService.getBySecurity(id),
        });
      })
    ).subscribe({
      next: ({ security, reports }) => {
        this.security.set(security);
        this.reports.set(reports);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Wertschrift konnte nicht geladen werden.');
        this.loading.set(false);
      }
    });
  }

  openReport(id: number): void {
    this.router.navigate(['/reports', id]);
  }

  goBack(): void {
    this.router.navigate(['/securities']);
  }

  ratingColorClass(rating: string): string {
    if (rating === 'STRONG_BUY' || rating === 'BUY') return 'text--positive';
    if (rating === 'SELL' || rating === 'STRONG_SELL') return 'text--negative';
    return 'text--neutral';
  }

  formattedRating(rating: string): string {
    return rating.replace(/_/g, ' ');
  }

  formatUpside(upside: number): string {
    const sign = upside >= 0 ? '+' : '';
    return `${sign}${upside.toFixed(1)}%`;
  }

  upsideClass(upside: number): string {
    if (upside > 0) return 'text--positive';
    if (upside < 0) return 'text--negative';
    return 'text--neutral';
  }
}
