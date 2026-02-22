import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Report, CreateReportRequest } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReportService {

  private readonly url = `${environment.apiUrl}/reports`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Report[]> {
    return this.http.get<Report[]>(this.url);
  }

  getById(id: number): Observable<Report> {
    return this.http.get<Report>(`${this.url}/${id}`);
  }

  getByAnalyst(analystId: number): Observable<Report[]> {
    return this.http.get<Report[]>(this.url, { params: { analystId } });
  }

  getBySecurity(securityId: number): Observable<Report[]> {
    return this.http.get<Report[]>(this.url, { params: { securityId } });
  }

  create(request: CreateReportRequest): Observable<Report> {
    return this.http.post<Report>(this.url, request);
  }

  update(id: number, request: CreateReportRequest): Observable<Report> {
    return this.http.put<Report>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
