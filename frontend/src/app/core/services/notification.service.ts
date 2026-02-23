import { Injectable, signal } from '@angular/core';

export type NotificationType = 'success' | 'error' | 'warning' | 'info';

export interface Notification {
  id: string;
  type: NotificationType;
  message: string;
  timestamp: number;
}

const MAX_NOTIFICATIONS = 3;
const DEFAULT_DISMISS_MS = 4000;

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  readonly notifications = signal<Notification[]>([]);

  success(message: string, dismissAfter = DEFAULT_DISMISS_MS): void {
    this.add('success', message, dismissAfter);
  }

  error(message: string, dismissAfter = DEFAULT_DISMISS_MS): void {
    this.add('error', message, dismissAfter);
  }

  warning(message: string, dismissAfter = DEFAULT_DISMISS_MS): void {
    this.add('warning', message, dismissAfter);
  }

  info(message: string, dismissAfter = DEFAULT_DISMISS_MS): void {
    this.add('info', message, dismissAfter);
  }

  dismiss(id: string): void {
    this.notifications.update(list => list.filter(n => n.id !== id));
  }

  private add(type: NotificationType, message: string, dismissAfter: number): void {
    const notification: Notification = {
      id: `${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
      type,
      message,
      timestamp: Date.now()
    };

    this.notifications.update(list => {
      const updated = [...list, notification];
      // Älteste entfernen wenn Maximum überschritten
      return updated.length > MAX_NOTIFICATIONS
        ? updated.slice(updated.length - MAX_NOTIFICATIONS)
        : updated;
    });

    setTimeout(() => this.dismiss(notification.id), dismissAfter);
  }
}
