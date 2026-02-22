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

  get latestReports(): Report[] {
    return this.reports().slice(0, 5);
  }

  ratingClass(rating: string): string {
    if (rating === 'STRONG_BUY' || rating === 'BUY') return 'positive';
    if (rating === 'SELL' || rating === 'STRONG_SELL') return 'negative';
    return 'neutral';
  }
}
