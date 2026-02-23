import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-server-error',
  standalone: true,
  templateUrl: './server-error.component.html',
  styleUrl: './server-error.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServerErrorComponent {
  constructor(private readonly router: Router) {}

  reload(): void {
    window.location.reload();
  }

  navigateToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
}
