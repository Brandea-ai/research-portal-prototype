import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'reports',
    loadComponent: () =>
      import('./features/reports/reports.component').then(m => m.ReportsComponent)
  },
  {
    path: 'securities',
    loadComponent: () =>
      import('./features/securities/securities.component').then(m => m.SecuritiesComponent)
  },
  {
    path: 'analysts',
    loadComponent: () =>
      import('./features/analysts/analysts.component').then(m => m.AnalystsComponent)
  },
  { path: '**', redirectTo: 'dashboard' }
];
