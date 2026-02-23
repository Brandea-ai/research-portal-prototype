import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Report } from '../models';
import { environment } from '../../../environments/environment';

export interface XmlImportResponse {
  importedCount: number;
  status: string;
  timestamp: string;
  reports: Report[];
}

export interface XmlValidationResponse {
  valid: boolean;
  message: string;
  timestamp: string;
}

@Injectable({ providedIn: 'root' })
export class ImportService {

  private readonly url = `${environment.apiUrl}/import`;

  constructor(private readonly http: HttpClient) {}

  importXml(file: File, ticker?: string): Observable<XmlImportResponse> {
    const formData = new FormData();
    formData.append('file', file);
    if (ticker) {
      formData.append('ticker', ticker);
    }
    return this.http.post<XmlImportResponse>(`${this.url}/xml`, formData);
  }

  validateXml(file: File): Observable<XmlValidationResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<XmlValidationResponse>(`${this.url}/xml/validate`, formData);
  }

  getSampleXml(): Observable<string> {
    return this.http.get(`${this.url}/sample`, { responseType: 'text' });
  }
}
