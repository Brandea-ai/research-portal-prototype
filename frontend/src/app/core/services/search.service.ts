import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SearchResult {
  type: string;
  id: number;
  title: string;
  subtitle: string;
  highlight: string;
  relevance: number;
}

export interface SearchResponse {
  query: string;
  totalResults: number;
  results: SearchResult[];
  resultsByType: Record<string, number>;
}

@Injectable({ providedIn: 'root' })
export class SearchService {

  private readonly url = `${environment.apiUrl}/search`;

  constructor(private readonly http: HttpClient) {}

  search(query: string, type?: string, limit = 10): Observable<SearchResponse> {
    let params = new HttpParams()
      .set('q', query)
      .set('limit', limit);

    if (type) {
      params = params.set('type', type);
    }

    return this.http.get<SearchResponse>(this.url, { params });
  }
}
