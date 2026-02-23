import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface WatchlistEntry {
  id: number;
  userId: string;
  securityId: number;
  securityTicker: string;
  securityName: string;
  addedAt: string;
  notes: string;
  alertOnNewReport: boolean;
}

export interface AddToWatchlistRequest {
  securityId: number;
  notes?: string;
  alertOnNewReport?: boolean;
}

@Injectable({ providedIn: 'root' })
export class WatchlistService {

  private readonly url = `${environment.apiUrl}/watchlist`;

  private readonly headers = new HttpHeaders({
    'X-User-Id': 'demo-user'
  });

  constructor(private readonly http: HttpClient) {}

  getWatchlist(): Observable<WatchlistEntry[]> {
    return this.http.get<WatchlistEntry[]>(this.url, { headers: this.headers });
  }

  addToWatchlist(request: AddToWatchlistRequest): Observable<WatchlistEntry> {
    return this.http.post<WatchlistEntry>(this.url, request, { headers: this.headers });
  }

  updateEntry(securityId: number, request: AddToWatchlistRequest): Observable<WatchlistEntry> {
    return this.http.put<WatchlistEntry>(`${this.url}/${securityId}`, request, { headers: this.headers });
  }

  removeFromWatchlist(securityId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${securityId}`, { headers: this.headers });
  }

  checkWatchlist(securityId: number): Observable<{ onWatchlist: boolean }> {
    return this.http.get<{ onWatchlist: boolean }>(`${this.url}/${securityId}/check`, { headers: this.headers });
  }

  getCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.url}/count`, { headers: this.headers });
  }
}
