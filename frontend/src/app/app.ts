import { Component, computed, inject, viewChild, OnInit } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter, map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { TopbarComponent } from './layout/topbar/topbar.component';
import { ShortcutHelpComponent } from './shared/components/shortcut-help/shortcut-help.component';
import { AuthService } from './core/services/auth.service';
import { ThemeService } from './core/services/theme.service';
import { KeyboardShortcutService } from './core/services/keyboard-shortcut.service';

const PAGE_TITLES: Record<string, string> = {
  dashboard: 'Dashboard',
  reports: 'Research Reports',
  securities: 'Wertschriften',
  analysts: 'Analysten'
};

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SidebarComponent, TopbarComponent, ShortcutHelpComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private readonly router = inject(Router);
  readonly authService = inject(AuthService);
  readonly themeService = inject(ThemeService);
  private readonly keyboardShortcuts = inject(KeyboardShortcutService);

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
    return sb.collapsed() ? 'var(--sidebar-collapsed)' : 'var(--sidebar-width)';
  });

  ngOnInit(): void {
    // Theme is applied automatically via ThemeService effect
  }

  onMenuToggle(): void {
    this.sidebar()?.toggleMobile();
  }
}
