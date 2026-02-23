import {
  Component,
  OnInit,
  inject,
  signal,
  ChangeDetectionStrategy,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { BreadcrumbComponent, BreadcrumbItem } from '../../shared/components/breadcrumb/breadcrumb.component';
import { AdminService, ValidationResult } from '../../core/services/admin.service';

@Component({
  selector: 'app-validation',
  standalone: true,
  imports: [DatePipe, TranslatePipe, BreadcrumbComponent],
  templateUrl: './validation.component.html',
  styleUrl: './validation.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ValidationComponent implements OnInit {

  private readonly adminService = inject(AdminService);

  /* ---- Breadcrumb ---- */
  readonly breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Datenvalidierung' },
  ];

  /* ---- Signals ---- */
  readonly result = signal<ValidationResult | null>(null);
  readonly loading = signal(true);
  readonly running = signal(false);

  ngOnInit(): void {
    this.loadValidation();
  }

  runValidation(): void {
    this.running.set(true);
    this.adminService.runValidation().subscribe({
      next: (data) => {
        this.result.set(data);
        this.running.set(false);
      },
      error: () => {
        this.running.set(false);
      },
    });
  }

  private loadValidation(): void {
    this.loading.set(true);
    this.adminService.getValidation().subscribe({
      next: (data) => {
        this.result.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }
}
