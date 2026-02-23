import {
  Component,
  OnInit,
  inject,
  signal,
  computed,
  ChangeDetectionStrategy,
} from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import {
  AdminService,
  SystemInfo,
  SystemStats,
  ValidationResult,
  ApiMetrics,
  EndpointEntry,
} from '../../core/services/admin.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [DecimalPipe, DatePipe, TranslatePipe],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SettingsComponent implements OnInit {

  private readonly adminService = inject(AdminService);

  /* ---- Signals ---- */
  readonly systemInfo = signal<SystemInfo | null>(null);
  readonly systemStats = signal<SystemStats | null>(null);
  readonly validation = signal<ValidationResult | null>(null);
  readonly metrics = signal<ApiMetrics | null>(null);
  readonly loading = signal(true);
  readonly validationRunning = signal(false);
  readonly metricsResetting = signal(false);

  /* ---- Computed ---- */
  readonly errorRate = computed(() => {
    const m = this.metrics();
    if (!m || m.totalRequests === 0) return 0;
    return (m.totalErrors / m.totalRequests) * 100;
  });

  readonly topEndpoints = computed((): EndpointEntry[] => {
    const m = this.metrics();
    if (!m || !m.requestsByEndpoint) return [];
    return Object.entries(m.requestsByEndpoint)
      .map(([endpoint, count]) => ({ endpoint, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 8);
  });

  readonly uptimeFormatted = computed(() => {
    const info = this.systemInfo();
    if (!info) return '';
    const secs = info.uptimeSeconds;
    const hours = Math.floor(secs / 3600);
    const mins = Math.floor((secs % 3600) / 60);
    const s = secs % 60;
    if (hours > 0) return `${hours}h ${mins}m ${s}s`;
    if (mins > 0) return `${mins}m ${s}s`;
    return `${s}s`;
  });

  ngOnInit(): void {
    this.loadAll();
  }

  onRunValidation(): void {
    this.validationRunning.set(true);
    this.adminService.runValidation().subscribe({
      next: (result) => {
        this.validation.set(result);
        this.validationRunning.set(false);
      },
      error: () => {
        this.validationRunning.set(false);
      },
    });
  }

  onResetMetrics(): void {
    this.metricsResetting.set(true);
    this.adminService.resetMetrics().subscribe({
      next: () => {
        this.metricsResetting.set(false);
        this.adminService.getMetrics().subscribe((m) => this.metrics.set(m));
      },
      error: () => {
        this.metricsResetting.set(false);
      },
    });
  }

  validationCategoryClass(count: number): string {
    return count === 0 ? 'positive' : 'negative';
  }

  private loadAll(): void {
    this.adminService.getSystemInfo().subscribe((info) => {
      this.systemInfo.set(info);
      this.checkLoading();
    });

    this.adminService.getSystemStats().subscribe((stats) => {
      this.systemStats.set(stats);
      this.checkLoading();
    });

    this.adminService.getValidation().subscribe((v) => {
      this.validation.set(v);
      this.checkLoading();
    });

    this.adminService.getMetrics().subscribe((m) => {
      this.metrics.set(m);
      this.checkLoading();
    });
  }

  private checkLoading(): void {
    if (
      this.systemInfo() &&
      this.systemStats() &&
      this.validation() &&
      this.metrics()
    ) {
      this.loading.set(false);
    }
  }
}
