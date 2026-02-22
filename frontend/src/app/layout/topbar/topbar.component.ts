import { Component, input, inject, output } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

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

  logout(): void {
    this.auth.logout();
  }

  onMenuToggle(): void {
    this.menuToggle.emit();
  }
}
