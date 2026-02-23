import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-notification-container',
  standalone: true,
  templateUrl: './notification-container.component.html',
  styleUrl: './notification-container.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NotificationContainerComponent {

  private readonly notificationService = inject(NotificationService);

  readonly notifications = this.notificationService.notifications;

  dismiss(id: string): void {
    this.notificationService.dismiss(id);
  }
}
