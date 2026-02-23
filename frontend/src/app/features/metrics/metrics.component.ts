import {
  Component,
  OnInit,
  inject,
  signal,
  computed,
  ChangeDetectionStrategy,
} from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { RouterLink } from '@angular/router';
import { BreadcrumbComponent, BreadcrumbItem } from '../../shared/components/breadcrumb/breadcrumb.component';
import { AdminService, ApiMetrics, EndpointEntry } from '../../core/services/admin.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface RateLimitStatus {
  clientIp: string;
  category: string;
  remaining: number;
  limit: number;
  resetAt: number;
  limited: boolean;
}

export interface RateLimitStats {
  totalBlocked: number;
  activeClients: number;
  blockedByCategory: Record<string, number>;
}

export interface StatusEntry {
  code: string;
  count: number;
  percentage: number;
}

@Component({
  selector: 'app-metrics',
  standalone: true,
  imports: [DecimalPipe, TranslatePipe, RouterLink, BreadcrumbComponent],
  templateUrl: './metrics.component.html',
  styleUrl: './metrics.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MetricsComponent implements OnInit {

  private readonly adminService = inject(AdminService);
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  /* ---- Breadcrumb ---- */
  readonly breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Settings', route: '/settings' },
    { label: 'Metrics' },
  ];

  /* ---- Signals ---- */
  readonly metrics = signal<ApiMetrics | null>(null);
  readonly rateLimitStatus = signal<RateLimitStatus | null>(null);
  readonly rateLimitStats = signal<RateLimitStats | null>(null);
  readonly loading = signal(true);
  readonly resetting = signal(false);
  readonly rateLimitResetting = signal(false);

  /* ---- Computed ---- */
  readonly errorRate = computed(() => {
    const m = this.metrics();
    if (!m || m.totalRequests === 0) return 0;
    return (m.totalErrors / m.totalRequests) * 100;
  });

  readonly successRate = computed(() => {
    return 100 - this.errorRate();
  });

  readonly allEndpoints = computed((): EndpointEntry[] => {
    const m = this.metrics();
    if (!m || !m.requestsByEndpoint) return [];
    return Object.entries(m.requestsByEndpoint)
      .map(([endpoint, count]) => ({ endpoint, count }))
      .sort((a, b) => b.count - a.count);
  });

  readonly maxEndpointCount = computed(() => {
    const eps = this.allEndpoints();
    return eps.length > 0 ? eps[0].count : 1;
  });

  readonly statusEntries = computed((): StatusEntry[] => {
    const m = this.metrics();
    if (!m || !m.requestsByStatus) return [];
    const entries = Object.entries(m.requestsByStatus);
    const total = entries.reduce((sum, [, count]) => sum + count, 0) || 1;
    return entries
      .map(([code, count]) => ({
        code,
        count,
        percentage: (count / total) * 100,
      }))
      .sort((a, b) => a.code.localeCompare(b.code));
  });

  readonly rateLimitUsage = computed(() => {
    const s = this.rateLimitStatus();
    if (!s) return 0;
    return ((s.limit - s.remaining) / s.limit) * 100;
  });

  readonly blockedCategories = computed((): Array<{ category: string; count: number }> => {
    const s = this.rateLimitStats();
    if (!s || !s.blockedByCategory) return [];
    return Object.entries(s.blockedByCategory)
      .map(([category, count]) => ({ category, count }))
      .sort((a, b) => b.count - a.count);
  });

  ngOnInit(): void {
    this.loadAll();
  }

  onResetMetrics(): void {
    this.resetting.set(true);
    this.adminService.resetMetrics().subscribe({
      next: () => {
        this.resetting.set(false);
        this.adminService.getMetrics().subscribe((m) => this.metrics.set(m));
      },
      error: () => this.resetting.set(false),
    });
  }

  onResetRateLimits(): void {
    this.rateLimitResetting.set(true);
    this.http.delete<Record<string, string>>(`${this.apiUrl}/rate-limit/reset`).subscribe({
      next: () => {
        this.rateLimitResetting.set(false);
        this.loadRateLimitData();
      },
      error: () => this.rateLimitResetting.set(false),
    });
  }

  endpointBarWidth(count: number): string {
    const max = this.maxEndpointCount();
    return `${(count / max) * 100}%`;
  }

  statusCodeClass(code: string): string {
    if (code.startsWith('2')) return 'status-code--2xx';
    if (code.startsWith('3')) return 'status-code--3xx';
    if (code.startsWith('4')) return 'status-code--4xx';
    if (code.startsWith('5')) return 'status-code--5xx';
    return '';
  }

  private loadAll(): void {
    this.adminService.getMetrics().subscribe({
      next: (m) => {
        this.metrics.set(m);
        this.checkLoading();
      },
      error: () => this.checkLoading(),
    });

    this.loadRateLimitData();
  }

  private loadRateLimitData(): void {
    this.http.get<RateLimitStatus>(`${this.apiUrl}/rate-limit/status`).subscribe({
      next: (s) => {
        this.rateLimitStatus.set(s);
        this.checkLoading();
      },
      error: () => this.checkLoading(),
    });

    this.http.get<RateLimitStats>(`${this.apiUrl}/rate-limit/stats`).subscribe({
      next: (s) => {
        this.rateLimitStats.set(s);
        this.checkLoading();
      },
      error: () => this.checkLoading(),
    });
  }

  private checkLoading(): void {
    if (this.metrics()) {
      this.loading.set(false);
    }
  }
}
