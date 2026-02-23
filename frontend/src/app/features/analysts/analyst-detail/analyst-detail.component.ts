import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AnalystService } from '../../../core/services/analyst.service';
import { ReportService } from '../../../core/services/report.service';
import { Analyst } from '../../../core/models/analyst.model';
import { Report } from '../../../core/models/report.model';

@Component({
  selector: 'app-analyst-detail',
  standalone: true,
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './analyst-detail.component.html',
  styleUrl: './analyst-detail.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnalystDetailComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly analystService = inject(AnalystService);
  private readonly reportService = inject(ReportService);

  analyst = signal<Analyst | null>(null);
  reports = signal<Report[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = Number(params.get('id'));
        return forkJoin({
          analyst: this.analystService.getById(id),
          reports: this.reportService.getByAnalyst(id),
        });
      })
    ).subscribe({
      next: ({ analyst, reports }) => {
        this.analyst.set(analyst);
        this.reports.set(reports);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Analyst konnte nicht geladen werden.');
        this.loading.set(false);
      }
    });
  }

  openReport(id: number): void {
    this.router.navigate(['/reports', id]);
  }

  goBack(): void {
    this.router.navigate(['/analysts']);
  }

  ratingColorClass(rating: string): string {
    if (rating === 'STRONG_BUY' || rating === 'BUY') return 'text--positive';
    if (rating === 'SELL' || rating === 'STRONG_SELL') return 'text--negative';
    return 'text--neutral';
  }

  formattedRating(rating: string): string {
    return rating.replace(/_/g, ' ');
  }

  starDisplay(rating: number): string {
    return '\u2605'.repeat(rating) + '\u2606'.repeat(5 - rating);
  }
}
