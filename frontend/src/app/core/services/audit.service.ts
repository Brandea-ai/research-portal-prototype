import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AuditLog {
  id: number;
  timestamp: string;
  action: string;
  entityType: string;
  entityId: number;
  userId: string;
  userName: string;
  userRole: string;
  details: string;
  ipAddress: string;
}

@Injectable({ providedIn: 'root' })
export class AuditService {

  private readonly url = `${environment.apiUrl}/audit`;

  constructor(private readonly http: HttpClient) {}

  getRecentLogs(limit = 10): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(this.url, { params: { limit: limit.toString() } });
  }

  getAllLogs(limit = 50): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(this.url, { params: { limit: limit.toString() } });
  }

  getLogsByEntity(entityType: string, entityId: number): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(this.url, {
      params: { entityType, entityId: entityId.toString() },
    });
  }

  getLogsByEntityType(entityType: string, limit = 50): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(this.url, {
      params: { entityType, limit: limit.toString() },
    });
  }

  getReportAuditTrail(reportId: number): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.url}/report/${reportId}`);
  }
}
