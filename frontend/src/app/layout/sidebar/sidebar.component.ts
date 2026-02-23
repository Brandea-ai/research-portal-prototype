import { Component, signal, inject, DestroyRef, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);

  readonly collapsed = signal(false);
  readonly mobileOpen = signal(false);

  private tabletQuery!: MediaQueryList;
  private mobileQuery!: MediaQueryList;

  ngOnInit(): void {
    this.tabletQuery = window.matchMedia('(max-width: 1024px) and (min-width: 769px)');
    this.mobileQuery = window.matchMedia('(max-width: 768px)');

    const onTabletChange = (e: MediaQueryListEvent) => {
      if (e.matches) {
        this.collapsed.set(true);
      }
    };

    const onMobileChange = (e: MediaQueryListEvent) => {
      if (e.matches) {
        this.mobileOpen.set(false);
      }
    };

    // Set initial state
    if (this.tabletQuery.matches) {
      this.collapsed.set(true);
    }

    this.tabletQuery.addEventListener('change', onTabletChange);
    this.mobileQuery.addEventListener('change', onMobileChange);

    this.destroyRef.onDestroy(() => {
      this.tabletQuery.removeEventListener('change', onTabletChange);
      this.mobileQuery.removeEventListener('change', onMobileChange);
    });
  }

  toggle(): void {
    this.collapsed.update(v => !v);
  }

  toggleMobile(): void {
    this.mobileOpen.update(v => !v);
  }

  closeMobile(): void {
    this.mobileOpen.set(false);
  }
}
