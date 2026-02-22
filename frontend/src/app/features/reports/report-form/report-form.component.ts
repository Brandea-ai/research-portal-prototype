import { Component, OnInit, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ReportService } from '../../../core/services/report.service';
import { ReportStateService } from '../../../core/services/report-state.service';
import { AnalystService } from '../../../core/services/analyst.service';
import { SecurityService } from '../../../core/services/security.service';
import { Analyst } from '../../../core/models/analyst.model';
import { Security } from '../../../core/models/security.model';
import { Report, CreateReportRequest } from '../../../core/models/report.model';

@Component({
  selector: 'app-report-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './report-form.component.html',
  styleUrl: './report-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportFormComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly reportService = inject(ReportService);
  private readonly reportState = inject(ReportStateService);
  private readonly analystService = inject(AnalystService);
  private readonly securityService = inject(SecurityService);

  isEditMode = signal(false);
  loading = signal(true);
  submitting = signal(false);
  error = signal<string | null>(null);
  reportId = signal<number | null>(null);

  analysts = signal<Analyst[]>([]);
  securities = signal<Security[]>([]);

  reportTypes = ['INITIATION', 'UPDATE', 'QUARTERLY', 'FLASH', 'DEEP_DIVE'];
  ratings = ['STRONG_BUY', 'BUY', 'HOLD', 'SELL', 'STRONG_SELL'];
  riskLevels = ['LOW', 'MEDIUM', 'HIGH', 'SPECULATIVE'];

  reportTypeLabels: Record<string, string> = {
    'INITIATION': 'Initiation',
    'UPDATE': 'Update',
    'QUARTERLY': 'Quarterly',
    'FLASH': 'Flash',
    'DEEP_DIVE': 'Deep Dive'
  };

  ratingLabels: Record<string, string> = {
    'STRONG_BUY': 'Strong Buy',
    'BUY': 'Buy',
    'HOLD': 'Hold',
    'SELL': 'Sell',
    'STRONG_SELL': 'Strong Sell'
  };

  riskLevelLabels: Record<string, string> = {
    'LOW': 'Tief',
    'MEDIUM': 'Mittel',
    'HIGH': 'Hoch',
    'SPECULATIVE': 'Spekulativ'
  };

  pageTitle = computed(() => this.isEditMode() ? 'Report bearbeiten' : 'Neuer Report');

  form = this.fb.group({
    analystId: [0, [Validators.required, Validators.min(1)]],
    securityId: [0, [Validators.required, Validators.min(1)]],
    reportType: ['', Validators.required],
    title: ['', [Validators.required, Validators.minLength(5)]],
    executiveSummary: ['', [Validators.required, Validators.minLength(20)]],
    rating: ['', Validators.required],
    targetPrice: [0, [Validators.required, Validators.min(0.01)]],
    currentPrice: [0],
    riskLevel: ['MEDIUM']
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode.set(true);
      this.reportId.set(Number(idParam));
    }

    this.loadDropdownData();
  }

  private loadDropdownData(): void {
    forkJoin({
      analysts: this.analystService.getAll(),
      securities: this.securityService.getAll()
    }).subscribe({
      next: ({ analysts, securities }) => {
        this.analysts.set(analysts);
        this.securities.set(securities);

        if (this.isEditMode()) {
          this.loadReport();
        } else {
          this.loading.set(false);
        }
      },
      error: () => {
        this.error.set('Stammdaten konnten nicht geladen werden.');
        this.loading.set(false);
      }
    });
  }

  private loadReport(): void {
    const id = this.reportId();
    if (!id) return;

    this.reportService.getById(id).subscribe({
      next: (report) => {
        this.form.patchValue({
          analystId: report.analystId,
          securityId: report.securityId,
          reportType: report.reportType,
          title: report.title,
          executiveSummary: report.executiveSummary,
          rating: report.rating,
          targetPrice: report.targetPrice,
          currentPrice: report.currentPrice,
          riskLevel: report.riskLevel ?? 'MEDIUM'
        });
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Report konnte nicht geladen werden.');
        this.loading.set(false);
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const raw = this.form.getRawValue();
    const request: CreateReportRequest = {
      analystId: raw.analystId!,
      securityId: raw.securityId!,
      reportType: raw.reportType!,
      title: raw.title!,
      executiveSummary: raw.executiveSummary!,
      rating: raw.rating!,
      targetPrice: raw.targetPrice!,
      currentPrice: raw.currentPrice ?? undefined,
      riskLevel: raw.riskLevel ?? undefined
    };

    if (this.isEditMode()) {
      const id = this.reportId()!;
      this.reportState.updateReport(id, request).subscribe({
        next: (report) => {
          this.submitting.set(false);
          this.router.navigate(['/reports', report.id]);
        },
        error: () => {
          this.submitting.set(false);
          this.error.set('Report konnte nicht aktualisiert werden.');
        }
      });
    } else {
      this.reportState.createReport(request).subscribe({
        next: (report) => {
          this.submitting.set(false);
          this.router.navigate(['/reports', report.id]);
        },
        error: () => {
          this.submitting.set(false);
          this.error.set('Report konnte nicht erstellt werden.');
        }
      });
    }
  }

  onCancel(): void {
    if (this.isEditMode()) {
      const id = this.reportId();
      if (id) {
        this.router.navigate(['/reports', id]);
        return;
      }
    }
    this.router.navigate(['/reports']);
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.form.get(fieldName);
    if (!control) return false;
    return control.invalid && control.touched;
  }
}
