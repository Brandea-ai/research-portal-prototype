import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Security } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SecurityService {

  private readonly url = `${environment.apiUrl}/securities`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Security[]> {
    return this.http.get<Security[]>(this.url);
  }

  getById(id: number): Observable<Security> {
    return this.http.get<Security>(`${this.url}/${id}`);
  }

  getByTicker(ticker: string): Observable<Security> {
    return this.http.get<Security>(this.url, { params: { ticker } });
  }
}
