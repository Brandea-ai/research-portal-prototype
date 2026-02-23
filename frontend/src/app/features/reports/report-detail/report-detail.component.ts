import { Component, OnInit, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ReportService } from '../../../core/services/report.service';
import { ReportStateService } from '../../../core/services/report-state.service';
import { AnalystService } from '../../../core/services/analyst.service';
import { SecurityService } from '../../../core/services/security.service';
import { Report } from '../../../core/models/report.model';
import { Analyst } from '../../../core/models/analyst.model';
import { Security } from '../../../core/models/security.model';
import { environment } from '../../../../environments/environment';
import { NotificationService } from '../../../core/services/notification.service';
import { BreadcrumbComponent } from '../../../shared/components/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-report-detail',
  standalone: true,
  imports: [DatePipe, DecimalPipe, BreadcrumbComponent],
  templateUrl: './report-detail.component.html',
  styleUrl: './report-detail.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportDetailComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly reportService = inject(ReportService);
  private readonly reportState = inject(ReportStateService);
  private readonly analystService = inject(AnalystService);
  private readonly securityService = inject(SecurityService);
  private readonly notification = inject(NotificationService);

  report = signal<Report | null>(null);
  analyst = signal<Analyst | null>(null);
  security = signal<Security | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  showDeleteConfirm = signal(false);

  ratingColorClass = computed(() => {
    const r = this.report();
    if (!r) return '';
    if (r.rating === 'STRONG_BUY' || r.rating === 'BUY') return 'positive';
    if (r.rating === 'SELL' || r.rating === 'STRONG_SELL') return 'negative';
    return 'neutral';
  });

  previousRatingColorClass = computed(() => {
    const r = this.report();
    if (!r || !r.previousRating) return '';
    if (r.previousRating === 'STRONG_BUY' || r.previousRating === 'BUY') return 'positive';
    if (r.previousRating === 'SELL' || r.previousRating === 'STRONG_SELL') return 'negative';
    return 'neutral';
  });

  upsideColorClass = computed(() => {
    const r = this.report();
    if (!r) return '';
    return r.impliedUpside >= 0 ? 'positive' : 'negative';
  });

  formattedRating = computed(() => {
    const r = this.report();
    if (!r) return '';
    return r.rating.replace(/_/g, ' ');
  });

  formattedPreviousRating = computed(() => {
    const r = this.report();
    if (!r || !r.previousRating) return '';
    return r.previousRating.replace(/_/g, ' ');
  });

  formattedReportType = computed(() => {
    const r = this.report();
    if (!r) return '';
    return r.reportType.replace(/_/g, ' ');
  });

  formattedRiskLevel = computed(() => {
    const r = this.report();
    if (!r || !r.riskLevel) return '';
    const map: Record<string, string> = {
      'LOW': 'Tief',
      'MEDIUM': 'Mittel',
      'HIGH': 'Hoch',
      'SPECULATIVE': 'Spekulativ'
    };
    return map[r.riskLevel] ?? r.riskLevel;
  });

  riskLevelColorClass = computed(() => {
    const r = this.report();
    if (!r || !r.riskLevel) return '';
    if (r.riskLevel === 'LOW') return 'positive';
    if (r.riskLevel === 'MEDIUM') return 'neutral';
    if (r.riskLevel === 'HIGH' || r.riskLevel === 'SPECULATIVE') return 'negative';
    return '';
  });

  hasCatalysts = computed(() => {
    const r = this.report();
    return r !== null && r.investmentCatalysts.length > 0;
  });

  hasRisks = computed(() => {
    const r = this.report();
    return r !== null && r.keyRisks.length > 0;
  });

  hasTags = computed(() => {
    const r = this.report();
    return r !== null && r.tags.length > 0;
  });

  hasInvestmentThesis = computed(() => {
    return this.hasCatalysts() || this.hasRisks();
  });

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = Number(params.get('id'));
        return this.reportService.getById(id);
      })
    ).subscribe({
      next: (report) => {
        this.report.set(report);
        this.loadRelatedData(report);
      },
      error: () => {
        this.error.set('Report konnte nicht geladen werden.');
        this.loading.set(false);
      }
    });
  }

  private loadRelatedData(report: Report): void {
    forkJoin({
      analyst: this.analystService.getById(report.analystId),
      security: this.securityService.getById(report.securityId)
    }).subscribe({
      next: ({ analyst, security }) => {
        this.analyst.set(analyst);
        this.security.set(security);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  editReport(): void {
    const r = this.report();
    if (r) this.router.navigate(['/reports', r.id, 'edit']);
  }

  confirmDelete(): void {
    this.showDeleteConfirm.set(true);
  }

  cancelDelete(): void {
    this.showDeleteConfirm.set(false);
  }

  exportPdf(): void {
    const r = this.report();
    if (r) window.open(`${environment.apiUrl}/export/reports/${r.id}/pdf`, '_blank');
  }

  deleteReport(): void {
    const r = this.report();
    if (!r) return;
    this.reportState.deleteReport(r.id).subscribe({
      next: () => {
        this.notification.success('Report wurde gelöscht.');
        this.router.navigate(['/reports']);
      },
      error: () => {
        this.error.set('Report konnte nicht gelöscht werden.');
        this.notification.error('Report konnte nicht gelöscht werden.');
      }
    });
  }
}
