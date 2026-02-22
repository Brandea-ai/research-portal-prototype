import { Component, computed, inject } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter, map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { TopbarComponent } from './layout/topbar/topbar.component';

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
}
