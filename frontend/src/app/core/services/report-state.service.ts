import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ReportService } from './report.service';
import { Report, CreateReportRequest } from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportStateService {

  private readonly reportService = inject(ReportService);
  private readonly reportsSubject = new BehaviorSubject<Report[]>([]);
  private readonly loadingSubject = new BehaviorSubject<boolean>(true);
  private loaded = false;

  readonly reports$ = this.reportsSubject.asObservable();
  readonly loading$ = this.loadingSubject.asObservable();

  /** Load reports if not already loaded. Call from any component. */
  loadReports(): void {
    if (this.loaded) return;
    this.loadingSubject.next(true);
    this.reportService.getAll().subscribe({
      next: (reports) => {
        this.reportsSubject.next(reports);
        this.loadingSubject.next(false);
        this.loaded = true;
      },
      error: () => {
        this.loadingSubject.next(false);
      }
    });
  }

  /** Force refresh after CRUD operations */
  refresh(): void {
    this.loaded = false;
    this.loadReports();
  }

  /** Get current snapshot */
  getReports(): Report[] {
    return this.reportsSubject.getValue();
  }

  /** Create report and auto-refresh */
  createReport(request: CreateReportRequest): Observable<Report> {
    return this.reportService.create(request).pipe(
      tap(() => this.refresh())
    );
  }

  /** Update report and auto-refresh */
  updateReport(id: number, request: CreateReportRequest): Observable<Report> {
    return this.reportService.update(id, request).pipe(
      tap(() => this.refresh())
    );
  }

  /** Delete report and auto-refresh */
  deleteReport(id: number): Observable<void> {
    return this.reportService.delete(id).pipe(
      tap(() => this.refresh())
    );
  }
}
