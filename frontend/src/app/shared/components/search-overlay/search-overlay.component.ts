import {
  Component,
  ChangeDetectionStrategy,
  signal,
  inject,
  ElementRef,
  OnInit,
  OnDestroy,
  ViewChild
} from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, tap, filter } from 'rxjs/operators';
import { SearchService, SearchResult } from '../../../core/services/search.service';

@Component({
  selector: 'app-search-overlay',
  standalone: true,
  imports: [],
  templateUrl: './search-overlay.component.html',
  styleUrl: './search-overlay.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchOverlayComponent implements OnInit, OnDestroy {
  private readonly searchService = inject(SearchService);
  private readonly router = inject(Router);
  private readonly elementRef = inject(ElementRef);

  readonly query = signal('');
  readonly results = signal<SearchResult[]>([]);
  readonly isOpen = signal(false);
  readonly loading = signal(false);
  readonly selectedIndex = signal(0);

  @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>;

  private readonly searchSubject = new Subject<string>();
  private subscription!: Subscription;
  private clickOutsideListener!: (event: MouseEvent) => void;
  private keyboardListener!: (event: KeyboardEvent) => void;

  ngOnInit(): void {
    this.subscription = this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      filter((q: string) => q.length >= 2),
      tap(() => this.loading.set(true)),
      switchMap((q: string) => this.searchService.search(q))
    ).subscribe({
      next: (response) => {
        this.results.set(response.results);
        this.loading.set(false);
        this.selectedIndex.set(0);
      },
      error: () => {
        this.results.set([]);
        this.loading.set(false);
      }
    });

    this.clickOutsideListener = (event: MouseEvent) => {
      if (!this.elementRef.nativeElement.contains(event.target)) {
        this.isOpen.set(false);
      }
    };
    document.addEventListener('click', this.clickOutsideListener);

    this.keyboardListener = (event: KeyboardEvent) => {
      if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
        event.preventDefault();
        this.searchInput?.nativeElement?.focus();
        this.isOpen.set(true);
      }
    };
    document.addEventListener('keydown', this.keyboardListener);
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    document.removeEventListener('click', this.clickOutsideListener);
    document.removeEventListener('keydown', this.keyboardListener);
  }

  onInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.query.set(value);
    if (value.length >= 2) {
      this.searchSubject.next(value);
    } else {
      this.results.set([]);
      this.loading.set(false);
    }
  }

  onKeydown(event: KeyboardEvent): void {
    const currentResults = this.results();

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        if (this.selectedIndex() < currentResults.length - 1) {
          this.selectedIndex.update(i => i + 1);
        }
        break;

      case 'ArrowUp':
        event.preventDefault();
        if (this.selectedIndex() > 0) {
          this.selectedIndex.update(i => i - 1);
        }
        break;

      case 'Enter':
        event.preventDefault();
        if (currentResults.length > 0) {
          this.openResult(currentResults[this.selectedIndex()]);
        }
        break;

      case 'Escape':
        this.isOpen.set(false);
        this.searchInput?.nativeElement?.blur();
        break;
    }
  }

  openResult(result: SearchResult): void {
    this.isOpen.set(false);
    this.query.set('');
    this.results.set([]);

    switch (result.type) {
      case 'REPORT':
        this.router.navigate(['/reports', result.id]);
        break;
      case 'ANALYST':
        this.router.navigate(['/analysts', result.id]);
        break;
      case 'SECURITY':
        this.router.navigate(['/securities', result.id]);
        break;
    }
  }

  clear(): void {
    this.query.set('');
    this.results.set([]);
    this.loading.set(false);
    this.searchInput?.nativeElement?.focus();
  }
}
