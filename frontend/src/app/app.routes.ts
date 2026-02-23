import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then(m => m.LoginComponent)
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'reports/new',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/reports/report-form/report-form.component').then(m => m.ReportFormComponent)
  },
  {
    path: 'reports/:id/edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/reports/report-form/report-form.component').then(m => m.ReportFormComponent)
  },
  {
    path: 'reports/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/reports/report-detail/report-detail.component').then(m => m.ReportDetailComponent)
  },
  {
    path: 'reports',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/reports/reports.component').then(m => m.ReportsComponent)
  },
  {
    path: 'securities/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/securities/security-detail/security-detail.component').then(m => m.SecurityDetailComponent)
  },
  {
    path: 'securities',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/securities/securities.component').then(m => m.SecuritiesComponent)
  },
  {
    path: 'analysts/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/analysts/analyst-detail/analyst-detail.component').then(m => m.AnalystDetailComponent)
  },
  {
    path: 'analysts',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/analysts/analysts.component').then(m => m.AnalystsComponent)
  },
  {
    path: 'settings',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/settings/settings.component').then(m => m.SettingsComponent)
  },
  {
    path: 'watchlist',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/watchlist/watchlist.component').then(m => m.WatchlistComponent)
  },
  {
    path: 'audit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/audit/audit.component').then(m => m.AuditComponent)
  },
  {
    path: 'import',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/import/import.component').then(m => m.ImportComponent)
  },
  {
    path: 'validation',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/validation/validation.component').then(m => m.ValidationComponent)
  },
  {
    path: 'metrics',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/metrics/metrics.component').then(m => m.MetricsComponent)
  },
  {
    path: 'forbidden',
    loadComponent: () =>
      import('./features/error-pages/forbidden/forbidden.component').then(m => m.ForbiddenComponent)
  },
  {
    path: 'error',
    loadComponent: () =>
      import('./features/error-pages/server-error/server-error.component').then(m => m.ServerErrorComponent)
  },
  {
    path: '**',
    loadComponent: () =>
      import('./features/error-pages/not-found/not-found.component').then(m => m.NotFoundComponent)
  },
];
