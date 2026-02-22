import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Analyst } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AnalystService {

  private readonly url = `${environment.apiUrl}/analysts`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Analyst[]> {
    return this.http.get<Analyst[]>(this.url);
  }

  getById(id: number): Observable<Analyst> {
    return this.http.get<Analyst>(`${this.url}/${id}`);
  }
}
