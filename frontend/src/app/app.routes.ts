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
    path: 'reports',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/reports/reports.component').then(m => m.ReportsComponent)
  },
  {
    path: 'securities',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/securities/securities.component').then(m => m.SecuritiesComponent)
  },
  {
    path: 'analysts',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/analysts/analysts.component').then(m => m.AnalystsComponent)
  },
  { path: '**', redirectTo: 'dashboard' }
];
