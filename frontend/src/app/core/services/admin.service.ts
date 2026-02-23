import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/* ------------------------------------------------
   Interfaces matching the backend DTOs
   GET /api/info          → SystemInfo
   GET /api/info/stats    → SystemStats
   GET /api/validation    → ValidationResult
   GET /api/metrics       → ApiMetrics
   ------------------------------------------------ */

export interface SystemInfo {
  name: string;
  version: string;
  description: string;
  status: string;
  uptimeSeconds: number;
  javaVersion: string;
}

export interface SystemStats {
  reportCount: number;
  analystCount: number;
  securityCount: number;
}

export interface ValidationResult {
  orphanedReports: number;
  invalidSecurityRefs: number;
  negativeMarketCaps: number;
  invalidAccuracies: number;
  totalIssues: number;
  hasIssues: boolean;
  lastRunAt: string;
  nextRunAt: string;
}

export interface EndpointEntry {
  endpoint: string;
  count: number;
}

export interface ApiMetrics {
  totalRequests: number;
  totalErrors: number;
  avgResponseTimeMs: number;
  requestsByEndpoint: Record<string, number>;
  requestsByStatus: Record<string, number>;
  uptime: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {

  private readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  getSystemInfo(): Observable<SystemInfo> {
    return this.http.get<SystemInfo>(`${this.baseUrl}/info`);
  }

  getSystemStats(): Observable<SystemStats> {
    return this.http.get<SystemStats>(`${this.baseUrl}/info/stats`);
  }

  getValidation(): Observable<ValidationResult> {
    return this.http.get<ValidationResult>(`${this.baseUrl}/validation`);
  }

  runValidation(): Observable<ValidationResult> {
    return this.http.post<ValidationResult>(`${this.baseUrl}/validation/run`, null);
  }

  getMetrics(): Observable<ApiMetrics> {
    return this.http.get<ApiMetrics>(`${this.baseUrl}/metrics`);
  }

  resetMetrics(): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/metrics`);
  }
}
