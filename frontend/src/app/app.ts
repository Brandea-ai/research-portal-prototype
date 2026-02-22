import { Component, computed, inject, viewChild } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter, map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { TopbarComponent } from './layout/topbar/topbar.component';
import { AuthService } from './core/services/auth.service';

const PAGE_TITLES: Record<string, string> = {
  dashboard: 'Dashboard',
  reports: 'Research Reports',
  securities: 'Wertschriften',
  analysts: 'Analysten'
};

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SidebarComponent, TopbarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly router = inject(Router);
  readonly authService = inject(AuthService);

  readonly sidebar = viewChild<SidebarComponent>('sidebar');

  private readonly currentRoute = toSignal(
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map(e => e.urlAfterRedirects.split('/')[1] || 'dashboard')
    ),
    { initialValue: 'dashboard' }
  );

  protected readonly pageTitle = computed(
    () => PAGE_TITLES[this.currentRoute()] ?? 'Dashboard'
  );

  protected readonly sidebarMargin = computed(() => {
    const sb = this.sidebar();
    if (!sb) return 'var(--sidebar-width)';
    return sb.collapsed() ? '56px' : 'var(--sidebar-width)';
  });

  onMenuToggle(): void {
    this.sidebar()?.toggleMobile();
  }
}
