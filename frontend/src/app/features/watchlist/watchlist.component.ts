import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { WatchlistService, WatchlistEntry, AddToWatchlistRequest } from '../../core/services/watchlist.service';
import { SecurityService } from '../../core/services/security.service';
import { Security } from '../../core/models/security.model';
import { BreadcrumbComponent, BreadcrumbItem } from '../../shared/components/breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-watchlist',
  standalone: true,
  imports: [FormsModule, TranslatePipe, BreadcrumbComponent],
  templateUrl: './watchlist.component.html',
  styleUrl: './watchlist.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WatchlistComponent implements OnInit {

  private readonly watchlistService = inject(WatchlistService);
  private readonly securityService = inject(SecurityService);
  private readonly router = inject(Router);

  entries = signal<WatchlistEntry[]>([]);
  loading = signal(true);
  showAddForm = signal(false);
  editingEntryId = signal<number | null>(null);
  securities = signal<Security[]>([]);
  showDeleteConfirm = signal<number | null>(null);

  editNotesValue = signal('');

  // Add form state
  selectedSecurityId = signal<number | null>(null);
  addNotes = signal('');
  addAlert = signal(false);

  breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Watchlist' }
  ];

  ngOnInit(): void {
    this.loadEntries();
    this.securityService.getAll().subscribe(data => this.securities.set(data));
  }

  loadEntries(): void {
    this.loading.set(true);
    this.watchlistService.getWatchlist().subscribe({
      next: (data) => {
        const sorted = [...data].sort((a, b) =>
          new Date(b.addedAt).getTime() - new Date(a.addedAt).getTime()
        );
        this.entries.set(sorted);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  openSecurity(securityId: number): void {
    this.router.navigate(['/securities', securityId]);
  }

  openAddForm(): void {
    this.selectedSecurityId.set(null);
    this.addNotes.set('');
    this.addAlert.set(false);
    this.showAddForm.set(true);
  }

  closeAddForm(): void {
    this.showAddForm.set(false);
  }

  submitAddForm(): void {
    const secId = this.selectedSecurityId();
    if (secId === null) return;

    const request: AddToWatchlistRequest = {
      securityId: secId,
      notes: this.addNotes() || undefined,
      alertOnNewReport: this.addAlert()
    };

    this.watchlistService.addToWatchlist(request).subscribe({
      next: () => {
        this.showAddForm.set(false);
        this.loadEntries();
      }
    });
  }

  startEditNotes(entry: WatchlistEntry): void {
    this.editingEntryId.set(entry.id);
    this.editNotesValue.set(entry.notes || '');
  }

  cancelEditNotes(): void {
    this.editingEntryId.set(null);
    this.editNotesValue.set('');
  }

  saveNotes(entry: WatchlistEntry): void {
    const request: AddToWatchlistRequest = {
      securityId: entry.securityId,
      notes: this.editNotesValue(),
      alertOnNewReport: entry.alertOnNewReport
    };

    this.watchlistService.updateEntry(entry.securityId, request).subscribe({
      next: () => {
        this.editingEntryId.set(null);
        this.loadEntries();
      }
    });
  }

  toggleAlert(entry: WatchlistEntry): void {
    const request: AddToWatchlistRequest = {
      securityId: entry.securityId,
      notes: entry.notes,
      alertOnNewReport: !entry.alertOnNewReport
    };

    this.watchlistService.updateEntry(entry.securityId, request).subscribe({
      next: () => {
        this.loadEntries();
      }
    });
  }

  requestRemove(securityId: number): void {
    this.showDeleteConfirm.set(securityId);
  }

  cancelRemove(): void {
    this.showDeleteConfirm.set(null);
  }

  confirmRemove(securityId: number): void {
    this.watchlistService.removeFromWatchlist(securityId).subscribe({
      next: () => {
        this.showDeleteConfirm.set(null);
        this.loadEntries();
      }
    });
  }

  getRelativeTime(dateStr: string): string {
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMinutes = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMinutes < 1) return 'gerade eben';
    if (diffMinutes < 60) return `vor ${diffMinutes} Min.`;
    if (diffHours < 24) return `vor ${diffHours} Std.`;
    if (diffDays < 30) return `vor ${diffDays} Tag${diffDays > 1 ? 'en' : ''}`;
    return new DatePipe('de-CH').transform(date, 'dd.MM.yyyy') ?? '';
  }

  availableSecurities(): Security[] {
    const watchedIds = new Set(this.entries().map(e => e.securityId));
    return this.securities().filter(s => !watchedIds.has(s.id));
  }
}
