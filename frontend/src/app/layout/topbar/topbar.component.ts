import { Component, input, inject, output } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-topbar',
  standalone: true,
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.css'
})
export class TopbarComponent {
  pageTitle = input<string>('Dashboard');
  menuToggle = output<void>();
  readonly auth = inject(AuthService);
  readonly themeService = inject(ThemeService);

  logout(): void {
    this.auth.logout();
  }

  onMenuToggle(): void {
    this.menuToggle.emit();
  }

  cycleTheme(): void {
    this.themeService.cycle();
  }
}
