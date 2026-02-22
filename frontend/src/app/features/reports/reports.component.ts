import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ReportService } from '../../core/services/report.service';
import { Report } from '../../core/models/report.model';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [DatePipe, DecimalPipe],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent implements OnInit {

  reports = signal<Report[]>([]);
  loading = signal(true);

  constructor(private readonly reportService: ReportService) {}

  ngOnInit(): void {
    this.reportService.getAll().subscribe(data => {
      this.reports.set(data);
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
}
