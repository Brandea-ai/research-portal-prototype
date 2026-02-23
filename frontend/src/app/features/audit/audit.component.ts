import {
  Component,
  OnInit,
  inject,
  signal,
  computed,
  ChangeDetectionStrategy,
  effect,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BreadcrumbComponent, BreadcrumbItem } from '../../shared/components/breadcrumb/breadcrumb.component';
import { AuditService, AuditLog } from '../../core/services/audit.service';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [DatePipe, FormsModule, TranslatePipe, BreadcrumbComponent],
  templateUrl: './audit.component.html',
  styleUrl: './audit.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditComponent implements OnInit {

  private readonly auditService = inject(AuditService);
  private readonly router = inject(Router);

  /* ---- Breadcrumb ---- */
  readonly breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Audit Trail' },
  ];

  /* ---- Signals ---- */
  readonly logs = signal<AuditLog[]>([]);
  readonly loading = signal(true);
  readonly filterAction = signal<string>('');
  readonly filterEntityType = signal<string>('');
  readonly limit = signal<number>(50);

  /* ---- Computed ---- */
  readonly filteredLogs = computed(() => {
    let result = this.logs();
    const action = this.filterAction();
    const entityType = this.filterEntityType();

    if (action) {
      result = result.filter(log => log.action === action);
    }
    if (entityType) {
      result = result.filter(log => log.entityType === entityType);
    }

    return result.sort((a, b) =>
      new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
    );
  });

  constructor() {
    effect(() => {
      const currentLimit = this.limit();
      this.loadLogs(currentLimit);
    });
  }

  ngOnInit(): void {
    // Initial load triggered by effect
  }

  onFilterActionChange(value: string): void {
    this.filterAction.set(value);
  }

  onFilterEntityTypeChange(value: string): void {
    this.filterEntityType.set(value);
  }

  onLimitChange(value: number): void {
    this.limit.set(value);
  }

  navigateToEntity(log: AuditLog): void {
    const type = log.entityType;
    const id = log.entityId;

    switch (type) {
      case 'REPORT':
        this.router.navigate(['/reports', id]);
        break;
      case 'SECURITY':
        this.router.navigate(['/securities', id]);
        break;
      case 'ANALYST':
        this.router.navigate(['/analysts', id]);
        break;
    }
  }

  getActionBadgeClass(action: string): string {
    switch (action) {
      case 'CREATE': return 'badge--create';
      case 'UPDATE': return 'badge--update';
      case 'DELETE': return 'badge--delete';
      case 'VIEW':   return 'badge--view';
      case 'EXPORT': return 'badge--export';
      case 'IMPORT': return 'badge--import';
      case 'LOGIN':  return 'badge--view';
      case 'LOGOUT': return 'badge--view';
      default:       return 'badge--view';
    }
  }

  private loadLogs(limit: number): void {
    this.loading.set(true);
    this.auditService.getAllLogs(limit).subscribe({
      next: (logs) => {
        this.logs.set(logs);
        this.loading.set(false);
      },
      error: () => {
        this.logs.set([]);
        this.loading.set(false);
      },
    });
  }
}
